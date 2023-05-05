package bsp.codegen

import bsp.codegen.Primitive._
import bsp.codegen.Type._
import bsp.traits.EnumKindTrait.EnumKind.{CLOSED, OPEN}
import bsp.traits._
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes._
import software.amazon.smithy.model.traits._

import java.util.Optional
import scala.collection.mutable.{Map => MMap}
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

class SmithyToIR(model: Model) {

  def docTree(namespace: String): DocTree = {
    val shapes =
      model.shapes().iterator().asScala.toList.filter(_.getId.getNamespace == namespace)
    val commonTag = "basic"
    val commonShapes =
      shapes
        .filter(_.getTrait(classOf[TagsTrait]).toScala.exists(_.getTags.contains(commonTag)))
        .sortBy(_.getTrait(classOf[DocsPriorityTrait]).toScala.map(_.getPriority).getOrElse(0))
        .reverse

    val serviceShapes = shapes.filter(_.isServiceShape())

    val docNodes = MMap[ShapeId, DocNode]()
    val visitor = new DocShapeVisitor(docNodes)

    (commonShapes ++ serviceShapes).foreach(_.accept(visitor))

    val commonShapeIds = commonShapes.map(_.getId)
    val serviceShapeIds = serviceShapes.map(_.getId)

    DocTree(commonShapeIds, serviceShapeIds, docNodes.toMap)
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
      val operations = shape.getOperations.asScala.toList
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
          Field(
            "dataKind",
            Type.TPrimitive(Primitive.PString),
            required = false,
            None,
            hints
          ) :: field :: next
        case field :: next => field :: insertDiscriminator(next)
        case Nil           => Nil
      }

      Some(Def.Structure(shape.getId, insertDiscriminator(fields), getHints(shape)))
    }

    override def intEnumShape(shape: IntEnumShape): Option[Def] = {
      val enumValues = shape.getEnumValues.asScala.map { case (name, value) =>
        val valueHints = getHints(shape.getAllMembers.get(name))
        EnumValue(name, value.toInt, valueHints)
      }.toList
      val hints = getHints(shape)
      shape.expectTrait(classOf[EnumKindTrait]).getEnumKind match {
        case OPEN   => Some(Def.OpenEnum(shape.getId, EnumType.IntEnum, enumValues, hints))
        case CLOSED => Some(Def.ClosedEnum(shape.getId, EnumType.IntEnum, enumValues, hints))
      }
    }

    override def enumShape(shape: EnumShape): Option[Def] = {
      val enumValues = shape.getEnumValues.asScala.map { case (name, value) =>
        val valueHints = getHints(shape.getAllMembers.get(name))
        EnumValue(name, value, valueHints)
      }.toList
      val hints = getHints(shape)
      shape.expectTrait(classOf[EnumKindTrait]).getEnumKind match {
        case OPEN   => Some(Def.OpenEnum(shape.getId, EnumType.StringEnum, enumValues, hints))
        case CLOSED => Some(Def.ClosedEnum(shape.getId, EnumType.StringEnum, enumValues, hints))
      }
    }

    val allDataKindAnnotated: Map[ShapeId, List[(String, Shape)]] = {
      val allExtendableTypes = model.getShapesWithTrait(classOf[DataTrait]).asScala.toList
      val allExtendableTypeIds = allExtendableTypes.map(_.getId).toSet

      val dataKindInhabitants =
        model
          .getShapesWithTrait(classOf[DataKindTrait])
          .asScala
          .toList
          .map { shape =>
            val tr = shape.expectTrait(classOf[DataKindTrait])
            shape -> tr
          }

      // Validate that all data kinds extend a known extendable type.
      // TODO: Is it a correct place to check that?
      dataKindInhabitants.foreach(inhabitant => {
        val correct = allExtendableTypeIds.contains(inhabitant._2.getPolymorphicData)
        if (!correct) {
          throw new RuntimeException(
            s"DataKindTrait on ${inhabitant._1.getId.getName} must extend a known extendable type."
          )
        }
      })

      val groupedInhabitants = dataKindInhabitants
        .groupBy { case (_, tr) => tr.getPolymorphicData }
        .map { case (dataType, shapeAndTraits) =>
          dataType -> shapeAndTraits.map { case (shape, tr) =>
            tr.getKind -> shape
          }
        }

      allExtendableTypeIds.map { id =>
        val inhabitants = groupedInhabitants.getOrElse(id, List.empty)
        id -> inhabitants
      }.toMap
    }

    override def documentShape(shape: DocumentShape): Option[Def] = {
      val id = shape.getId
      allDataKindAnnotated.get(id).map { allKnownInhabitants =>
        val openEnumId = ShapeId.fromParts(id.getNamespace, id.getName + "Kind")
        val values = allKnownInhabitants.map { case (disc, member) =>
          val snakeCased = disc.replace('-', '_').toUpperCase()
          val memberDoc = s"`data` field must contain a ${member.getId.getName} object."
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

    def primitiveShape(shape: Shape): Option[Def] = {
      val hints = getHints(shape)
      shape match {
        case shape: BooleanShape => Some(Def.PrimitiveAlias(shape.getId, Primitive.PBool, hints))
        case shape: IntegerShape => Some(Def.PrimitiveAlias(shape.getId, Primitive.PInt, hints))
        case shape: LongShape    => Some(Def.PrimitiveAlias(shape.getId, Primitive.PLong, hints))
        case shape: FloatShape   => Some(Def.PrimitiveAlias(shape.getId, Primitive.PFloat, hints))
        case shape: DoubleShape  => Some(Def.PrimitiveAlias(shape.getId, Primitive.PDouble, hints))
        case shape: StringShape  => Some(Def.PrimitiveAlias(shape.getId, Primitive.PString, hints))
        case shape: TimestampShape =>
          Some(Def.PrimitiveAlias(shape.getId, Primitive.PTimestamp, hints))
        case _ => None
      }
    }

    override def booleanShape(shape: BooleanShape): Option[Def] = primitiveShape(shape)

    override def integerShape(shape: IntegerShape): Option[Def] = primitiveShape(shape)

    override def longShape(shape: LongShape): Option[Def] = primitiveShape(shape)

    override def floatShape(shape: FloatShape): Option[Def] = primitiveShape(shape)

    override def doubleShape(shape: DoubleShape): Option[Def] = primitiveShape(shape)

    override def stringShape(shape: StringShape): Option[Def] = primitiveShape(shape)

    override def timestampShape(shape: TimestampShape): Option[Def] = primitiveShape(shape)
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

  /// Finds ids of all immediate children structures of a shape
  object ChildrenShapeVisitor extends ShapeVisitor.Default[List[ShapeId]] {
    protected def getDefault(shape: Shape): List[ShapeId] = List(shape.getId)

    override def listShape(shape: ListShape): List[ShapeId] = {
      model.expectShape(shape.getMember.getTarget).accept(ChildrenShapeVisitor)
    }

    override def mapShape(shape: MapShape): List[ShapeId] = {
      val key = model.expectShape(shape.getKey.getTarget).accept(ChildrenShapeVisitor)
      val value = model.expectShape(shape.getValue.getTarget).accept(ChildrenShapeVisitor)
      key ++ value
    }

    override def unionShape(shape: UnionShape): List[ShapeId] = {
      shape.members.asScala.toList.flatMap(m =>
        model.expectShape(m.getTarget).accept(ChildrenShapeVisitor)
      )
    }

    override def structureShape(shape: StructureShape): List[ShapeId] = {
      List(shape.getId)
    }

    override def enumShape(shape: EnumShape): List[ShapeId] = {
      List(shape.getId)
    }

    override def intEnumShape(shape: IntEnumShape): List[ShapeId] = {
      List(shape.getId)
    }

  }

  class DocShapeVisitor(map: MMap[ShapeId, DocNode]) extends ShapeVisitor.Default[Unit] {
    protected def getDefault(shape: Shape) = {
      val id = shape.getId
      if (!map.contains(id)) {

        val childrenNodeIds = shape.members.asScala
          .flatMap(m => {
            val child = model.expectShape(m.getTarget)
            child.accept(ChildrenShapeVisitor)
          })
          .toList

        childrenNodeIds.foreach(model.expectShape(_).accept(this))

        val doc = shape.accept(ToIRVisitor).map { definition =>
          ShapeDocNode(definition, childrenNodeIds)
        }

        doc.foreach(map.put(id, _))
      }
    }

    override def operationShape(shape: OperationShape) = {
      val input = shape.getInput.toScala
      input.foreach(model.expectShape(_).accept(this))
      val output = shape.getOutput.toScala
      output.foreach(model.expectShape(_).accept(this))
      val operation = ToIRVisitor.buildOperation(shape).map { op: Operation =>
        OperationDocNode(op, input, output)
      }

      operation.foreach(map.put(shape.getId, _))
    }

    override def serviceShape(shape: ServiceShape) = {
      val ops = shape.getOperations.asScala.toList
      ops.foreach(model.expectShape(_).accept(this))
      map.put(shape.getId, ServiceDocNode(shape.getId, ops))
    }

  }

}
