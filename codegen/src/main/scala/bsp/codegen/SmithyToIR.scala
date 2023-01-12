package bsp.codegen

import software.amazon.smithy.model.Model
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._
import software.amazon.smithy.model.shapes._
import software.amazon.smithy.model.shapes.Shape
import Primitive._
import Type._
import ch.epfl.smithy.jsonrpc.traits.EnumKindTrait
import ch.epfl.smithy.jsonrpc.traits.EnumKindTrait.EnumKind.OPEN
import ch.epfl.smithy.jsonrpc.traits.EnumKindTrait.EnumKind.CLOSED
import ch.epfl.smithy.jsonrpc.traits.UntaggedUnionTrait
import ch.epfl.smithy.jsonrpc.traits.JsonRequestTrait
import ch.epfl.smithy.jsonrpc.traits.JsonNotificationTrait
import java.util.Optional
import software.amazon.smithy.model.traits.RequiredTrait

class SmithyToIR(model: Model) {

  def definitions(namespace: String): List[Def] =
    model
      .shapes()
      .iterator()
      .asScala
      .filter(_.getId().getNamespace() == namespace)
      .map { shape =>
        shape.accept(ToIRVisitor)
      }
      .collect { case Some(definition) =>
        definition
      }
      .toList

  object ToIRVisitor extends ShapeVisitor.Default[Option[Def]] {
    // A lot of smithy shapes are not transformed to bsp4j data types, but are rather used
    // as type aliases and holders in Smithy. That is the case, in particular, for all
    // primitive shapes, that get handled by this default method.
    protected def getDefault(shape: Shape): Option[Def] = None

    override def serviceShape(shape: ServiceShape): Option[Def] = {
      val operations = shape
        .getOperations()
        .asScala
        .toList
        .map(model.expectShape(_, classOf[OperationShape]))
        .flatMap { op =>
          val maybeMethod = if (op.hasTrait(classOf[JsonRequestTrait])) {
            val methodName = op.expectTrait(classOf[JsonRequestTrait]).getValue()
            val methodType = JsonRPCMethodType.Request
            Some(methodName -> methodType)
          } else if (op.hasTrait(classOf[JsonRequestTrait])) {
            val methodName = op.expectTrait(classOf[JsonNotificationTrait]).getValue()
            val methodType = JsonRPCMethodType.Notification
            Some(methodName -> methodType)
          } else None
          maybeMethod.map { case (methodName, methodType) =>
            val inputType = getType(op.getInput()).getOrElse(TPrimitive(PUnit))
            val outputType = getType(op.getOutput()).getOrElse(TPrimitive(PUnit))
            Operation(shape.getId().getName(), inputType, outputType, methodType, methodName)
          }
        }
      Some(Def.Service(shape.getId(), operations))
    }

    def toField(member: MemberShape): Option[Field] = {
      val required = member.hasTrait(classOf[RequiredTrait])
      val name = member.getMemberName()
      getType(member.getTarget()).map(Field(name, _, required))
    }
    def getType(shapeId: ShapeId): Option[Type] = model.expectShape(shapeId).accept(ToTypeVisitor)
    def getType(maybeShapeId: Optional[ShapeId]): Option[Type] =
      maybeShapeId.toScala.flatMap(getType)

    override def structureShape(shape: StructureShape): Option[Def] = {
      val fields = shape.members().asScala.flatMap(toField).toList
      Some(Def.Structure(shape.getId(), fields))
    }

    override def intEnumShape(shape: IntEnumShape): Option[Def] = {
      val enumValues = shape
        .getEnumValues()
        .asScala
        .map { case (name, value) =>
          EnumValue(name, value.toInt)
        }
        .toList
      shape.expectTrait(classOf[EnumKindTrait]).getEnumKind() match {
        case OPEN   => Some(Def.OpenEnum(shape.getId, EnumType.IntEnum, enumValues))
        case CLOSED => Some(Def.ClosedEnum(shape.getId, EnumType.IntEnum, enumValues))
      }
    }
    override def enumShape(shape: EnumShape): Option[Def] = {
      val enumValues = shape
        .getEnumValues()
        .asScala
        .map { case (name, value) =>
          EnumValue(name, value)
        }
        .toList
      shape.expectTrait(classOf[EnumKindTrait]).getEnumKind() match {
        case OPEN   => Some(Def.OpenEnum(shape.getId, EnumType.StringEnum, enumValues))
        case CLOSED => Some(Def.ClosedEnum(shape.getId, EnumType.StringEnum, enumValues))
      }
    }
  }

  object ToTypeVisitor extends ShapeVisitor[Option[Type]] {

    def prim(primitive: Primitive): Option[Type] = Some(TPrimitive(primitive))

    def booleanShape(shape: BooleanShape): Option[Type] = prim(PBool)
    def integerShape(shape: IntegerShape): Option[Type] = prim(PInt)
    def longShape(shape: LongShape): Option[Type] = prim(PLong)
    def floatShape(shape: FloatShape): Option[Type] = prim(PFloat)
    def documentShape(shape: DocumentShape): Option[Type] = prim(PDocument)
    def doubleShape(shape: DoubleShape): Option[Type] = prim(PDouble)
    def stringShape(shape: StringShape): Option[Type] = prim(PString)
    def structureShape(shape: StructureShape): Option[Type] = Some(TRef(shape.getId()))

    def listShape(shape: ListShape): Option[Type] =
      model.expectShape(shape.getMember().getTarget()).accept(this).map(TCollection(_))

    def mapShape(shape: MapShape): Option[Type] = for {
      k <- shape.getKey().accept(this)
      v <- shape.getValue().accept(this)
    } yield TMap(k, v)

    def unionShape(shape: UnionShape): Option[Type] = Some {
      if (shape.hasTrait(classOf[UntaggedUnionTrait])) {
        val memberTypes = shape.getAllMembers().asScala.values.map(_.accept(this)).toList.flatten
        TUntaggedUnion(memberTypes)
      } else TRef(shape.getId())
    }

    override def enumShape(shape: EnumShape): Option[Type] = {
      val enumKind = shape.expectTrait(classOf[EnumKindTrait]).getEnumKind()
      enumKind match {
        case OPEN   => prim(PString)
        case CLOSED => Some(TRef(shape.getId()))
      }
    }

    override def intEnumShape(shape: IntEnumShape): Option[Type] = {
      val enumKind = shape.expectTrait(classOf[EnumKindTrait]).getEnumKind()
      enumKind match {
        case OPEN   => prim(PInt)
        case CLOSED => Some(TRef(shape.getId()))
      }
    }

    def memberShape(shape: MemberShape): Option[Type] =
      model.expectShape(shape.getTarget()).accept(this)

    def shortShape(shape: ShortShape): Option[Type] = None
    def blobShape(shape: BlobShape): Option[Type] = None
    def byteShape(shape: ByteShape): Option[Type] = None
    def timestampShape(shape: TimestampShape): Option[Type] = None
    def operationShape(shape: OperationShape): Option[Type] = None
    def resourceShape(shape: ResourceShape): Option[Type] = None
    def serviceShape(shape: ServiceShape): Option[Type] = None
    def bigIntegerShape(shape: BigIntegerShape): Option[Type] = None
    def bigDecimalShape(shape: BigDecimalShape): Option[Type] = None
  }

}
