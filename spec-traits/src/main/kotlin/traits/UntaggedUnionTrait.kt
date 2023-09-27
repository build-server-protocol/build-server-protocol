package traits

import software.amazon.smithy.model.node.ObjectNode
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.traits.AnnotationTrait

class UntaggedUnionTrait(node: ObjectNode) : AnnotationTrait(ID, node) {
    class Provider : AnnotationTrait.Provider<UntaggedUnionTrait>(ID, ::UntaggedUnionTrait)

    companion object {
        val ID: ShapeId = ShapeId.from("traits#untaggedUnion")
    }
}
