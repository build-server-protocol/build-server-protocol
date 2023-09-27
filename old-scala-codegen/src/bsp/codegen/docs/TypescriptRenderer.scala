package bsp.codegen.docs

import bsp.codegen.Lines
import bsp.codegen.dsl._
import bsp.codegen.ir.Def._
import bsp.codegen.ir.EnumType.{IntEnum, StringEnum}
import bsp.codegen.ir.Hint.Documentation
import bsp.codegen.ir.Primitive._
import bsp.codegen.ir.Type._
import bsp.codegen.ir._
import cats.syntax.all._
import software.amazon.smithy.model.shapes.ShapeId

class TypescriptRenderer {
  import bsp.codegen.Settings.typescript

  def render(definition: Def): Option[Lines] = {
    definition match {
      case Alias(shapeId, primitiveType, hints) =>
        Some(renderAlias(shapeId, primitiveType))
      case Structure(shapeId, fields, hints, associatedDataKinds) =>
        Some(renderStructure(shapeId, fields))
      case ClosedEnum(shapeId, enumType, values, hints) =>
        Some(renderClosedEnum(shapeId, enumType, values))
      case OpenEnum(shapeId, enumType, values, hints) =>
        Some(renderOpenEnum(shapeId, enumType, values))
      case Service(shapeId, operations, hints) => None
    }
  }

  def renderAlias(id: ShapeId, aliased: Type): Lines = {
    val tpe = aliased match {
      case TPrimitive(prim, _) => renderPrimitive(prim)
      case _                   => renderType(aliased)
    }
    lines(
      s"export type ${id.getName()} = $tpe;"
    )
  }

  def renderStructure(shapeId: ShapeId, fields: List[Field]): Lines = {
    lines(
      block(s"export interface ${shapeId.getName()}")(
        lines(fields.map(f => renderTSField(f)).intercalate(newline))
      )
    )
  }

  def renderClosedEnum[A](id: ShapeId, enumType: EnumType[A], values: List[EnumValue[A]]): Lines = {
    val tpe = id.getName()
    lines(
      block(s"export enum $tpe") {
        values
          .map(value =>
            lines(
              renderDocumentation(value.hints),
              s"${renderStaticValue(enumType)(value)},"
            )
          )
          .intercalate(newline)
      }
    )
  }

  def renderEnumType[A](enumType: EnumType[A]): String = {
    enumType match {
      case IntEnum    => renderPrimitive(Primitive.PInt)
      case StringEnum => renderPrimitive(Primitive.PString)
    }
  }

  def renderOpenEnum[A](
      shapeId: ShapeId,
      enumType: EnumType[A],
      values: List[EnumValue[A]]
  ): Lines = {
    val tpe = shapeId.getName()
    lines(
      s"export type $tpe = ${renderEnumType(enumType)};",
      newline,
      block(s"export namespace $tpe") {
        values
          .map(value =>
            lines(
              renderDocumentation(value.hints),
              s"export const ${renderStaticValue(enumType)(value)};"
            )
          )
          .intercalate(newline)
      }
    )
  }

  def renderStaticValue[A](enumType: EnumType[A]): EnumValue[A] => String = {
    enumType match {
      case IntEnum =>
        (ev: EnumValue[Int]) => s"${camelCase(ev.name).capitalize} = ${ev.value}"

      case StringEnum =>
        (ev: EnumValue[String]) => s"""${camelCase(ev.name).capitalize} = "${ev.value}""""
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
      case StringEnum => (ev: EnumValue[String]) => s"""${ev.name}("${ev.value}")"""
    }
  }

  def renderTSField(field: Field): Lines = {
    val `:` = if (field.required) ": " else "?: "
    lines(
      renderDocumentation(field.hints),
      field.name + `:` + renderType(field.tpe) + ";"
    )
  }

  def renderType(tpe: Type): String = tpe match {
    case TRef(shapeId) => shapeId.getName()
    case TPrimitive(prim, shapeId) =>
      if (shapeId.getNamespace == "smithy.api") {
        renderPrimitive(prim)
      } else {
        shapeId.getName
      }

    case TMap(key, value)     => s"Map<${renderType(key)}, ${renderType(value)}>"
    case TCollection(member)  => s"${renderType(member)}[]"
    case TSet(member)         => s"Set<${renderType(member)}>"
    case TUntaggedUnion(tpes) => tpes.map(renderType).mkString("|")
  }

  def renderPrimitive(prim: Primitive) = prim match {
    case PFloat     => "number"
    case PDouble    => "number"
    case PUnit      => "void"
    case PString    => "string"
    case PInt       => "number"
    case PDocument  => "any"
    case PBool      => "boolean"
    case PLong      => "number"
    case PTimestamp => "number"
  }

  def renderDocumentation(hints: List[Hint]): Lines = hints
    .collectFold { case Documentation(string) =>
      val lines = string.split(System.lineSeparator())
      if (lines.nonEmpty) {
        lines(0) = "/** " + lines(0)
        val lastIndex = lines.length - 1
        for (i <- 1 to lastIndex) {
          lines(i) = " * " + lines(i)
        }
        lines(lastIndex) = lines(lastIndex) + " */"
      }
      Lines(lines.toList)
    }

}
