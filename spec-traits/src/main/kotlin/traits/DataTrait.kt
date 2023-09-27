package traits

import software.amazon.smithy.model.node.ObjectNode
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.traits.AnnotationTrait

class DataTrait(node: ObjectNode) : AnnotationTrait(ID, node) {

    class Provider : AnnotationTrait.Provider<DataTrait>(ID, ::DataTrait)

    companion object {
        val ID: ShapeId = ShapeId.from("traits#data")
    }
}