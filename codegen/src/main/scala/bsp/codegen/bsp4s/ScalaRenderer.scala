package bsp.codegen.bsp4s

import bsp.codegen._
import bsp.codegen.dsl.{block, empty, lines, newline, paren}
import bsp.codegen.ir.Def._
import bsp.codegen.ir.EnumType.{IntEnum, StringEnum}
import bsp.codegen.ir.Primitive._
import bsp.codegen.ir.Type._
import bsp.codegen.ir._
import cats.implicits.toFoldableOps
import os.RelPath
import software.amazon.smithy.model.shapes.ShapeId

class ScalaRenderer(basepkg: String, definitions: List[Def]) {

  val baseRelPath: RelPath = os.rel / basepkg.split('.')

  def renderDefinitions(): Lines = {
    val renderedDefinitions = lines(definitions.map {
      case PrimitiveAlias(shapeId, tpe, _)  => Lines.empty
      case Structure(shapeId, fields, _, _) => renderStructure(shapeId, fields)
      case ClosedEnum(shapeId, enumType, values, _) =>
        renderClosedEnum(shapeId, enumType, values)
      case OpenEnum(shapeId, enumType, values, _) => renderOpenEnum(shapeId, enumType, values)
      case Service(shapeId, operations, hints)    => Lines.empty
    })

    lines(
      "package ch.epfl.scala.bsp",
      newline,
      "import java.net.{URI, URISyntaxException}",
      newline,
      "import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec",
      "import com.github.plokhotnyuk.jsoniter_scala.macros.named",
      "import com.github.plokhotnyuk.jsoniter_scala.core.JsonCodec",
      "import com.github.plokhotnyuk.jsoniter_scala.core.JsonWriter",
      "import com.github.plokhotnyuk.jsoniter_scala.core.JsonReader",
      "import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker",
      "import jsonrpc4s.RawJson",
      // Special handling for URI
      """
        |final case class Uri private[Uri] (val value: String) {
        |  def toPath: java.nio.file.Path =
        |    java.nio.file.Paths.get(new java.net.URI(value))
        |}
        |
        |object Uri {
        |  // This is the only valid way to create a URI
        |  def apply(u: URI): Uri = Uri(u.toString)
        |
        |  implicit val uriCodec: JsonValueCodec[Uri] = new JsonValueCodec[Uri] {
        |    def nullValue: Uri = null
        |    def encodeValue(id: Uri, out: JsonWriter): Unit = out.writeVal(id.value)
        |    def decodeValue(in: JsonReader, default: Uri): Uri = {
        |      val defaultStr = if (default == null) null else default.value
        |      val str = in.readString(defaultStr)
        |      try Uri(URI.create(str).toString)
        |      catch {
        |        case _: IllegalArgumentException | _: URISyntaxException =>
        |          in.decodeError(s"String $str is not a valid URI!")
        |      }
        |    }
        |  }
        |}""".stripMargin,
      newline,
      renderedDefinitions
    )
  }

  def renderEndpoints(): Lines = {
    val operations = definitions.collect { case Service(shapeId, operations, _) =>
      operations
    }.flatten

    val renderedOperations = renderOperations(operations)

    lines(
      "package ch.epfl.scala.bsp",
      "package endpoints",
      newline,
      "import jsonrpc4s.Endpoint",
      "import jsonrpc4s.Endpoint.unitCodec",
      renderedOperations
    )
  }

  def renderStructure(shapeId: ShapeId, fields: List[Field]): Lines = {
    lines(
      paren(s"final case class ${shapeId.getName()}")(
        fields.foldMap(renderScalaField)
      ),
      newline,
      block(s"object ${shapeId.getName()}")(
        lines(
          s"implicit val codec: JsonValueCodec[${shapeId.getName()}] = JsonCodecMaker.makeWithRequiredCollectionFields"
        )
      ),
      newline
    )
  }

  def renderClosedEnum[A](
      shapeId: ShapeId,
      enumType: EnumType[A],
      values: List[EnumValue[A]]
  ): Lines = {
    val valueType = enumValueType(enumType)
    val enumName = shapeId.getName()
    lines(
      s"sealed abstract class $enumName(val id: $valueType)",
      block(s"object $enumName")(
        values.map(renderEnumValueDef(enumType, shapeId)),
        newline,
        block(
          s"implicit val codec: JsonValueCodec[${shapeId.getName}] = new JsonValueCodec[${shapeId.getName}]"
        )(
          s"def nullValue: $enumName = null",
          s"def encodeValue(msg: $enumName, out: JsonWriter): Unit = out.writeVal(msg.id)",
          block(s"def decodeValue(in: JsonReader, default: $enumName): $enumName = ")(
            block("in.readInt() match ")(
              values.map { ev =>
                s"case ${ev.value} => ${ev.name}"
              },
              s"""case n => in.decodeError(s"Unknown message type id for $$n")"""
            )
          )
        )
      )
    )
  }

  def renderOpenEnum[A](
      shapeId: ShapeId,
      enumType: EnumType[A],
      values: List[EnumValue[A]]
  ): Lines = {
    val tpe = shapeId.getName()
    lines(
      block(s"object $tpe") {
        values.map(renderStaticValue(enumType))
      },
      newline
    )
  }

  def renderOperations(operations: List[Operation]): Lines = {
    val groupedByTargetName = operations.groupBy(_.jsonRPCMethod.split("/")(0))
    lines(
      groupedByTargetName.map { case (targetName, operations) =>
        val targetNameCapitalized = targetName.capitalize
        lines(
          s"object $targetNameCapitalized extends $targetNameCapitalized",
          block(s"trait $targetNameCapitalized")(
            operations.foldMap(renderOperation)
          )
        )
      }.toList,
      newline
    )
  }

  def renderOperation(operation: Operation): Lines = {
    val name = operation.jsonRPCMethod.split("/")(1)
    val output = operation.outputType match {
      case TPrimitive(Primitive.PUnit, _) => "Unit"
      case other                          => renderType(other)
    }
    val input = operation.inputType match {
      case TPrimitive(Primitive.PUnit, _) => "Unit"
      case other                          => renderType(other)
    }
    lines(
      s"""object $name extends Endpoint[$input, $output]("${operation.jsonRPCMethod}")"""
    )
  }

  def enumValueType[A](enumType: EnumType[A]): String = enumType match {
    case IntEnum    => "Int"
    case StringEnum => "String"
  }

  def renderStaticValue[A](enumType: EnumType[A]): EnumValue[A] => String = {
    enumType match {
      case IntEnum =>
        (ev: EnumValue[Int]) => s"""val ${ev.name} = ${ev.value}"""
      case StringEnum =>
        (ev: EnumValue[String]) => s"""val ${ev.name} = "${ev.value}""""
    }
  }

  def renderEnumValueDef[A](enumType: EnumType[A], shapeId: ShapeId): EnumValue[A] => String = {
    enumType match {
      case IntEnum =>
        (ev: EnumValue[Int]) => s"case object ${ev.name} extends ${shapeId.getName}(${ev.value})"
      case StringEnum =>
        (ev: EnumValue[String]) =>
          s"""case object ${ev.name} extends ${shapeId.getName}("${ev.value}")"""
    }
  }

  def renderScalaField(field: Field): Lines = {
    val maybeRename =
      field.jsonRename.map(name => lines(s"""@named("$name")""")).getOrElse(empty)
    lines(
      maybeRename,
      renderFieldRaw(field) + ","
    )
  }

  def renderFieldRaw(field: Field): String = {
    val name =
      if (field.name == "type")
        "`type`"
      else field.name

    val tpe = if (field.required) renderType(field.tpe) else s"Option[${renderType(field.tpe)}]"

    s"$name: $tpe"
  }

  def renderType(tpe: Type): String = tpe match {
    case TRef(shapeId)             => shapeId.getName()
    case TPrimitive(prim, shapeId) => renderPrimitive(prim, shapeId)
    case TMap(key, value)          => s"Map[${renderType(key)}, ${renderType(value)}]"
    case TCollection(member)       => s"List[${renderType(member)}]"
    case TUntaggedUnion(tpes)      => renderType(tpes.head) // Todo what does bsp4j do ?
  }

  def renderPrimitive(prim: Primitive, shapeId: ShapeId): String = {
    // Special handling for URI
    if (shapeId == ShapeId.fromParts("bsp", "URI")) {
      "Uri"
    } else
      prim match {
        case PFloat     => "Float"
        case PDouble    => "Double"
        case PUnit      => "Unit"
        case PString    => "String"
        case PInt       => "Int"
        case PDocument  => "RawJson"
        case PBool      => "Boolean"
        case PLong      => "Long"
        case PTimestamp => "Long"
      }
  }

}
