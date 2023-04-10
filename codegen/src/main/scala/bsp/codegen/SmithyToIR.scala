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
import ch.epfl.smithy.jsonrpc.traits.DataTrait
import ch.epfl.smithy.jsonrpc.traits.JsonRequestTrait
import ch.epfl.smithy.jsonrpc.traits.JsonNotificationTrait

import java.util.Optional
import software.amazon.smithy.model.traits.{DocumentationTrait, JsonNameTrait, MixinTrait, RequiredTrait, TagsTrait}

class SmithyToIR(model: Model) {

  def docTree: DocTree = {
    val shapes = model.shapes().iterator().asScala.toList
    val commonTag = "basic"
    val commonShapes =
      shapes.filter(_.getTrait(classOf[TagsTrait]).toScala.exists(_.getTags.contains(commonTag)))
    val commonShapeDocs = commonShapes.flatMap(_.accept(DocShapeVisitor))
    val serviceDocs = shapes.filter(_.isServiceShape()).flatMap(_.accept(DocShapeVisitor))
    DocTree(commonShapeDocs, serviceDocs)
  }

  def definitions(namespace: String): List[Def] =
    model
      .shapes()
      .iterator()
      .asScala
      .filter(_.getId.getNamespace == namespace)
      .map { shape =>
        if (shape.hasTrait("smithy.api#trait")) None
        else shape.accept(ToIRVisitor)
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

    def buildOperation(op: OperationShape): Option[Operation] = {
      val maybeMethod = if (op.hasTrait(classOf[JsonRequestTrait])) {
        val methodName = op.expectTrait(classOf[JsonRequestTrait]).getValue
        val methodType = JsonRPCMethodType.Request
        Some(methodName -> methodType)
      } else if (op.hasTrait(classOf[JsonNotificationTrait])) {
        val methodName = op.expectTrait(classOf[JsonNotificationTrait]).getValue
        val methodType = JsonRPCMethodType.Notification
        Some(methodName -> methodType)
      } else None
      maybeMethod.map { case (methodName, methodType) =>
        val inputType = getType(op.getInput).getOrElse(TPrimitive(PUnit))
        val outputType = getType(op.getOutput).getOrElse(TPrimitive(PUnit))
        val hints = getHints(op)
        Operation(op.getId, inputType, outputType, methodType, methodName, hints)
      }
    }

    override def serviceShape(shape: ServiceShape): Option[Def] = {
      val operations = shape
        .getOperations
        .asScala
        .toList
        .map(model.expectShape(_, classOf[OperationShape]))
        .flatMap(buildOperation)
      Some(Def.Service(shape.getId, operations, getHints(shape)))
    }

    def toField(member: MemberShape): Option[Field] = {
      val required = member.hasTrait(classOf[RequiredTrait])
      val jsonRenamed = member.getTrait(classOf[JsonNameTrait]).toScala.map(_.getValue)
      val name = member.getMemberName
      getType(member.getTarget).map(Field(name, _, required, jsonRenamed, getHints(member)))
    }
    def getType(shapeId: ShapeId): Option[Type] = model.expectShape(shapeId).accept(ToTypeVisitor)
    def getType(maybeShapeId: Optional[ShapeId]): Option[Type] =
      maybeShapeId.toScala.flatMap(getType)

    override def structureShape(shape: StructureShape): Option[Def] = {
      if (shape.hasTrait(classOf[MixinTrait])) {
        return None
      }

      val fields = shape.members().asScala.flatMap(toField).toList
      def insertDiscriminator(list: List[Field]): List[Field] = list match {
        case field :: next
            if field.name == "data" && field.tpe == Type.TPrimitive(Primitive.PDocument) =>
          val doc =
            "Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified."
          val hints = List(Hint.Documentation(doc))
          Field("dataKind", Type.TPrimitive(Primitive.PString), required = false, None, hints) :: field :: next
        case field :: next => field :: insertDiscriminator(next)
        case Nil           => Nil
      }

      Some(Def.Structure(shape.getId, insertDiscriminator(fields), getHints(shape)))
    }

    override def intEnumShape(shape: IntEnumShape): Option[Def] = {
      val enumValues = shape
        .getEnumValues
        .asScala
        .map { case (name, value) =>
          val valueHints = getHints(shape.getAllMembers.get(name))
          EnumValue(name, value.toInt, valueHints)
        }
        .toList
      val hints = getHints(shape)
      shape.expectTrait(classOf[EnumKindTrait]).getEnumKind match {
        case OPEN   => Some(Def.OpenEnum(shape.getId, EnumType.IntEnum, enumValues, hints))
        case CLOSED => Some(Def.ClosedEnum(shape.getId, EnumType.IntEnum, enumValues, hints))
      }
    }
    override def enumShape(shape: EnumShape): Option[Def] = {
      val enumValues = shape
        .getEnumValues
        .asScala
        .map { case (name, value) =>
          val valueHints = getHints(shape.getAllMembers.get(name))
          EnumValue(name, value, valueHints)
        }
        .toList
      val hints = getHints(shape)
      shape.expectTrait(classOf[EnumKindTrait]).getEnumKind match {
        case OPEN   => Some(Def.OpenEnum(shape.getId, EnumType.StringEnum, enumValues, hints))
        case CLOSED => Some(Def.ClosedEnum(shape.getId, EnumType.StringEnum, enumValues, hints))
      }
    }

    val allDataKindAnnotated: Map[ShapeId, List[(String, Shape)]] = model
      .getShapesWithTrait(classOf[DataTrait])
      .asScala
      .toList
      .map { shape =>
        val tr = shape.expectTrait(classOf[DataTrait])
        shape -> tr
      }
      .groupBy { case (_, tr) => tr.getPolymorphicData }
      .map { case (dataType, shapeAndTraits) =>
        dataType -> shapeAndTraits.map { case (shape, tr) =>
          tr.getKind -> shape
        }
      }

    override def documentShape(shape: DocumentShape): Option[Def] = {
      val id = shape.getId
      allDataKindAnnotated.get(id).map { allKnownInhabitants =>
        val openEnumId = ShapeId.fromParts(id.getNamespace, id.getName + "Kind")
        val values = allKnownInhabitants.map { case (disc, member) =>
          val snakeCased = disc.replace('-', '_').toUpperCase()
          val memberDoc = s"/** `data` field must contain a ${member.getId.getName} object. */"
          EnumValue(snakeCased, disc, List(Hint.Documentation(memberDoc)))
        }
        val shapeDoc = shape
          .getTrait(classOf[DocumentationTrait])
          .toScala
          .map(doc => Hint.Documentation(doc.getValue))
          .toList
        Def.OpenEnum(openEnumId, EnumType.StringEnum, values, shapeDoc)
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
    def structureShape(shape: StructureShape): Option[Type] = Some(TRef(shape.getId))

    def listShape(shape: ListShape): Option[Type] =
      model.expectShape(shape.getMember.getTarget).accept(this).map(TCollection)

    def mapShape(shape: MapShape): Option[Type] = for {
      k <- shape.getKey.accept(this)
      v <- shape.getValue.accept(this)
    } yield TMap(k, v)

    def unionShape(shape: UnionShape): Option[Type] = Some {
      if (shape.hasTrait(classOf[UntaggedUnionTrait])) {
        val memberTypes = shape.getAllMembers.asScala.values.map(_.accept(this)).toList.flatten
        TUntaggedUnion(memberTypes)
      } else TRef(shape.getId)
    }

    override def enumShape(shape: EnumShape): Option[Type] = {
      val enumKind = shape.expectTrait(classOf[EnumKindTrait]).getEnumKind
      enumKind match {
        case OPEN   => prim(PString)
        case CLOSED => Some(TRef(shape.getId))
      }
    }

    override def intEnumShape(shape: IntEnumShape): Option[Type] = {
      val enumKind = shape.expectTrait(classOf[EnumKindTrait]).getEnumKind
      enumKind match {
        case OPEN   => prim(PInt)
        case CLOSED => Some(TRef(shape.getId))
      }
    }

    def memberShape(shape: MemberShape): Option[Type] =
      model.expectShape(shape.getTarget).accept(this)

    def timestampShape(shape: TimestampShape): Option[Type] = Some(TPrimitive(PTimestamp))

    def shortShape(shape: ShortShape): Option[Type] = None
    def blobShape(shape: BlobShape): Option[Type] = None
    def byteShape(shape: ByteShape): Option[Type] = None
    def operationShape(shape: OperationShape): Option[Type] = None
    def resourceShape(shape: ResourceShape): Option[Type] = None
    def serviceShape(shape: ServiceShape): Option[Type] = None
    def bigIntegerShape(shape: BigIntegerShape): Option[Type] = None
    def bigDecimalShape(shape: BigDecimalShape): Option[Type] = None
  }

  def getHints(shape: Shape): List[Hint] = {
    val documentation = shape
      .getTrait(classOf[DocumentationTrait])
      .toScala
      .map(_.getValue)
      .map(Hint.Documentation)
      .toList
    documentation
  }

  object DocShapeVisitor extends ShapeVisitor.Default[Option[DocNode]] {
    protected def getDefault(shape: Shape): Option[DocNode] = {
      shape.accept(ToIRVisitor).map { definition =>
        ShapeDocNode(definition, Nil)
      }
    }

    override def structureShape(shape: StructureShape): Option[DocNode] = {
      val childrenNodes = shape
        .members()
        .asScala
        .toList
        .flatMap(m => model.expectShape(m.getTarget).accept(this).toList)

      shape.accept(ToIRVisitor).map { definition =>
        ShapeDocNode(definition, childrenNodes)
      }
    }

    override def operationShape(shape: OperationShape): Option[DocNode] = {
      val input = shape.getInput.toScala.flatMap(input => model.expectShape(input).accept(this))
      val output = shape.getOutput.toScala.flatMap(output => model.expectShape(output).accept(this))
      ToIRVisitor.buildOperation(shape).map { op: Operation =>
        OperationDocNode(op, input, output)
      }
    }

    override def serviceShape(shape: ServiceShape): Option[DocNode] = {
      val ops = shape.getOperations.asScala.toList.map(model.expectShape).flatMap(_.accept(this))
      Some(ServiceDocNode(shape.getId, ops))
    }

  }

}
