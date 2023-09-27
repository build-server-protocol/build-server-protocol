package org.jetbrains.bsp.generators.ir

import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.BooleanShape
import software.amazon.smithy.model.shapes.DocumentShape
import software.amazon.smithy.model.shapes.EnumShape
import software.amazon.smithy.model.shapes.IntEnumShape
import software.amazon.smithy.model.shapes.IntegerShape
import software.amazon.smithy.model.shapes.ListShape
import software.amazon.smithy.model.shapes.LongShape
import software.amazon.smithy.model.shapes.MapShape
import software.amazon.smithy.model.shapes.MemberShape
import software.amazon.smithy.model.shapes.OperationShape
import software.amazon.smithy.model.shapes.ServiceShape
import software.amazon.smithy.model.shapes.Shape
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.shapes.ShapeVisitor
import software.amazon.smithy.model.shapes.StringShape
import software.amazon.smithy.model.shapes.StructureShape
import software.amazon.smithy.model.traits.DeprecatedTrait
import software.amazon.smithy.model.traits.DocumentationTrait
import software.amazon.smithy.model.traits.JsonNameTrait
import software.amazon.smithy.model.traits.MixinTrait
import software.amazon.smithy.model.traits.RequiredTrait
import software.amazon.smithy.model.traits.UnstableTrait
import traits.DataKindTrait
import traits.DataTrait
import traits.EnumKindTrait
import traits.JsonNotificationTrait
import traits.JsonRequestTrait
import traits.SetTrait
import java.util.stream.Collectors
import kotlin.jvm.optionals.getOrNull

class SmithyToIr(val model: Model) {

    data class PolymorphicData(val kind: String, val shapeId: ShapeId)

    val allDataKindAnnotated: Map<ShapeId, List<PolymorphicData>> = run {
        val allExtendableTypes = model.getShapesWithTrait(DataTrait::class.java).toList()
        val allExtendableTypeIds = allExtendableTypes.map { it.id }.toSet()

        val dataKindInhabitants = model
            .getShapesWithTrait(DataKindTrait::class.java)
            .toList()
            .map { shape ->
                val dataKindTrait = shape.expectTrait(DataKindTrait::class.java)
                shape to dataKindTrait
            }

        // Validate that all data kinds extend a known extendable type.
        dataKindInhabitants.forEach { (shape, dataKindTrait) ->
            val correct = dataKindTrait.polymorphicData.all { allExtendableTypeIds.contains(it) }
            if (!correct) {
                throw RuntimeException(
                    "DataKindTrait on ${shape.id.name} must extend a known extendable type."
                )
            }
        }

        val groupedInhabitants = dataKindInhabitants
            .flatMap { (shape, dataKindTrait) ->
                dataKindTrait.polymorphicData.map { Triple(shape, dataKindTrait.kind, it) }
            }
            .groupBy { (_, _, referencedShapeId) -> referencedShapeId }
            .map { (dataType, shapeAndTraits) ->
                dataType to shapeAndTraits.map { (shape, dataKind, _) ->
                    PolymorphicData(dataKind, shape.id)
                }
            }.toMap()

        allExtendableTypeIds.associateWith { id ->
            val inhabitants = groupedInhabitants.getOrDefault(id, emptyList()).sortedBy { it.kind }
            inhabitants
        }
    }

    fun definitions(namespace: String): List<Def> =
        model.shapes().filter { it.id.namespace == namespace && !it.hasTrait("smithy.api#trait") }
            .flatMap { it.accept(toDefVisitor).stream() }.collect(Collectors.toList())

    private val toDefVisitor = object : ShapeVisitor.Default<List<Def>>() {
        override fun getDefault(shape: Shape): List<Def> = emptyList()

        fun buildOperation(op: OperationShape): Operation? {
            val maybeMethod = when {
                op.hasTrait(JsonRequestTrait::class.java) -> {
                    val methodName = op.expectTrait(JsonRequestTrait::class.java).value!!
                    val methodType = JsonRpcMethodType.Request
                    methodName to methodType
                }

                op.hasTrait(JsonNotificationTrait::class.java) -> {
                    val methodName = op.expectTrait(JsonNotificationTrait::class.java).value!!
                    val methodType = JsonRpcMethodType.Notification
                    methodName to methodType
                }

                else -> null
            }

            return maybeMethod?.let { (methodName, methodType) ->
                val inputType = getType(op.input.getOrNull()) ?: IrShape.Unit
                val outputType = getType(op.output.getOrNull()) ?: IrShape.Unit
                val hints = getHints(op)
                Operation(op.id, inputType, outputType, methodType, methodName, hints)
            }
        }

        override fun serviceShape(shape: ServiceShape): List<Def> {
            val operations = shape.operations.toList()
                .map { model.expectShape(it, OperationShape::class.java) }
                .mapNotNull { buildOperation(it) }

            return listOf(Def.Service(shape.id, operations, getHints(shape)))
        }

        fun toField(member: MemberShape): Field? {
            val required = member.hasTrait(RequiredTrait::class.java)
            val name = member.memberName
            return getType(member.target)?.let {
                Field(name, it, required, getHints(member))
            }
        }

        fun getType(shapeId: ShapeId?): IrShape? = shapeId?.let { model.expectShape(it).accept(toIrShapeVisitor) }

        fun dataKindShapeId(dataTypeShapeId: ShapeId): ShapeId =
            ShapeId.fromParts(dataTypeShapeId.namespace, dataTypeShapeId.name + "Kind")

        override fun structureShape(shape: StructureShape): List<Def> {
            // Skip shapes that are used as mixins.
            if (shape.hasTrait(MixinTrait::class.java)) {
                return emptyList()
            }

            val fields = shape.members().mapNotNull { toField(it) }

            fun fieldIsData(field: Field): Boolean =
                field.name == "data" && field.type.type == Type.Json

            fun makeDiscriminatorField(dataField: Field): Field {
                val doc =
                    "Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified."
                val hints = listOf(Hint.Documentation(doc))
                if (dataField.type.type != Type.Json) {
                    throw RuntimeException("Expected document type")
                }
                return Field("dataKind", IrShape(dataKindShapeId(dataField.type.shapeId), Type.String), false, hints)
            }

            fun insertDiscriminator(fields: List<Field>): List<Field> {
                val mutableFields = fields.toMutableList()

                val dataIndex = fields.indexOfFirst { fieldIsData(it) }
                if (dataIndex != -1) {
                    val newField = makeDiscriminatorField(fields[dataIndex])
                    mutableFields.add(dataIndex, newField)
                }

                return mutableFields
            }

            // this adds a "dataKind" field to the structure, it if contains a "data" (dataWithKind) field
            // ideally, we would like to delete this logic and take care of it in according generators
            val updatedFields = insertDiscriminator(fields)

            return listOf(Def.Structure(shape.id, updatedFields, getHints(shape)))
        }

        override fun intEnumShape(shape: IntEnumShape): List<Def> {
            val enumValues = shape.enumValues.map { (name, value) ->
                val valueHints = getHints(shape.allMembers.getValue(name))
                EnumValue(name, value!!, valueHints)
            }

            val hints = getHints(shape)
            return when (shape.expectTrait(EnumKindTrait::class.java).enumKind) {
                EnumKindTrait.EnumKind.OPEN ->
                    listOf(Def.OpenEnum(shape.id, EnumType.IntEnum, enumValues.sortedBy { it.value }, hints))

                EnumKindTrait.EnumKind.CLOSED ->
                    listOf(Def.ClosedEnum(shape.id, EnumType.IntEnum, enumValues, hints))
            }
        }

        override fun enumShape(shape: EnumShape): List<Def> {
            val enumValues = shape.enumValues.map { (name, value) ->
                val valueHints = getHints(shape.allMembers.getValue(name))
                EnumValue(name, value!!, valueHints)
            }

            val hints = getHints(shape)
            return when (shape.expectTrait(EnumKindTrait::class.java).enumKind) {
                EnumKindTrait.EnumKind.OPEN ->
                    listOf(Def.OpenEnum(shape.id, EnumType.StringEnum, enumValues.sortedBy { it.name }, hints))

                EnumKindTrait.EnumKind.CLOSED ->
                    listOf(Def.ClosedEnum(shape.id, EnumType.StringEnum, enumValues, hints))
            }
        }

        override fun documentShape(shape: DocumentShape): List<Def> {
            val hints = getHints(shape)

            // A document shape with the data trait in fact defines two structures: one for the data (a type alias for any)
            // and one for the data kind (an enum).
            return if (shape.hasTrait(DataTrait::class.java)) {
                val id = shape.id

                val allKnownInhabitants = allDataKindAnnotated[id]!!
                val openEnumId = dataKindShapeId(id)
                val values = allKnownInhabitants.map { (kind, memberId) ->
                    val memberDoc = "`data` field must contain a ${memberId.name} object."
                    PolymorphicDataKind(kind, getType(memberId)!!, listOf(Hint.Documentation(memberDoc)))
                }

                val dataKinds = Def.DataKinds(id, openEnumId, values, hints)
                listOf(dataKinds)
            } else {
                typeShape(shape)
            }
        }

        fun typeShape(shape: Shape): List<Def> {
            val hints = getHints(shape)
            val irShape = shape.accept(toIrShapeVisitor)

            return listOfNotNull(irShape?.let {
                Def.Alias(shape.id, it.type, hints)
            })
        }

        override fun booleanShape(shape: BooleanShape): List<Def> = typeShape(shape)

        override fun integerShape(shape: IntegerShape): List<Def> = typeShape(shape)

        override fun longShape(shape: LongShape): List<Def> = typeShape(shape)

        override fun stringShape(shape: StringShape): List<Def> = typeShape(shape)

        override fun listShape(shape: ListShape): List<Def> = typeShape(shape)

        override fun mapShape(shape: MapShape): List<Def> = typeShape(shape)

    }

    val toIrShapeVisitor = object : ShapeVisitor.Default<IrShape?>() {
        override fun getDefault(shape: Shape): IrShape? = null

        override fun booleanShape(shape: BooleanShape): IrShape = IrShape(shape.id, Type.Bool)

        override fun integerShape(shape: IntegerShape): IrShape = IrShape(shape.id, Type.Int)

        override fun longShape(shape: LongShape): IrShape = IrShape(shape.id, Type.Long)

        override fun stringShape(shape: StringShape): IrShape = IrShape(shape.id, Type.String)

        override fun documentShape(shape: DocumentShape): IrShape = IrShape(shape.id, Type.Json)

        override fun listShape(shape: ListShape): IrShape? {
            return shape.member.accept(this)?.let { memberType ->
                if (shape.hasTrait(SetTrait::class.java)) IrShape(shape.id, Type.Set(memberType))
                else IrShape(shape.id, Type.List(memberType))
            }
        }

        override fun mapShape(shape: MapShape): IrShape? {
            return shape.key.accept(this)?.let { key ->
                shape.value.accept(this)?.let { value ->
                    IrShape(shape.id, Type.Map(key, value))
                }
            }
        }

        override fun structureShape(shape: StructureShape): IrShape = IrShape(shape.id, Type.Ref)

        fun enumUniversal(shape: Shape, openType: Type): IrShape {
            val enumKind = shape.expectTrait(EnumKindTrait::class.java).enumKind
            return when (enumKind) {
                EnumKindTrait.EnumKind.OPEN -> IrShape(shape.id, openType)
                EnumKindTrait.EnumKind.CLOSED -> IrShape(shape.id, Type.Ref)
            }
        }

        override fun enumShape(shape: EnumShape): IrShape {
            return enumUniversal(shape, Type.String)
        }

        override fun intEnumShape(shape: IntEnumShape): IrShape {
            return enumUniversal(shape, Type.Int)
        }

        override fun memberShape(shape: MemberShape): IrShape? = model.expectShape(shape.target).accept(this)
    }

    fun getHints(shape: Shape): List<Hint> {
        val documentation = shape.getTrait(DocumentationTrait::class.java).map { Hint.Documentation(it.value) }
        val deprecated = shape.getTrait(DeprecatedTrait::class.java).map { Hint.Deprecated(it.message.orElse("")) }
        val rename = shape.getTrait(JsonNameTrait::class.java).map { Hint.JsonRename(it.value) }
        val unstable = shape.getTrait(UnstableTrait::class.java).map { Hint.Unstable }

        return listOf(documentation, deprecated, rename, unstable).mapNotNull { it.getOrNull() }
    }
}