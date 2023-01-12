package bsp.codegen

import software.amazon.smithy.model.shapes.ShapeId

// This file contains an internal representation specifically tailored for the needs and idioms
// of bsp4j. It is not meant to be reused for Scala code-generation, or any other language, for that matter.

sealed trait Def { def shapeId: ShapeId }
// scalafmt: {maxColumn = 120}
object Def {
  final case class Structure(shapeId: ShapeId, fields: List[Field]) extends Def
  final case class OpenEnum[A](shapeId: ShapeId, enumType: EnumType[A], values: List[EnumValue[A]]) extends Def
  final case class ClosedEnum[A](shapeId: ShapeId, enumType: EnumType[A], values: List[EnumValue[A]]) extends Def
  final case class Service(shapeId: ShapeId, operations: List[Operation]) extends Def
}

sealed trait JsonRPCMethodType extends Product with Serializable
object JsonRPCMethodType {
  case object Request extends JsonRPCMethodType
  case object Notification extends JsonRPCMethodType
}

final case class Operation(
    name: String,
    inlinedInputFields: List[Field],
    jsonRPCMethodType: JsonRPCMethodType,
    jsonRPCMethod: String
) {
  def params: List[Field] = inlinedInputFields
}

sealed trait EnumType[A]
object EnumType {
  case object IntEnum extends EnumType[Int]
  case object StringEnum extends EnumType[String]
}

final case class EnumValue[A](name: String, value: A)
final case class Field(name: String, tpe: Type, required: Boolean)
final case class Alternative(name: String, typ: Type)

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
}

sealed trait Type
object Type {
  case class TCollection(member: Type) extends Type
  case class TMap(key: Type, value: Type) extends Type
  case class TRef(shapeId: ShapeId) extends Type
  case class TPrimitive(prim: Primitive) extends Type
  case class TUntaggedUnion(tpes: List[Type]) extends Type
}
