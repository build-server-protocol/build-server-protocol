package bsp.codegen.bsp4s

import bsp.codegen.common.CodegenFile
import bsp.codegen.common.Loader
import bsp.codegen.common.dsl.CodeBlock
import bsp.codegen.common.dsl.Newline
import bsp.codegen.common.dsl.code
import bsp.codegen.common.dsl.emptyCode
import bsp.codegen.common.ir.Def
import bsp.codegen.common.ir.EnumType
import bsp.codegen.common.ir.EnumValue
import bsp.codegen.common.ir.Field
import bsp.codegen.common.ir.Hint
import bsp.codegen.common.ir.Operation
import bsp.codegen.common.ir.Type
import bsp.codegen.common.util.kebabToScreamingSnakeCase
import bsp.codegen.common.util.snakeToUpperCamelCase
import java.util.Locale
import kotlin.io.path.Path
import software.amazon.smithy.model.shapes.ShapeId

class ScalaRenderer(val basepkg: String, val definitions: List<Def>, val version: String) {
  val baseRelPath = Path(basepkg.replace(".", "/"))

  fun render(): List<CodegenFile> {
    return listOf(renderEndpoints(), renderDefinitions(), copyCustomCodec())
  }

  fun copyCustomCodec(): CodegenFile {
    val contents = Loader.readResource("CustomCodec.scala")
    val path = Path("ch", "epfl", "scala", "util", "CustomCodec.scala")
    return CodegenFile(path, contents)
  }

  fun renderDefinitions(): CodegenFile {
    val filePath = baseRelPath.resolve("Bsp.scala")
    val renderedDefinitions =
        definitions.mapNotNull { definition ->
          when (definition) {
            is Def.Alias -> null
            is Def.Structure -> renderStructure(definition)
            is Def.ClosedEnum<*> -> renderClosedEnum(definition)
            is Def.OpenEnum<*> -> renderOpenEnum(definition)
            is Def.Service -> null
            is Def.DataKinds -> renderData(definition)
            is Def.UntaggedUnion -> throw IllegalArgumentException("UntaggedUnion not supported")
          }
        }
    val contents = code {
      -"package $basepkg"
      newline()
      -"import ch.epfl.scala.util.CustomCodec"
      newline()
      -"import java.net.{URI, URISyntaxException}"
      newline()
      -"import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec"
      -"import com.github.plokhotnyuk.jsoniter_scala.macros.named"
      -"import com.github.plokhotnyuk.jsoniter_scala.core.JsonCodec"
      -"import com.github.plokhotnyuk.jsoniter_scala.core.JsonWriter"
      -"import com.github.plokhotnyuk.jsoniter_scala.core.JsonReader"
      -"import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker"
      -"import jsonrpc4s.RawJson"
      newline()
      block("object Bsp4s") { -"""val ProtocolVersion: String = "$version"""" }
      newline()
      block("final case class Uri private[Uri] (val value: String)") {
        -"def toPath: java.nio.file.Path ="
        -"  java.nio.file.Paths.get(new java.net.URI(value))"
      }
      newline()
      block("object Uri") {
        -"// This is the only valid way to create a URI"
        -"def apply(u: URI): Uri = Uri(u.toString)"
        newline()
        block("implicit val uriCodec: JsonValueCodec[Uri] = new JsonValueCodec[Uri]") {
          -"def nullValue: Uri = null"
          -"def encodeValue(id: Uri, out: JsonWriter): Unit = out.writeVal(id.value)"
          block("def decodeValue(in: JsonReader, default: Uri): Uri = ") {
            -"val defaultStr = if (default == null) null else default.value"
            -"val str = in.readString(defaultStr)"
            -"try Uri(URI.create(str).toString)"
            block("catch") {
              -"  case _: IllegalArgumentException | _: URISyntaxException =>"
              -"    in.decodeError(s\"String \$str is not a valid URI!\")"
            }
          }
        }
      }
      newline()
      elements(renderedDefinitions, Newline)
    }
    return CodegenFile(filePath, contents.toString())
  }

  fun renderEndpoints(): CodegenFile {
    val endpointsPath = baseRelPath.resolve("endpoints").resolve("Endpoints.scala")
    val operations =
        definitions
            .mapNotNull { definition ->
              when (definition) {
                is Def.Service -> definition.operations
                else -> null
              }
            }
            .flatten()

    val renderedOperations = renderOperations(operations)
    val contents = code {
      -"package $basepkg"
      -"package endpoints"
      newline()
      -"import jsonrpc4s.Endpoint"
      -"import jsonrpc4s.Endpoint.unitCodec"
      newline()
      include(renderedOperations)
    }
    return CodegenFile(endpointsPath, contents.toString())
  }

  fun renderOperations(operations: List<Operation>): CodeBlock {
    val groupedByTargetName = operations.groupBy { it.jsonRpcMethod.split("/")[0] }
    val fragments =
        groupedByTargetName.map { (targetName, operations) ->
          val targetNameCapitalized =
              targetName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
              }
          code {
            -"object $targetNameCapitalized extends $targetNameCapitalized"
            newline()
            block("trait $targetNameCapitalized") {
              elements(operations.map { renderOperation(it) }, Newline)
            }
          }
        }
    return code { elements(fragments, Newline) }
  }

  fun renderOperation(operation: Operation): CodeBlock {
    val name = operation.jsonRpcMethod.split("/")[1]
    val output = renderType(operation.outputType)
    val input = renderType(operation.inputType)
    val maybeDeprecated =
        operation.hints.filterIsInstance<Hint.Deprecated>().firstOrNull()?.let {
          if (it.message.isEmpty()) "@deprecated" else """@deprecated("${it.message}")"""
        }
    val docsLines = renderDocs(operation.hints)
    return code {
      include(docsLines)
      -maybeDeprecated
      -"""object $name extends Endpoint[$input, $output]("${operation.jsonRpcMethod}")"""
    }
  }

  fun renderDocs(hints: List<Hint>): CodeBlock {
    val isUnstable = hints.contains(Hint.Unstable)
    val unstableNote = " * **Unstable** (may change in future versions)".takeIf { isUnstable }
    val docs =
        hints.filterIsInstance<Hint.Documentation>().flatMap {
          it.string.split(System.lineSeparator())
        }
    return if (docs.isEmpty() && !isUnstable) {
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

  fun renderStructure(def: Def.Structure): CodeBlock {
    val fields = def.fields
    val hints = def.hints
    val shapeId = def.shapeId
    val docsLines = renderDocs(hints)
    return code {
      include(docsLines)
      paren("final case class ${shapeId.name}") {
        fields.map { renderScalaField(it) }.joinToString(separator = ",\n").lines().forEach { -it }
      }
      newline()
      block("object ${shapeId.name}") {
        -"implicit val codec: JsonValueCodec[${shapeId.name}] = JsonCodecMaker.makeWithRequiredCollectionFields"
        if (fields.any { it.type is Type.UntaggedUnion }) {
          -"implicit val codecForEither: JsonValueCodec[Either[String, Int]] = CustomCodec.forEitherStringInt"
        }
      }
      newline()
    }
  }

  fun enumValueType(enumType: EnumType<*>): String {
    return when (enumType) {
      EnumType.IntEnum -> "Int"
      EnumType.StringEnum -> "String"
    }
  }

  fun renderClosedEnum(def: Def.ClosedEnum<*>): CodeBlock {
    val enumType = def.enumType
    val shapeId = def.shapeId
    val hints = def.hints
    val values = def.values
    val valueType = enumValueType(enumType)
    val enumName = shapeId.name
    val docsLines = renderDocs(hints)
    return code {
      include(docsLines)
      -"sealed abstract class $enumName(val value: $valueType)"
      block("object $enumName") {
        values.map { renderEnumValueDef(enumType, shapeId)(it) }.forEach { -it }
        newline()
        block("implicit val codec: JsonValueCodec[$enumName] = new JsonValueCodec[$enumName]") {
          -"def nullValue: $enumName = null"
          -"def encodeValue(msg: $enumName, out: JsonWriter): Unit = out.writeVal(msg.value)"
          block("def decodeValue(in: JsonReader, default: $enumName): $enumName = ") {
            block("in.readInt() match ") {
              values
                  .map { ev -> "case ${ev.value} => ${ev.name.snakeToUpperCamelCase()}" }
                  .forEach { -it }
              -"case n => in.decodeError(s\"Unknown message type id for \$n\")"
            }
          }
        }
      }
    }
  }

  fun renderOpenEnum(def: Def.OpenEnum<*>): CodeBlock {
    val enumType = def.enumType
    val shapeId = def.shapeId
    val hints = def.hints
    val values = def.values
    val tpe = shapeId.name
    val docsLines = renderDocs(hints)
    return code {
      include(docsLines)
      block("object $tpe") { values.map { renderStaticValue(enumType)(it) }.forEach { -it } }
      newline()
    }
  }

  fun renderStaticValue(enumType: EnumType<*>): (EnumValue<*>) -> String {
    return when (enumType) {
      EnumType.IntEnum -> { ev: EnumValue<*> ->
            """val ${ev.name.snakeToUpperCamelCase()} = ${ev.value}"""
          }
      EnumType.StringEnum -> { ev: EnumValue<*> ->
            """val ${ev.name.snakeToUpperCamelCase()} = "${ev.value}""""
          }
    }
  }

  fun renderEnumValueDef(enumType: EnumType<*>, shapeId: ShapeId): (EnumValue<*>) -> String {
    return when (enumType) {
      EnumType.IntEnum -> { ev: EnumValue<*> ->
            """case object ${ev.name.snakeToUpperCamelCase()} extends ${shapeId.name}(${ev.value})"""
          }
      EnumType.StringEnum -> { ev: EnumValue<*> ->
            """case object ${ev.name.snakeToUpperCamelCase()} extends ${shapeId.name}("${ev.value}")"""
          }
    }
  }

  fun renderData(def: Def.DataKinds): CodeBlock {
    val values =
        def.kinds.map { polyData ->
          EnumValue(polyData.kind.kebabToScreamingSnakeCase(), polyData.kind, polyData.hints)
        }
    val dataKindDef = Def.OpenEnum(def.kindsEnumId, EnumType.StringEnum, values, def.hints)
    return renderOpenEnum(dataKindDef)
  }

  fun renderScalaField(field: Field): CodeBlock {
    val maybeRename =
        field.hints.filterIsInstance<Hint.JsonRename>().firstOrNull()?.let {
          """@named("${it.name}")"""
        }
    val tpe = if (field.required) renderType(field.type) else "Option[${renderType(field.type)}]"
    val name = if (field.name == "type") "`type`" else field.name
    return code {
      -maybeRename
      -"$name: $tpe"
    }
  }

  fun renderType(type: Type): String =
      when (type) {
        Type.Bool -> "Boolean"
        Type.Int -> "Int"
        Type.Json -> "RawJson"
        is Type.List -> "List[${renderType(type.member)}]"
        Type.Long -> "Long"
        is Type.Map -> "Map[${renderType(type.key)}, ${renderType(type.value)}]"
        is Type.Ref ->
            if (type.shapeId == ShapeId.fromParts("bsp", "URI")) "Uri" else type.shapeId.name
        is Type.Set -> "Set[${renderType(type.member)}]"
        Type.String -> "String"
        Type.Unit -> "Unit"
        is Type.UntaggedUnion -> renderUntaggedUnion(type)
      }

  fun renderUntaggedUnion(def: Type.UntaggedUnion): String {
    if (def.members.size != 2) {
      throw IllegalArgumentException("UntaggedUnion must have exactly two members")
    }
    return "Either[${renderType(def.members[0])}, ${renderType(def.members[1])}]"
  }
}
