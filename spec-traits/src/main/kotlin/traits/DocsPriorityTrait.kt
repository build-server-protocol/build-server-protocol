package traits

import software.amazon.smithy.model.node.Node
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.traits.AbstractTrait
import software.amazon.smithy.model.traits.AbstractTraitBuilder
import software.amazon.smithy.model.traits.TraitService
import software.amazon.smithy.utils.SmithyBuilder
import software.amazon.smithy.utils.ToSmithyBuilder

class DocsPriorityTrait private constructor(builder: Builder) : AbstractTrait(ID, builder.sourceLocation),
    ToSmithyBuilder<DocsPriorityTrait> {
    val priority: Int

    init {
        priority = builder.priority
    }

    override fun createNode(): Node {
        return Node.from(priority)
    }

    override fun toBuilder(): SmithyBuilder<DocsPriorityTrait> {
        return builder().priority(priority).sourceLocation(sourceLocation)
    }

    class Builder : AbstractTraitBuilder<DocsPriorityTrait, Builder>() {
        var priority = 0
        fun priority(priority: Int): Builder {
            this.priority = priority
            return this
        }

        override fun build(): DocsPriorityTrait {
            return DocsPriorityTrait(this)
        }
    }

    class Provider : TraitService {
        override fun getShapeId(): ShapeId {
            return ID
        }

        override fun createTrait(target: ShapeId, value: Node): DocsPriorityTrait {
            val priority = value.expectNumberNode().value.toInt()
            return builder().priority(priority).sourceLocation(value)!!.build()
        }
    }

    companion object {
        val ID: ShapeId = ShapeId.from("traits#docsPriority")
        fun builder(): Builder {
            return Builder()
        }
    }
}
