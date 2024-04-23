package bsp.codegen.bsp4j

import bsp.codegen.Lines
import bsp.codegen.common.{CodegenFile, Loader, ir}
import bsp.codegen.dsl.{block, empty, lines, newline}
import bsp.codegen.common.ir.Def._
import bsp.codegen.common.ir.EnumType.{IntEnum, StringEnum}
import bsp.codegen.ir.Hint.{Deprecated, Documentation, Unstable}
import bsp.codegen.ir.JsonRPCMethodType.{Notification, Request}
import bsp.codegen.ir.Primitive._
import bsp.codegen.common.ir.Type._
import bsp.codegen.ir._
import cats.implicits.toFoldableOps
import os.RelPath
import software.amazon.smithy.model.shapes.ShapeId

import java.nio.file.Path

class JavaRenderer(basepkg: String, definitions: List[ir.Def], version: String) {
  import bsp.codegen.Settings.java

  val baseRelPath: Path = Path.of(basepkg.replace(".", "/"))

  def render(): List[CodegenFile] = {
    definitions.flatMap(renderDef) ++ List(renderVersion(), copyPreconditions())
  }

  def copyPreconditions(): CodegenFile = {
    val preconditionsContents = Loader.INSTANCE.readResource("Preconditions.java")
    val preconditionsPath = Path.of("org", "eclipse", "lsp4j", "util", "Preconditions.java")
    // For some reason extend expects this file to be present in this specific location,
    // it can be removed once we stop using extend
    new CodegenFile(preconditionsPath, preconditionsContents)
  }

  def renderVersion(): CodegenFile = {
    val contents = lines(
      s"package $basepkg;",
      newline,
      block("public class Bsp4j")(
        s"""public static final String PROTOCOL_VERSION = new String("$version");"""
      ),
      newline
    )

    new CodegenFile(baseRelPath.resolve("Bsp4j.java"), contents.render)
  }

  def renderDef(definition: ir.Def): Option[CodegenFile] = {
    definition match {
      case Alias(shapeId, tpe, _)               => None
      case Structure(shapeId, fields, hints, _) => Some(renderStructure(shapeId, fields, hints))
      case ClosedEnum(shapeId, enumType, values, hints) =>
        Some(renderClosedEnum(shapeId, enumType, values, hints))
      case OpenEnum(shapeId, enumType, values, hints) =>
        Some(renderOpenEnum(shapeId, enumType, values, hints))
      case Service(shapeId, operations, _) => Some(renderService(shapeId, operations))
    }
  }

  def renderStructure(
      shapeId: ShapeId,
      fields: List[ir.Field],
      hints: List[ir.Hint]
  ): CodegenFile = {
    val requiredFields = fields.filter(_.required)
    val docsLines = renderDocs(hints)

    val allLines = lines(
      renderPkg(shapeId),
      newline,
      "import org.eclipse.lsp4j.generator.JsonRpcData",
      renderImports(fields),
      newline,
      docsLines,
      "@JsonRpcData",
      block(s"class ${shapeId.getName}")(
        lines(fields.map(renderJavaField)),
        newline, {
          val params = requiredFields.map(renderParam).mkString(", ")
          val assignments = requiredFields.map(_.name).map(n => s"this.$n = $n")
          block(s"new($params)")(assignments)
        }
      ),
      newline
    )

    val fileName = shapeId.getName + ".xtend"
    new CodegenFile(baseRelPath.resolve(fileName), allLines.render)
  }

  def spreadEnumLines[A](enumType: ir.EnumType[A], values: List[ir.EnumValue[A]]): Lines = {
    val renderedValues = values.map(renderEnumValueDef(enumType))
    renderedValues.init.map(_ + ",") :+ (renderedValues.last + ";")
  }

  def renderDocs(hints: List[ir.Hint]): Lines = {
    val isUnstable = hints.contains(Unstable)
    val unstableNote = if (isUnstable) {
      List("**Unstable** (may change in future versions)")
    } else {
      List.empty
    }
    val docs = unstableNote ++
      hints.collect { case Documentation(string) =>
        string.split(System.lineSeparator()).toList
      }.flatten
    docs match {
      case Nil => empty
      case _ =>
        lines(
          "/**",
          docs.map(line => s" * $line"),
          " */"
        )
    }
  }

  def renderClosedEnum[A](
      shapeId: ShapeId,
      enumType: ir.EnumType[A],
      values: List[ir.EnumValue[A]],
      hints: List[ir.Hint]
  ): CodegenFile = {
    val evt = enumValueType(enumType)
    val tpe = shapeId.getName()
    val docsLines = renderDocs(hints)
    val allLines = lines(
      renderPkg(shapeId).map(_ + ";"),
      newline,
      "import com.google.gson.annotations.JsonAdapter;",
      "import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;",
      newline,
      docsLines,
      "@JsonAdapter(EnumTypeAdapter.Factory.class)",
      block(s"public enum $tpe")(
        newline,
        spreadEnumLines(enumType, values),
        newline,
        s"private final $evt value;",
        newline,
        block(s"$tpe($evt value)") {
          "this.value = value;"
        },
        newline,
        block(s"public $evt getValue()") {
          "return value;"
        },
        newline,
        block(s"public static $tpe forValue($evt value)")(
          s"$tpe[] allValues = $tpe.values();",
          "if (value < 1 || value > allValues.length)",
          lines("""throw new IllegalArgumentException("Illegal enum value: " + value);""").indent,
          "return allValues[value - 1];"
        )
      ),
      newline
    )
    val fileName = shapeId.getName() + ".java"
    new CodegenFile(baseRelPath.resolve(fileName), allLines.render)
  }

  def renderOpenEnum[A](
      shapeId: ShapeId,
      enumType: ir.EnumType[A],
      values: List[ir.EnumValue[A]],
      hints: List[ir.Hint]
  ): CodegenFile = {
    val tpe = shapeId.getName()
    val docsLines = renderDocs(hints)
    val allLines = lines(
      renderPkg(shapeId).map(_ + ";"),
      newline,
      docsLines,
      block(s"public class $tpe") {
        values.map(renderStaticValue(enumType))
      },
      newline
    )
    val fileName = shapeId.getName() + ".java"
    new CodegenFile(baseRelPath.resolve(fileName), allLines.render)
  }

  def renderService(shapeId: ShapeId, operations: List[ir.Operation]): CodegenFile = {
    val allLines = lines(
      renderPkg(shapeId).map(_ + ";"),
      newline,
      "import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;",
      "import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;",
      newline,
      "import java.util.concurrent.CompletableFuture;",
      newline,
      block(s"public interface ${shapeId.getName()}")(
        operations.foldMap(renderOperation),
        newline
      ),
      newline
    )
    val fileName = shapeId.getName() + ".java"
    new CodegenFile(baseRelPath.resolve(fileName), allLines.render)
  }

  def renderOperation(operation: ir.Operation): Lines = {
    val output = (operation.jsonRPCMethodType, operation.outputType) match {
      case (Notification, _)                         => "void"
      case (Request, TPrimitive(Primitive.PUnit, _)) => s"CompletableFuture<Object>"
      case (Request, other)                          => s"CompletableFuture<${renderType(other)}>"
    }
    val input = operation.inputType match {
      case TPrimitive(Primitive.PUnit, _) => ""
      case other                          => s"${renderType(other)} params"
    }
    val rpcMethod = operation.jsonRPCMethod
    val rpcAnnotation = operation.jsonRPCMethodType match {
      case Notification => s"""@JsonNotification("$rpcMethod")"""
      case Request      => s"""@JsonRequest("$rpcMethod")"""
    }
    val maybeDeprecated = operation.hints.collectFirst({ case Deprecated(_) => "@Deprecated" })
    val docsLines = renderDocs(operation.hints)
    val method = operation.name.head.toLower + operation.name.tail
    lines(
      docsLines,
      maybeDeprecated,
      rpcAnnotation,
      s"$output $method($input);",
      newline
    )
  }

  def enumValueType[A](enumType: ir.EnumType[A]): String = enumType match {
    case IntEnum    => "int"
    case StringEnum => "String"
  }

  def renderStaticValue[A](enumType: ir.EnumType[A]): ir.EnumValue[A] => String = {
    enumType match {
      case IntEnum =>
        (ev: ir.EnumValue[Int]) => s"""public static final int ${ev.name} = ${ev.value};"""
      case StringEnum =>
        (ev: ir.EnumValue[String]) => s"""public static final String ${ev.name} = "${ev.value}";"""
    }
  }

  def renderEnumValueDef[A](enumType: ir.EnumType[A]): ir.EnumValue[A] => String = {
    enumType match {
      case IntEnum    => (ev: ir.EnumValue[Int]) => s"${ev.name}(${ev.value})"
      case StringEnum => (ev: ir.EnumValue[String]) => s"""${ev.name}("${ev.value}")"""
    }
  }

  def renderPkg(shapeId: ShapeId): Lines = lines(
    s"package $basepkg"
  )

  def renderImports(fields: List[ir.Field]): Lines =
    fields.foldMap(renderImport).distinct.sorted

  def renderImportFromType(tpe: ir.Type): Lines = tpe match {
    case TRef(shapeId) => empty // assuming everything is generated in the same package
    case TMap(key, value) =>
      lines(s"import java.util.Map") ++ renderImportFromType(key) ++ renderImportFromType(value)
    case TCollection(member) =>
      lines(s"import java.util.List") ++ renderImportFromType(member)
    case TSet(member) =>
      lines(s"import java.util.Set") ++ renderImportFromType(member)
    case TUntaggedUnion(tpes) =>
      lines(s"import org.eclipse.lsp4j.jsonrpc.messages.Either") ++ tpes.foldMap(
        renderImportFromType
      )
    case TPrimitive(prim, _) => empty
  }

  def renderImport(field: ir.Field): Lines = {
    val renameAnnotation = if (field.jsonRename.isDefined) {
      lines(s"import com.google.gson.annotations.SerializedName")
    } else empty

    val importType = renderImportFromType(field.tpe)

    val jsonAdapter = if (useJsonAdapter(field)) {
      lines(
        s"import com.google.gson.annotations.JsonAdapter",
        s"import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter"
      )
    } else empty

    val nonNull = if (field.required) {
      lines(s"import org.eclipse.lsp4j.jsonrpc.validation.NonNull")
    } else empty

    renameAnnotation ++ importType ++ jsonAdapter ++ nonNull
  }

  def useJsonAdapter(field: ir.Field): Boolean = {
    field.tpe match {
      case ir.Type.TPrimitive(Primitive.PDocument, _) => true
      case _                                          => false
    }
  }

  def renderJavaField(field: ir.Field): Lines = {
    val maybeAdapter = if (useJsonAdapter(field)) {
      lines("@JsonAdapter(JsonElementTypeAdapter.Factory)")
    } else empty
    val maybeNonNull = if (field.required) lines("@NonNull") else empty
    val maybeRename =
      field.jsonRename.map(name => lines(s"""@SerializedName("$name")""")).getOrElse(empty)
    lines(
      maybeAdapter,
      maybeRename,
      maybeNonNull,
      renderFieldRaw(field)
    )
  }

  def renderParam(field: ir.Field): String = {
    val decl = renderFieldRaw(field)
    if (field.required) {
      s"@NonNull $decl"
    } else decl
  }

  def renderFieldRaw(field: ir.Field): String = {
    s"${renderType(field.tpe)} ${field.name}"
  }

  def renderType(tpe: ir.Type): String = tpe match {
    case TRef(shapeId)        => shapeId.getName()
    case TPrimitive(prim, _)  => renderPrimitive(prim)
    case TMap(key, value)     => s"Map<${renderType(key)}, ${renderType(value)}>"
    case TCollection(member)  => s"List<${renderType(member)}>"
    case TSet(member)         => s"Set<${renderType(member)}>"
    case TUntaggedUnion(tpes) => renderUntaggedUnion(tpes)
  }

  private def renderUntaggedUnion(types: List[ir.Type]): String = {
    if (types.size != 2)
      throw new Exception("Only unions of two types are supported")

    s"Either<${types.map(renderType).mkString(", ")}>"
  }

  def renderPrimitive(prim: Primitive): String = prim match {
    case PFloat     => "Float"
    case PDouble    => "Double"
    case PUnit      => "void"
    case PString    => "String"
    case PInt       => "Integer"
    case PDocument  => "Object"
    case PBool      => "Boolean"
    case PLong      => "Long"
    case PTimestamp => "Long"
  }

}
