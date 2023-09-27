package traits

import software.amazon.smithy.model.SourceException
import software.amazon.smithy.model.node.Node
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.traits.AbstractTrait
import software.amazon.smithy.model.traits.AbstractTraitBuilder
import software.amazon.smithy.model.traits.TraitService
import software.amazon.smithy.utils.SmithyBuilder
import software.amazon.smithy.utils.ToSmithyBuilder
import java.util.Locale

class EnumKindTrait private constructor(builder: Builder) : AbstractTrait(ID, builder.sourceLocation),
    ToSmithyBuilder<EnumKindTrait> {
    enum class EnumKind(val value: String) {
        OPEN("open"),
        CLOSED("closed")
    }

    val enumKind: EnumKind

    init {
        enumKind = builder.enumKind ?: throw SourceException("An enumKind must be provided", sourceLocation)
    }

    override fun createNode(): Node {
        return Node.from(enumKind.value)
    }

    override fun toBuilder(): SmithyBuilder<EnumKindTrait> {
        return builder().enumKind(enumKind).sourceLocation(sourceLocation)
    }

    class Builder : AbstractTraitBuilder<EnumKindTrait, Builder>() {
        var enumKind: EnumKind? = null
        fun enumKind(enumKind: EnumKind?): Builder {
            this.enumKind = enumKind
            return this
        }

        override fun build(): EnumKindTrait {
            return EnumKindTrait(this)
        }
    }

    class Provider : TraitService {
        override fun getShapeId(): ShapeId {
            return ID
        }

        override fun createTrait(target: ShapeId, value: Node): EnumKindTrait {
            val enumKind = EnumKind.valueOf(value.expectStringNode().value.uppercase(Locale.getDefault()))
            val builder = Builder().enumKind(enumKind)
            return EnumKindTrait(builder)
        }
    }

    companion object {
        val ID: ShapeId = ShapeId.from("traits#enumKind")

        /**
         * @return Returns a new EnumKindTrait builder.
         */
        fun builder(): Builder {
            return Builder()
        }
    }
}
