package bsp.codegen.ir

import software.amazon.smithy.model.shapes.ShapeId

// This file contains an internal representation specifically tailored for the needs and idioms
// of bsp4j. It is not meant to be reused for Scala code-generation, or any other language, for that matter.

sealed trait Def {
  def shapeId: ShapeId;

  def hints: List[Hint]
}

object Def {
  final case class PrimitiveAlias(shapeId: ShapeId, prim: Primitive, hints: List[Hint]) extends Def

  final case class Structure(shapeId: ShapeId, fields: List[Field], hints: List[Hint]) extends Def

  final case class OpenEnum[A](
      shapeId: ShapeId,
      enumType: EnumType[A],
      values: List[EnumValue[A]],
      hints: List[Hint]
  ) extends Def

  final case class ClosedEnum[A](
      shapeId: ShapeId,
      enumType: EnumType[A],
      values: List[EnumValue[A]],
      hints: List[Hint]
  ) extends Def

  final case class Service(shapeId: ShapeId, operations: List[Operation], hints: List[Hint])
      extends Def
}

sealed trait JsonRPCMethodType extends Product with Serializable

object JsonRPCMethodType {
  case object Request extends JsonRPCMethodType

  case object Notification extends JsonRPCMethodType
}

final case class Operation(
    shapeId: ShapeId,
    inputType: Type,
    outputType: Type,
    jsonRPCMethodType: JsonRPCMethodType,
    jsonRPCMethod: String,
    hints: List[Hint]
) {
  def name: String = shapeId.getName()
}

sealed trait EnumType[A]

object EnumType {
  case object IntEnum extends EnumType[Int]

  case object StringEnum extends EnumType[String]
}

final case class EnumValue[A](name: String, value: A, hints: List[Hint])

final case class Field(
    name: String,
    tpe: Type,
    required: Boolean,
    jsonRename: Option[String],
    hints: List[Hint]
)

final case class Alternative(name: String, typ: Type, hints: List[Hint])

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
}

object Type {
  case class TCollection(member: Type) extends Type

  case class TMap(key: Type, value: Type) extends Type

  case class TRef(shapeId: ShapeId) extends Type

  case class TPrimitive(prim: Primitive, shapeId: ShapeId) extends Type {
    override def isPrimitive(primitive: Primitive): Boolean = prim == primitive
  }

  case class TUntaggedUnion(tpes: List[Type]) extends Type

  val TUnit = TPrimitive(Primitive.PUnit, ShapeId.from("smithy.api#void"))
  val TBool = TPrimitive(Primitive.PBool, ShapeId.from("smithy.api#Boolean"))
  val TString = TPrimitive(Primitive.PString, ShapeId.from("smithy.api#String"))
  val TInt = TPrimitive(Primitive.PInt, ShapeId.from("smithy.api#Integer"))
  val TLong = TPrimitive(Primitive.PLong, ShapeId.from("smithy.api#Long"))
}

sealed trait Hint

object Hint {
  case class Documentation(string: String) extends Hint
}
