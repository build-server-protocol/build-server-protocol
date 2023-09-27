package traits

import software.amazon.smithy.model.SourceException
import software.amazon.smithy.model.node.Node
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.traits.AbstractTrait
import software.amazon.smithy.model.traits.AbstractTraitBuilder
import software.amazon.smithy.model.traits.TraitService
import software.amazon.smithy.utils.SmithyBuilder
import software.amazon.smithy.utils.ToSmithyBuilder
import java.util.stream.Collectors

class DataKindTrait(builder: Builder) : AbstractTrait(ID, builder.sourceLocation), ToSmithyBuilder<DataKindTrait> {
    val kind: String
    val polymorphicData: List<ShapeId>

    init {
        kind = builder.kind ?: throw SourceException("A kind must be provided", sourceLocation)
        polymorphicData = builder.polymorphicData
            ?: throw SourceException("Polymorphic data must be provided", sourceLocation)
    }

    override fun createNode(): Node {
        val builder = Node.objectNodeBuilder()
        val polymorphicDataNodes =
            polymorphicData.stream().map { obj: ShapeId -> obj.toString() }.map { value: String? -> Node.from(value) }
                .collect(Collectors.toList())
        val extendsNode = Node.fromNodes(polymorphicDataNodes)
        builder.withMember("extends", extendsNode)
        builder.withMember("kind", kind)
        return builder.build()
    }

    override fun toBuilder(): SmithyBuilder<DataKindTrait> {
        return builder().polymorphicData(polymorphicData).kind(kind).sourceLocation(sourceLocation)
    }

    class Builder : AbstractTraitBuilder<DataKindTrait, Builder>() {
        var kind: String? = null
        var polymorphicData: List<ShapeId>? = null
        fun kind(kind: String?): Builder {
            this.kind = kind
            return this
        }

        fun polymorphicData(polymorphicData: List<ShapeId>?): Builder {
            this.polymorphicData = polymorphicData
            return this
        }

        override fun build(): DataKindTrait {
            return DataKindTrait(this)
        }
    }

    class Provider : TraitService {
        override fun getShapeId(): ShapeId {
            return ID
        }

        override fun createTrait(target: ShapeId, value: Node): DataKindTrait {
            val objectNode = value.expectObjectNode()
            val polymorphicData = objectNode.expectMember("extends").expectArrayNode()
                .getElementsAs { node: Node? -> ShapeId.fromNode(node) }
            val kind = objectNode.expectMember("kind").expectStringNode().value
            return builder().kind(kind).polymorphicData(polymorphicData).build()
        }
    }

    companion object {
        val ID = ShapeId.from("traits#dataKind")

        /**
         * @return Returns a new RefinedTrait builder.
         */
        fun builder(): Builder {
            return Builder()
        }
    }
}
