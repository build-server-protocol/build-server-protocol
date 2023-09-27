package bsp.codegen.bsp4j

import bsp.codegen.Lines
import bsp.codegen.dsl.{block, empty, lines, newline}
import bsp.codegen.ir.Def._
import bsp.codegen.ir.EnumType.{IntEnum, StringEnum}
import bsp.codegen.ir.Hint.Deprecated
import bsp.codegen.ir.JsonRPCMethodType.{Notification, Request}
import bsp.codegen.ir.Primitive._
import bsp.codegen.ir.Type._
import bsp.codegen.ir._
import cats.implicits.toFoldableOps
import org.jetbrains.bsp.generators.{CodegenFile, Loader}
import os.RelPath
import software.amazon.smithy.model.shapes.ShapeId

import java.nio.file.Path

class JavaRenderer(basepkg: String, definitions: List[Def], version: String) {
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

  def renderDef(definition: Def): Option[CodegenFile] = {
    definition match {
      case Alias(shapeId, tpe, _)           => None
      case Structure(shapeId, fields, _, _) => Some(renderStructure(shapeId, fields))
      case ClosedEnum(shapeId, enumType, values, _) =>
        Some(renderClosedEnum(shapeId, enumType, values))
      case OpenEnum(shapeId, enumType, values, _) => Some(renderOpenEnum(shapeId, enumType, values))
      case Service(shapeId, operations, _)        => Some(renderService(shapeId, operations))
    }
  }

  def renderStructure(shapeId: ShapeId, fields: List[Field]): CodegenFile = {
    val requiredFields = fields.filter(_.required)

    val allLines = lines(
      renderPkg(shapeId),
      newline,
      "import org.eclipse.lsp4j.generator.JsonRpcData",
      renderImports(fields),
      newline,
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

  def spreadEnumLines[A](enumType: EnumType[A], values: List[EnumValue[A]]): Lines = {
    val renderedValues = values.map(renderEnumValueDef(enumType))
    renderedValues.init.map(_ + ",") :+ (renderedValues.last + ";")
  }

  def renderClosedEnum[A](
      shapeId: ShapeId,
      enumType: EnumType[A],
      values: List[EnumValue[A]]
  ): CodegenFile = {
    val evt = enumValueType(enumType)
    val tpe = shapeId.getName()
    val allLines = lines(
      renderPkg(shapeId).map(_ + ";"),
      newline,
      "import com.google.gson.annotations.JsonAdapter;",
      "import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;",
      newline,
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
      enumType: EnumType[A],
      values: List[EnumValue[A]]
  ): CodegenFile = {
    val _evt = enumValueType(enumType)
    val tpe = shapeId.getName()
    val allLines = lines(
      renderPkg(shapeId).map(_ + ";"),
      newline,
      block(s"public class $tpe") {
        values.map(renderStaticValue(enumType))
      },
      newline
    )
    val fileName = shapeId.getName() + ".java"
    new CodegenFile(baseRelPath.resolve(fileName), allLines.render)
  }

  def renderService(shapeId: ShapeId, operations: List[Operation]): CodegenFile = {
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

  def renderOperation(operation: Operation): Lines = {
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
    val method = operation.name.head.toLower + operation.name.tail
    lines(
      maybeDeprecated,
      rpcAnnotation,
      s"$output $method($input);",
      newline
    )
  }

  def enumValueType[A](enumType: EnumType[A]): String = enumType match {
    case IntEnum    => "int"
    case StringEnum => "String"
  }

  def renderStaticValue[A](enumType: EnumType[A]): EnumValue[A] => String = {
    enumType match {
      case IntEnum =>
        (ev: EnumValue[Int]) => s"""public static final int ${ev.name} = ${ev.value};"""
      case StringEnum =>
        (ev: EnumValue[String]) => s"""public static final String ${ev.name} = "${ev.value}";"""
    }
  }

  def renderEnumValueDef[A](enumType: EnumType[A]): EnumValue[A] => String = {
    enumType match {
      case IntEnum    => (ev: EnumValue[Int]) => s"${ev.name}(${ev.value})"
      case StringEnum => (ev: EnumValue[String]) => s"""${ev.name}("${ev.value}")"""
    }
  }

  def renderPkg(shapeId: ShapeId): Lines = lines(
    s"package $basepkg"
  )

  def renderImports(fields: List[Field]): Lines =
    fields.foldMap(renderImport).distinct.sorted

  def renderImportFromType(tpe: Type): Lines = tpe match {
    case TRef(shapeId) => empty // assuming everything is generated in the same package
    case TMap(key, value) =>
      lines(s"import java.util.Map") ++ renderImportFromType(key) ++ renderImportFromType(value)
    case TCollection(member) =>
      lines(s"import java.util.List") ++ renderImportFromType(member)
    case TSet(member) =>
      lines(s"import java.util.Set") ++ renderImportFromType(member)
    case TUntaggedUnion(tpes) => tpes.foldMap(renderImportFromType)
    case TPrimitive(prim, _)  => empty
  }

  def renderImport(field: Field): Lines = {
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

  def useJsonAdapter(field: Field): Boolean = {
    field.tpe match {
      case Type.TPrimitive(Primitive.PDocument, _) => true
      case _                                       => false
    }
  }

  def renderJavaField(field: Field): Lines = {
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

  def renderParam(field: Field): String = {
    val decl = renderFieldRaw(field)
    if (field.required) {
      s"@NonNull $decl"
    } else decl
  }

  def renderFieldRaw(field: Field): String = {
    s"${renderType(field.tpe)} ${field.name}"
  }

  def renderType(tpe: Type): String = tpe match {
    case TRef(shapeId)        => shapeId.getName()
    case TPrimitive(prim, _)  => renderPrimitive(prim)
    case TMap(key, value)     => s"Map<${renderType(key)}, ${renderType(value)}>"
    case TCollection(member)  => s"List<${renderType(member)}>"
    case TSet(member)         => s"Set<${renderType(member)}>"
    case TUntaggedUnion(tpes) => renderType(tpes.head) // Todo what does bsp4j do ?
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
