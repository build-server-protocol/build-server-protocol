package bsp.codegen.ir

import bsp.codegen.docs.{DocNode, DocTree, OperationDocNode, ServiceDocNode, StructureDocNode}
import bsp.codegen.ir.Primitive._
import bsp.codegen.ir.Type._
import traits.EnumKindTrait.EnumKind.{CLOSED, OPEN}
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes._
import software.amazon.smithy.model.traits._
import traits.{
  DataKindTrait,
  DataTrait,
  DocsPriorityTrait,
  EnumKindTrait,
  JsonNotificationTrait,
  JsonRequestTrait,
  SetTrait,
  UntaggedUnionTrait
}

import java.util.Optional
import scala.collection.mutable.{Map => MMap}
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

class SmithyToIR(model: Model) {

  val allDataKindAnnotated: Map[ShapeId, List[PolymorphicDataKind]] = {
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
    dataKindInhabitants.foreach(inhabitant => {
      val correct = inhabitant._2.getPolymorphicData.asScala.forall(allExtendableTypeIds.contains)
      if (!correct) {
        throw new RuntimeException(
          s"DataKindTrait on ${inhabitant._1.getId.getName} must extend a known extendable type."
        )
      }
    })

    val groupedInhabitants = dataKindInhabitants
      .flatMap { case (shape, dataKindTrait) =>
        dataKindTrait.getPolymorphicData.asScala.map((shape, dataKindTrait.getKind, _))
      }
      .groupBy { case (_, _, referencedShapeId) => referencedShapeId }
      .map { case (dataType, shapeAndTraits) =>
        dataType -> shapeAndTraits.map { case (shape, dataKind, _) =>
          PolymorphicDataKind(dataKind, shape.getId)
        }
      }

    allExtendableTypeIds.map { id =>
      val inhabitants = groupedInhabitants.getOrElse(id, List.empty).sortBy(_.kind)
      id -> inhabitants
    }.toMap
  }

  def docTree(namespace: String): DocTree = {
    val shapes =
      model.shapes().iterator().asScala.toList.filter(_.getId.getNamespace == namespace)
    val commonTag = "basic"
    val commonShapes =
      shapes
        .filter(_.getTrait(classOf[TagsTrait]).toScala.exists(_.getTags.contains(commonTag)))
        .sortBy(_.getTrait(classOf[DocsPriorityTrait]).toScala.map(_.getPriority).getOrElse(0))
        .reverse

    val docNodes = {
      val map = MMap[ShapeId, DocNode]()
      val visitor = new DocShapeVisitor(map)

      shapes.foreach(_.accept(visitor))
      map.toMap
    }

    val commonShapeIds = commonShapes.map(_.getId)
    val services = docNodes.collect({ case (id, node: ServiceDocNode) => (id, node) }).values.toList
    val operations = docNodes.collect({ case (id, node: OperationDocNode) => (id, node) })
    val structures = docNodes.collect({ case (id, node: StructureDocNode) => (id, node) })
    val dataKindInhabitants = allDataKindAnnotated
      .collect { case (extendableType, inhabitants) =>
        (extendableType, inhabitants.filter(_.shapeId.getNamespace == namespace))
      }
      .filter(_._2.nonEmpty)

    DocTree(commonShapeIds, services, operations, structures, dataKindInhabitants)
  }

  def definitions(namespace: String): List[Def] =
    model
      .shapes()
      .iterator()
      .asScala
      .filter(_.getId.getNamespace == namespace)
      .flatMap { shape =>
        if (shape.hasTrait("smithy.api#trait")) None
        else shape.accept(ToIRVisitor)
      }
      .toList

  object ToIRVisitor extends ShapeVisitor.Default[List[Def]] {
    // A lot of smithy shapes are not transformed to bsp4j data types, but are rather used
    // as type aliases and holders in Smithy. That is the case, in particular, for all
    // primitive shapes, that get handled by this default method.
    protected def getDefault(shape: Shape): List[Def] = List.empty

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
        val inputType = getType(op.getInput).getOrElse(TUnit)
        val outputType = getType(op.getOutput).getOrElse(TUnit)
        val hints = getHints(op)
        Operation(op.getId, inputType, outputType, methodType, methodName, hints)
      }
    }

    override def serviceShape(shape: ServiceShape): List[Def] = {
      val operations = shape.getOperations.asScala.toList
        .map(model.expectShape(_, classOf[OperationShape]))
        .flatMap(buildOperation)
      List(Def.Service(shape.getId, operations, getHints(shape)))
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

    def dataKindShapeId(dataTypeShapeId: ShapeId): ShapeId =
      ShapeId.fromParts(dataTypeShapeId.getNamespace, dataTypeShapeId.getName + "Kind")

    override def structureShape(shape: StructureShape): List[Def] = {
      // Skip shapes that are used as mixins.
      if (shape.hasTrait(classOf[MixinTrait])) {
        return List.empty
      }

      val fields = shape.members().asScala.flatMap(toField).toList

      def fieldIsData(field: Field): Boolean =
        field.name == "data" && field.tpe.isPrimitive(Primitive.PDocument)

      def makeDiscriminatorField(dataField: Field): Field = {
        val doc =
          "Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified."
        val hints = List(Hint.Documentation(doc))
        val dataTypeShape = dataField.tpe match {
          case TPrimitive(Primitive.PDocument, dataTypeShape) => dataTypeShape
          case _ => throw new RuntimeException("Expected document type")
        }
        Field(
          "dataKind",
          TPrimitive(PString, dataKindShapeId(dataTypeShape)),
          required = false,
          None,
          hints
        )
      }

      def insertDiscriminator(fields: List[Field]): List[Field] = fields match {
        case field :: next if fieldIsData(field) =>
          val newField = makeDiscriminatorField(field)
          newField :: field :: next

        case field :: next => field :: insertDiscriminator(next)
        case Nil           => Nil
      }

      val associatedDataKinds =
        fields.filter(fieldIsData).flatMap(_.tpe.members()) match {
          case Nil => List.empty
          case dataShapeId :: Nil =>
            if (model.expectShape(dataShapeId).hasTrait(classOf[DataTrait])) {
              allDataKindAnnotated(dataShapeId)
            } else {
              throw new RuntimeException("Expected data field's shape to have data trait")
            }
          case _ => throw new RuntimeException("Expected exactly one data field")
        }

      val updatedFields = insertDiscriminator(fields)

      List(Def.Structure(shape.getId, updatedFields, getHints(shape), associatedDataKinds))
    }

    override def intEnumShape(shape: IntEnumShape): List[Def] = {
      val enumValues = shape.getEnumValues.asScala.map { case (name, value) =>
        val valueHints = getHints(shape.getAllMembers.get(name))
        EnumValue(name, value.toInt, valueHints)
      }.toList
      val hints = getHints(shape)
      shape.expectTrait(classOf[EnumKindTrait]).getEnumKind match {
        case OPEN =>
          List(Def.OpenEnum(shape.getId, EnumType.IntEnum, enumValues.sortBy(_.value), hints))
        case CLOSED => List(Def.ClosedEnum(shape.getId, EnumType.IntEnum, enumValues, hints))
      }
    }

    override def enumShape(shape: EnumShape): List[Def] = {
      val enumValues = shape.getEnumValues.asScala.map { case (name, value) =>
        val valueHints = getHints(shape.getAllMembers.get(name))
        EnumValue(name, value, valueHints)
      }.toList
      val hints = getHints(shape)
      shape.expectTrait(classOf[EnumKindTrait]).getEnumKind match {
        case OPEN =>
          List(Def.OpenEnum(shape.getId, EnumType.StringEnum, enumValues.sortBy(_.name), hints))
        case CLOSED => List(Def.ClosedEnum(shape.getId, EnumType.StringEnum, enumValues, hints))
      }
    }

    override def documentShape(shape: DocumentShape): List[Def] = {
      val hints = getHints(shape)

      // A document shape with the data trait in fact defines two structures: one for the data (a type alias for any)
      // and one for the data kind (an enum).
      if (shape.hasTrait(classOf[DataTrait])) {
        val id = shape.getId

        val allKnownInhabitants = allDataKindAnnotated(id)
        val openEnumId = dataKindShapeId(id)
        val values = allKnownInhabitants.map { case PolymorphicDataKind(disc, member) =>
          val snakeCased = disc.replace('-', '_').toUpperCase()
          val memberDoc = s"`data` field must contain a ${member.getName} object."
          EnumValue(snakeCased, disc, List(Hint.Documentation(memberDoc)))
        }

        val dataKindDef = Def.OpenEnum(openEnumId, EnumType.StringEnum, values, hints)
        val dataDef = Def.Alias(id, Type.TDocument, hints)
        List(dataKindDef, dataDef)
      } else {
        aliasShape(shape)
      }
    }

    def aliasShape(shape: Shape): List[Def] = {
      val hints = getHints(shape)
      shape
        .accept(ToTypeVisitor)
        .map(it => List(Def.Alias(shape.getId, it, hints)))
        .getOrElse(List.empty)
    }

    override def unionShape(shape: UnionShape): List[Def] = aliasShape(shape)

    override def booleanShape(shape: BooleanShape): List[Def] = aliasShape(shape)

    override def integerShape(shape: IntegerShape): List[Def] = aliasShape(shape)

    override def longShape(shape: LongShape): List[Def] = aliasShape(shape)

    override def floatShape(shape: FloatShape): List[Def] = aliasShape(shape)

    override def doubleShape(shape: DoubleShape): List[Def] = aliasShape(shape)

    override def stringShape(shape: StringShape): List[Def] = aliasShape(shape)

    override def timestampShape(shape: TimestampShape): List[Def] = aliasShape(shape)

    override def listShape(shape: ListShape): List[Def] = aliasShape(shape)

    override def mapShape(shape: MapShape): List[Def] = aliasShape(shape)
  }

  object ToTypeVisitor extends ShapeVisitor[Option[Type]] {

    def prim(primitive: Primitive, shape: Shape): Option[Type] = Some(
      TPrimitive(primitive, shape.getId)
    )

    def booleanShape(shape: BooleanShape): Option[Type] = prim(PBool, shape)

    def integerShape(shape: IntegerShape): Option[Type] = prim(PInt, shape)

    def longShape(shape: LongShape): Option[Type] = prim(PLong, shape)

    def floatShape(shape: FloatShape): Option[Type] = prim(PFloat, shape)

    def documentShape(shape: DocumentShape): Option[Type] =
      prim(PDocument, shape)

    def doubleShape(shape: DoubleShape): Option[Type] = prim(PDouble, shape)

    def stringShape(shape: StringShape): Option[Type] = prim(PString, shape)

    def structureShape(shape: StructureShape): Option[Type] = Some(TRef(shape.getId))

    def listShape(shape: ListShape): Option[Type] =
      model
        .expectShape(shape.getMember.getTarget)
        .accept(this)
        .map(memberType =>
          if (shape.hasTrait(classOf[SetTrait])) TSet(memberType)
          else TCollection(memberType)
        )

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
        case OPEN   => prim(PString, shape)
        case CLOSED => Some(TRef(shape.getId))
      }
    }

    override def intEnumShape(shape: IntEnumShape): Option[Type] = {
      val enumKind = shape.expectTrait(classOf[EnumKindTrait]).getEnumKind
      enumKind match {
        case OPEN   => prim(PInt, shape)
        case CLOSED => Some(TRef(shape.getId))
      }
    }

    def memberShape(shape: MemberShape): Option[Type] =
      model.expectShape(shape.getTarget).accept(this)

    def timestampShape(shape: TimestampShape): Option[Type] = Some(
      TPrimitive(PTimestamp, shape.getId)
    )

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

    val deprecated = shape
      .getTrait(classOf[DeprecatedTrait])
      .toScala
      .map(_.getMessage.orElse(""))
      .map(Hint.Deprecated)

    val unstable = if (shape.hasTrait(classOf[UnstableTrait])) Some(Hint.Unstable) else None

    List(documentation, deprecated, unstable).flatten
  }

  class DocShapeVisitor(map: MMap[ShapeId, DocNode]) extends ShapeVisitor.Default[Unit] {
    protected def getDefault(shape: Shape) = {
      val id = shape.getId
      if (!map.contains(id)) {
        val docNodes = shape.accept(ToIRVisitor).map { definition =>
          StructureDocNode(definition)
        }

        docNodes.foreach(node => map.put(node.shapeId, node))
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
