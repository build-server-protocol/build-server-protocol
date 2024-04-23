package bsp.codegen.ir

import bsp.codegen.common.ir
import bsp.codegen.common.ir.{Field, Operation, PolymorphicDataKind}
import software.amazon.smithy.model.shapes.ShapeId

// This file contains an internal representation specifically tailored for the needs and idioms
// of bsp4j. It is not meant to be reused for Scala code-generation, or any other language, for that matter.

sealed trait Def {
  def members: List[ShapeId] = List.empty

  def shapeId: ShapeId

  def hints: List[ir.Hint]
}

object Def {
  final case class Alias(shapeId: ShapeId, aliasedType: ir.Type, hints: List[ir.Hint])
      extends ir.Def

  final case class Structure(
      shapeId: ShapeId,
      fields: List[Field],
      hints: List[ir.Hint],
      associatedDataKinds: List[PolymorphicDataKind]
  ) extends ir.Def {
    override def members: List[ShapeId] =
      fields.flatMap(_.tpe.members()) ++ associatedDataKinds.map(_.shapeId)
  }

  final case class OpenEnum[A](
      shapeId: ShapeId,
      enumType: ir.EnumType[A],
      values: List[ir.EnumValue[A]],
      hints: List[ir.Hint]
  ) extends ir.Def

  final case class ClosedEnum[A](
      shapeId: ShapeId,
      enumType: ir.EnumType[A],
      values: List[ir.EnumValue[A]],
      hints: List[ir.Hint]
  ) extends ir.Def

  final case class Service(shapeId: ShapeId, operations: List[Operation], hints: List[ir.Hint])
      extends ir.Def
}

sealed trait JsonRPCMethodType extends Product with Serializable

object JsonRPCMethodType {
  case object Request extends JsonRPCMethodType

  case object Notification extends JsonRPCMethodType
}

final case class Operation(
    shapeId: ShapeId,
    inputType: ir.Type,
    outputType: ir.Type,
    jsonRPCMethodType: JsonRPCMethodType,
    jsonRPCMethod: String,
    hints: List[ir.Hint]
) {
  def name: String = shapeId.getName()
}

sealed trait EnumType[A]

object EnumType {
  case object IntEnum extends ir.EnumType[Int]

  case object StringEnum extends ir.EnumType[String]
}

final case class EnumValue[A](name: String, value: A, hints: List[ir.Hint])

final case class Field(
    name: String,
    tpe: ir.Type,
    required: Boolean,
    jsonRename: Option[String],
    hints: List[ir.Hint]
)

final case class Alternative(name: String, typ: ir.Type, hints: List[ir.Hint])

sealed trait Primitive

object Primitive {
  case object PUnit extends Primitive

  case object PBool extends Primitive

  case object PString extends Primitive

  case object PInt extends Primitive

  case object PLong extends Primitive

  case object PFloat extends Primitive

  case object PDouble extends Primitive

  case object PDocument extends Primitive

  case object PTimestamp extends Primitive
}

sealed trait Type {
  def isPrimitive(primitive: Primitive): Boolean = false

  def members(): List[ShapeId]
}

object Type {
  case class TCollection(member: ir.Type) extends ir.Type {
    override def members(): List[ShapeId] = member.members()
  }

  case class TSet(member: ir.Type) extends ir.Type {
    override def members(): List[ShapeId] = member.members()
  }

  case class TMap(key: ir.Type, value: ir.Type) extends ir.Type {
    override def members(): List[ShapeId] = key.members() ++ value.members()
  }

  case class TRef(shapeId: ShapeId) extends ir.Type {
    override def members(): List[ShapeId] = List(shapeId)
  }

  case class TPrimitive(prim: Primitive, shapeId: ShapeId) extends ir.Type {
    override def isPrimitive(primitive: Primitive): Boolean = prim == primitive

    override def members(): List[ShapeId] = List(shapeId)
  }

  case class TUntaggedUnion(tpes: List[ir.Type]) extends ir.Type {
    override def members(): List[ShapeId] = tpes.flatMap(_.members())
  }

  val TUnit = TPrimitive(Primitive.PUnit, ShapeId.from("smithy.api#void"))
  val TBool = TPrimitive(Primitive.PBool, ShapeId.from("smithy.api#Boolean"))
  val TString = TPrimitive(Primitive.PString, ShapeId.from("smithy.api#String"))
  val TInt = TPrimitive(Primitive.PInt, ShapeId.from("smithy.api#Integer"))
  val TLong = TPrimitive(Primitive.PLong, ShapeId.from("smithy.api#Long"))
  val TFloat = TPrimitive(Primitive.PFloat, ShapeId.from("smithy.api#Float"))
  val TDouble = TPrimitive(Primitive.PDouble, ShapeId.from("smithy.api#Double"))
  val TDocument = TPrimitive(Primitive.PDocument, ShapeId.from("smithy.api#Document"))
  val TTimestamp = TPrimitive(Primitive.PTimestamp, ShapeId.from("smithy.api#Timestamp"))
}

sealed trait Hint

object Hint {
  case class Documentation(string: String) extends ir.Hint
  case class Deprecated(message: String) extends ir.Hint
  case object Unstable extends ir.Hint
}

case class PolymorphicDataKind(kind: String, shapeId: ShapeId)
