package bsp.codegen.bsp4j

import bsp.codegen.common.CodegenFile
import bsp.codegen.common.Loader
import bsp.codegen.common.dsl.CodeBlock
import bsp.codegen.common.dsl.code
import bsp.codegen.common.dsl.emptyCode
import bsp.codegen.common.ir.Def
import bsp.codegen.common.ir.EnumType
import bsp.codegen.common.ir.EnumValue
import bsp.codegen.common.ir.Field
import bsp.codegen.common.ir.Hint
import bsp.codegen.common.ir.JsonRpcMethodType
import bsp.codegen.common.ir.Operation
import bsp.codegen.common.ir.Type
import bsp.codegen.common.util.kebabToScreamingSnakeCase
import kotlin.io.path.Path

class JavaRenderer(val basepkg: String, val definitions: List<Def>, val version: String) {
  val baseRelPath = Path(basepkg.replace(".", "/"))

  fun render(): List<CodegenFile> {
    val files = definitions.mapNotNull { renderDef(it) }
    val versionFile = renderVersion()
    return listOf(versionFile, copyPreconditions()) + files
  }

  fun copyPreconditions(): CodegenFile {
    val preconditionsContents = Loader.readResource("Preconditions.java")
    val preconditionsPath = Path("org", "eclipse", "lsp4j", "util", "Preconditions.java")
    return CodegenFile(preconditionsPath, preconditionsContents)
  }

  fun renderVersion(): CodegenFile {
    val contents =
        """
      package $basepkg;
      
      public class Bsp4j {
        public static final String PROTOCOL_VERSION = new String("$version");
      }
    """
            .trimIndent()

    return CodegenFile(baseRelPath.resolve("Bsp4j.java"), contents)
  }

  fun renderDef(definition: Def): CodegenFile? {
    return when (definition) {
      is Def.Alias -> null
      is Def.Structure -> renderStructure(definition)
      is Def.ClosedEnum<*> -> renderClosedEnum(definition)
      is Def.Service -> renderService(definition)
      is Def.OpenEnum<*> -> renderOpenEnum(definition)
      is Def.DataKinds -> renderData(definition)
      is Def.UntaggedUnion -> throw IllegalArgumentException("UntaggedUnion not supported")
    }
  }

  fun renderStructure(def: Def.Structure): CodegenFile {
    val fields = def.fields
    val hints = def.hints
    val shapeId = def.shapeId

    val requiredFields = fields.filter { it.required }
    val docsLines = renderDocs(hints)

    val allLines = code {
      -renderPkg()
      newline()
      -"import org.eclipse.lsp4j.generator.JsonRpcData"
      fields.forEach { field -> include(renderImport(field)) }
      newline()
      include(docsLines)
      -"@JsonRpcData"
      block("class ${shapeId.name}") {
        fields.forEach { field -> include(renderJavaField(field)) }
        newline()
        block("new(${requiredFields.map {renderParam(it)} .joinToString { it }})") {
          requiredFields.forEach { field -> -"this.${field.name} = ${field.name}" }
        }
      }
      newline()
    }

    val fileName = "${shapeId.name}.xtend"
    return CodegenFile(baseRelPath.resolve(fileName), allLines.toString())
  }

  fun spreadEnumLines(enumType: EnumType<*>, values: List<EnumValue<*>>): CodeBlock {
    val renderedValues = values.map { renderEnumValueDef(enumType)(it) }
    val initValues = code { renderedValues.dropLast(1).map { -"$it," } }
    val lastValue = renderedValues.last() + ";"
    return code {
      include(initValues)
      -lastValue
    }
  }

  fun renderDocs(hints: List<Hint>): CodeBlock {
    val isUnstable = hints.contains(Hint.Unstable)
    val unstableNote = "**Unstable** (may change in future versions)".takeIf { isUnstable }
    val docs =
        hints.filterIsInstance<Hint.Documentation>().flatMap {
          it.string.split(System.lineSeparator())
        }
    return if (docs.isEmpty()) {
      emptyCode
    } else {
      code {
        -"/**"
        -unstableNote
        docs.forEach { line -> -" * $line" }
        -" */"
      }
    }
  }

  fun renderClosedEnum(def: Def.ClosedEnum<*>): CodegenFile {
    val enumType = def.enumType
    val shapeId = def.shapeId
    val hints = def.hints
    val values = def.values

    val evt = enumValueType(enumType)
    val tpe = shapeId.name
    val docsLines = renderDocs(hints)
    val allLines = code {
      -renderPkg()
      newline()
      -"import com.google.gson.annotations.JsonAdapter;"
      -"import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;"
      newline()
      include(docsLines)
      -"@JsonAdapter(EnumTypeAdapter.Factory.class)"
      block("public enum $tpe") {
        newline()
        include(spreadEnumLines(enumType, values))
        newline()
        -"private final $evt value;"
        newline()
        block("$tpe($evt value)") { -"this.value = value;" }
        newline()
        block("public $evt getValue()") { -"return value;" }
        newline()
        block("public static $tpe forValue($evt value)") {
          -"$tpe[] allValues = $tpe.values();"
          -"if (value < 1 || value > allValues.length)"
          -"""throw new IllegalArgumentException("Illegal enum value: " + value);"""
          -"return allValues[value - 1];"
        }
      }
      newline()
    }
    val fileName = "$tpe.java"
    return CodegenFile(baseRelPath.resolve(fileName), allLines.toString())
  }

  fun renderOpenEnum(def: Def.OpenEnum<*>): CodegenFile {
    val enumType = def.enumType
    val shapeId = def.shapeId
    val hints = def.hints
    val values = def.values

    val tpe = shapeId.name
    val docsLines = renderDocs(hints)
    val allLines = code {
      -renderPkg()
      newline()
      include(docsLines)
      block("public class $tpe") { values.forEach { value -> -renderStaticValue(enumType)(value) } }
      newline()
    }
    val fileName = "$tpe.java"
    return CodegenFile(baseRelPath.resolve(fileName), allLines.toString())
  }

  fun renderService(def: Def.Service): CodegenFile {
    val allLines = code {
      -renderPkg()
      newline()
      -"import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;"
      -"import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;"
      newline()
      -"import java.util.concurrent.CompletableFuture;"
      newline()
      block("public interface ${def.shapeId.name}") {
        def.operations.forEach { operation -> include(renderOperation(operation)) }
        newline()
      }
      newline()
    }
    val fileName = "${def.shapeId.name}.java"
    return CodegenFile(baseRelPath.resolve(fileName), allLines.toString())
  }

  fun renderOperation(operation: Operation): CodeBlock {
    val output =
        when (operation.jsonRpcMethodType) {
          JsonRpcMethodType.Notification -> "void"
          JsonRpcMethodType.Request ->
              if (operation.outputType == Type.Unit) "CompletableFuture<Object>"
              else
                  "CompletableFuture<${
        renderType(
          operation.outputType
        )
      }>"
        }
    val input =
        when (operation.inputType) {
          Type.Unit -> ""
          else -> "${renderType(operation.inputType)} params"
        }
    val rpcMethod = operation.jsonRpcMethod
    val rpcAnnotation =
        when (operation.jsonRpcMethodType) {
          JsonRpcMethodType.Notification -> """@JsonNotification("$rpcMethod")"""
          JsonRpcMethodType.Request -> """@JsonRequest("$rpcMethod")"""
        }
    val maybeDeprecated =
        operation.hints.filterIsInstance<Hint.Deprecated>().firstOrNull()?.let { "@Deprecated" }
    val docsLines = renderDocs(operation.hints)
    val method = operation.name.replaceFirstChar { it.lowercase() }
    return code {
      include(docsLines)
      -maybeDeprecated
      -rpcAnnotation
      -"$output $method($input);"
      newline()
    }
  }

  fun enumValueType(enumType: EnumType<*>): String {
    return when (enumType) {
      EnumType.IntEnum -> "int"
      EnumType.StringEnum -> "String"
    }
  }

  fun renderStaticValue(enumType: EnumType<*>): (EnumValue<*>) -> String {
    return when (enumType) {
      EnumType.IntEnum -> { ev: EnumValue<*> ->
            """public static final int ${ev.name} = ${ev.value};"""
          }
      EnumType.StringEnum -> { ev: EnumValue<*> ->
            """public static final String ${ev.name} = "${ev.value}";"""
          }
    }
  }

  fun renderEnumValueDef(enumType: EnumType<*>): (EnumValue<*>) -> String {
    return when (enumType) {
      EnumType.IntEnum -> { ev: EnumValue<*> -> "${ev.name}(${ev.value})" }
      EnumType.StringEnum -> { ev: EnumValue<*> -> """${ev.name}("${ev.value}")""" }
    }
  }

  fun renderPkg(): String {
    return "package $basepkg;"
  }

  fun renderImportFromType(tpe: Type): CodeBlock {
    return when (tpe) {
      is Type.Map ->
          code {
            -"import java.util.Map"
            include(renderImportFromType(tpe.key))
            include(renderImportFromType(tpe.value))
          }
      is Type.List ->
          code {
            -"import java.util.List"
            include(renderImportFromType(tpe.member))
          }
      is Type.Set ->
          code {
            -"import java.util.Set"
            include(renderImportFromType(tpe.member))
          }
      is Type.UntaggedUnion ->
          code {
            -"import org.eclipse.lsp4j.jsonrpc.messages.Either"
            tpe.members.forEach { include(renderImportFromType(it)) }
          }
      else -> emptyCode
    }
  }

  fun renderImport(field: Field): CodeBlock {
    val renameAnnotation =
        field.hints.filterIsInstance<Hint.JsonRename>().firstOrNull()?.let {
          "import com.google.gson.annotations.SerializedName"
        }
    val importType = renderImportFromType(field.type)
    val jsonAdapter =
        if (useJsonAdapter(field)) {
          code {
            -"import com.google.gson.annotations.JsonAdapter"
            -"import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter"
          }
        } else emptyCode
    val nonNull = "import org.eclipse.lsp4j.jsonrpc.validation.NonNull".takeIf { field.required }
    return code {
      -renameAnnotation
      include(importType)
      include(jsonAdapter)
      -nonNull
    }
  }

  fun useJsonAdapter(field: Field): Boolean {
    return when (field.type) {
      is Type.Json -> true
      else -> false
    }
  }

  fun renderJavaField(field: Field): CodeBlock {
    val maybeAdapter =
        "@JsonAdapter(JsonElementTypeAdapter.Factory)".takeIf { useJsonAdapter(field) }
    val maybeNonNull = "@NonNull".takeIf { field.required }
    val maybeRename =
        field.hints.filterIsInstance<Hint.JsonRename>().firstOrNull()?.let {
          """@SerializedName("$it")"""
        }
    return code {
      -maybeAdapter
      -maybeRename
      -maybeNonNull
      -renderFieldRaw(field)
    }
  }

  fun renderParam(field: Field): String {
    val decl = renderFieldRaw(field)
    return if (field.required) {
      "@NonNull $decl"
    } else decl
  }

  fun renderFieldRaw(field: Field): String = "${renderType(field.type)} ${field.name}"

  fun renderType(type: Type): String =
      when (type) {
        Type.Bool -> "Boolean"
        Type.Int -> "Integer"
        Type.Json -> "Object"
        is Type.List -> "List<${renderType(type.member)}>"
        Type.Long -> "Long"
        is Type.Map -> "Map<${renderType(type.key)}, ${renderType(type.value)}>"
        is Type.Ref -> type.shapeId.name
        is Type.Set -> "Set<${renderType(type.member)}>"
        Type.String -> "String"
        Type.Unit -> "void"
        is Type.UntaggedUnion -> renderUntaggedUnion(type)
      }

  fun renderData(def: Def.DataKinds): CodegenFile {
    val values =
        def.kinds.map { polyData ->
          val snakeCased = polyData.kind.kebabToScreamingSnakeCase()
          EnumValue(snakeCased, polyData.kind, polyData.hints)
        }
    val dataKindDef = Def.OpenEnum(def.kindsEnumId, EnumType.StringEnum, values, def.hints)
    return renderOpenEnum(dataKindDef)
  }

  fun renderUntaggedUnion(def: Type.UntaggedUnion): String {
    if (def.members.size != 2) {
      throw IllegalArgumentException("UntaggedUnion must have exactly two members")
    }

    return "Either<${renderType(def.members[0])}, ${renderType(def.members[1])}>"
  }
}
