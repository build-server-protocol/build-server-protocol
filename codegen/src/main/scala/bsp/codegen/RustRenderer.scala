package bsp.codegen

import bsp.codegen.Def._
import bsp.codegen.Primitive._
import bsp.codegen.Type._
import cats.syntax.all._
import software.amazon.smithy.model.shapes.ShapeId
import dsl._
import bsp.codegen.EnumType.IntEnum
import bsp.codegen.EnumType.StringEnum
import bsp.codegen.JsonRPCMethodType.Notification
import bsp.codegen.JsonRPCMethodType.Request
import bsp.codegen.RustRenderer.camelToSnakeCase

object RustRenderer {
  // Convert a camelCase string to snake_case
  def camelToSnakeCase(name: String): String = {
    val sb = new StringBuilder
    for (c <- name) {
      if (c.isUpper) {
        sb.append('_')
        sb.append(c.toLower)
      } else {
        sb.append(c)
      }
    }
    sb.toString
  }
}

class RustRenderer(basepkg: String) {

  val baseRelPath = os.rel / basepkg.split('.')
  // scalafmt: { maxColumn = 120}
  def render(definition: Def): CodegenFile = {
    definition match {
      case Structure(shapeId, fields, _)            => renderStructure(shapeId, fields)
      case ClosedEnum(shapeId, enumType, values, _) => renderClosedEnum(shapeId, enumType, values)
      case OpenEnum(shapeId, enumType, values, _)   => renderOpenEnum(shapeId, enumType, values)
      case Service(shapeId, operations, _)          => renderService(shapeId, operations)
    }
  }

  def renderStructure(shapeId: ShapeId, fields: List[Field]): CodegenFile = {
    val allLines = lines(
      "use super::*;",
      renderImports(fields),
      newline,
      "#[derive(Debug, Eq, PartialEq, Clone, Deserialize, Serialize)]",
      "#[serde(rename_all = \"camelCase\")]",
      block(s"pub struct ${shapeId.getName()}")(
        lines(fields.map(renderRustField)),
      )
    )

    val fileName = shapeId.getName() + ".rs"
    CodegenFile(shapeId, baseRelPath / fileName, allLines.render)
  }

  def renderClosedEnum[A](shapeId: ShapeId, enumType: EnumType[A], values: List[EnumValue[A]]): CodegenFile = {
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
        values.map(renderEnumValueDef(enumType)),
        newline,
        s"private final $evt value;",
        block(s"$tpe($evt value)") {
          "this.value = value;"
        },
        newline,
        block(s"public $evt getValue())") {
          "return value;"
        },
        newline,
        block(s"public static $tpe forValue (${evt} value))")(
          s"$tpe[] allValues = $tpe.values();",
          "if (value < 1 || value > allValues.length)",
          lines("""throw new IllegalArgumentException("Illegal enum value: " + value);""").indent,
          "return allValues[value - 1],;"
        )
      )
    )
    val fileName = shapeId.getName() + ".rs"
    CodegenFile(shapeId, baseRelPath / fileName, allLines.render)
  }

  def renderOpenEnum[A](shapeId: ShapeId, enumType: EnumType[A], values: List[EnumValue[A]]): CodegenFile = {
    val evt = enumValueType(enumType)
    val tpe = shapeId.getName()
    val allLines = lines(
      renderPkg(shapeId).map(_ + ";"),
      newline,
      block(s"public class $tpe") {
        values.map(renderStaticValue(enumType))
      },
      newline
    )
    val fileName = shapeId.getName() + ".rs"
    CodegenFile(shapeId, baseRelPath / fileName, allLines.render)
  }

  def renderService(shapeId: ShapeId, operations: List[Operation]): CodegenFile = {
    val allLines = lines(
      renderPkg(shapeId).map(_ + ";"),
      newline,
        operations.foldMap(renderOperation),
      newline
    )
    val fileName = shapeId.getName() + ".rs"
    CodegenFile(shapeId, baseRelPath / fileName, allLines.render)
  }

  def renderOperation(operation: Operation): Lines = {
    operation.jsonRPCMethodType match {
      case Notification => {
        val input = renderType(operation.inputType)

        lines(
          "#[derive(Debug)]",
          s"pub enum ${operation.name} {}",
          newline,
          block(s"impl Notification for ${operation.name})")(
            s"type Params = $input;",
            s"""const METHOD: &'static str = "${operation.jsonRPCMethod}";"""
          )
        )
      }
      case Request => {
        val input = renderType(operation.inputType)
        val output = renderType(operation.outputType)

        lines(
          "#[derive(Debug)]",
          s"pub enum ${operation.name} {}",
          newline,
          block(s"impl Request for ${operation.name})")(
            s"type Params = $input;",
            s"type Result = $output;",
            s"""const METHOD: &'static str = "${operation.jsonRPCMethod}";"""
          )
        )

      }
    }
  }

  def enumValueType[A](enumType: EnumType[A]): String = enumType match {
    case IntEnum    => "int"
    case StringEnum => "String"
  }

  def renderStaticValue[A](enumType: EnumType[A]): EnumValue[A] => String = {
    enumType match {
      case IntEnum    => (ev: EnumValue[Int]) => s"""public static final int ${ev.name} = ${ev.value}"""
      case StringEnum => (ev: EnumValue[String]) => s"""public static final String ${ev.name} = "${ev.value}""""
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
    fields.map(_.tpe).foldMap(renderImport).distinct.sorted

  def renderImport(tpe: Type): Lines = tpe match {
    case TRef(shapeId) => empty // assuming everything is generated in the same package
    case TMap(key, value) =>
      lines(s"use std::collections::HashMap;") ++ renderImport(key) ++ renderImport(value)
    case TCollection(member) => renderImport(member)
    case TUntaggedUnion(tpes) => tpes.foldMap(renderImport)
    case TPrimitive(_, Some(shape))     => empty
  }

  def renderRustField(field: Field): Lines = {
    val maybeSkipSerializing = if (field.required) empty else lines("#[serde(skip_serializing_if = \"Option::is_none\")]")
    lines(
        maybeSkipSerializing,
        s"pub ${renderFieldRaw(field)},",
    )
  }

  def renderParam(field: Field): String = {
    renderFieldRaw(field)
  }

  def renderFieldRaw(field: Field): String = {
    val name = camelToSnakeCase(field.name)
    val tpe = if (field.required) renderType(field.tpe) else s"Option<${renderType(field.tpe)}>"
    s"$name: $tpe"
  }

  def renderType(tpe: Type): String = tpe match {
    case TRef(shapeId)        => shapeId.getName()
    case TPrimitive(prim)     => renderPrimitive(prim)
    case TMap(key, value)     => s"HashMap<${renderType(key)}, ${renderType(value)}>"
    case TCollection(member)  => s"Vec<${renderType(member)}>"
    case TUntaggedUnion(tpes) => renderType(tpes.head) // Todo what does bsp4j do ?
  }

  def renderPrimitive(prim: Primitive) = prim match {
    case PFloat     => "f32"
    case PDouble    => "f64"
    case PUnit      => "()"
    case PString    => "String"
    case PInt       => "i32"
    case PDocument  => "serde_json::Map<String, serde_json::Value>"
    case PBool      => "bool"
    case PLong      => "i64"
    case PTimestamp => "u64"
  }

}
