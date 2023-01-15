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
import bsp.codegen.Hint.Documentation

class TypescriptSpecRenderer(basepkg: String) {

  val baseRelPath = os.rel / basepkg.split('.')
  // scalafmt: { maxColumn = 120}
  def render(definition: Def): Option[CodegenFile] = {
    definition match {
      case Structure(shapeId, fields, hints)            => Some(renderStructure(shapeId, fields))
      case ClosedEnum(shapeId, enumType, values, hints) => None // no exemple of this in the spc
      case OpenEnum(shapeId, enumType, values, hints)   => Some(renderOpenEnum(shapeId, enumType, values))
      case Service(shapeId, operations, hints)          => None
    }
  }

  def renderStructure(shapeId: ShapeId, fields: List[Field]): CodegenFile = {
    val allLines = lines(
      block(s"export interface ${shapeId.getName()}")(
        lines(fields.map(renderTSField))
      )
    )

    val fileName = shapeId.getName() + ".ts"
    CodegenFile(shapeId, baseRelPath / fileName, allLines.render)
  }

  def renderOpenEnum[A](shapeId: ShapeId, enumType: EnumType[A], values: List[EnumValue[A]]): CodegenFile = {
    val tpe = shapeId.getName()
    val allLines = lines(
      block(s"export namespace $tpe") {
        values.map(renderStaticValue(enumType))
      },
      newline
    )
    val fileName = shapeId.getName() + ".ts"
    CodegenFile(shapeId, baseRelPath / fileName, allLines.render)
  }

  def renderStaticValue[A](enumType: EnumType[A]): EnumValue[A] => Lines = {
    enumType match {
      case IntEnum =>
        (ev: EnumValue[Int]) =>
          lines(
            renderDocumentation(ev.hints),
            s"export const ${camelCase(ev.name)} = ${ev.value};"
          )

      case StringEnum =>
        (ev: EnumValue[String]) =>
          lines(
            renderDocumentation(ev.hints),
            s"public static final String ${camelCase(ev.name)} = \"${ev.value}\""
          )
    }
  }

  def camelCase(string: String): String = {
    val first :: rest = string.split(Array(' ', '_')).toList.map(_.toLowerCase)
    val changedRest = rest.map(w => w.take(1).toUpperCase + w.drop(1))
    val reunited = first :: changedRest
    reunited.mkString
  }

  def renderEnumValueDef[A](enumType: EnumType[A]): EnumValue[A] => String = {
    enumType match {
      case IntEnum    => (ev: EnumValue[Int]) => s"${ev.name}(${ev.value})"
      case StringEnum => (ev: EnumValue[String]) => s"${ev.name}(\"${ev.value}\")"
    }
  }

  def renderTSField(field: Field): Lines = {
    val `:` = if (field.required) ":" else "?:"
    val maybeNonNull = if (field.required) lines("@NonNull") else empty
    lines(field.name + `:` + renderType(field.tpe) + ";")
  }

  def renderType(tpe: Type): String = tpe match {
    case TRef(shapeId)        => shapeId.getName()
    case TPrimitive(prim)     => renderPrimitive(prim)
    case TMap(key, value)     => ??? // Are maps even used in the BSP ?
    case TCollection(member)  => s"${renderType(member)}[]"
    case TUntaggedUnion(tpes) => tpes.map(renderType).mkString("|")
  }

  def renderPrimitive(prim: Primitive) = prim match {
    case PFloat     => "Float"
    case PDouble    => "Double"
    case PUnit      => "void"
    case PString    => "String"
    case PInt       => "Integer"
    case PDocument  => "any"
    case PBool      => "Boolean"
    case PLong      => "Long"
    case PTimestamp => "Long"
  }

  def renderDocumentation(hints: List[Hint]): Lines = hints
    .collectFold { case Documentation(string) =>
      val lines = string.split(System.lineSeparator())
      if (lines.nonEmpty) {
        lines(0) = "/** " + lines(0).head
        val lastIndex = lines.length - 1
        lines(lastIndex) = lines(lastIndex) + " */"
      }
      Lines(lines.toList)
    }

}
