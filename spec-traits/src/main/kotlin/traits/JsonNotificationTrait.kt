package traits

import software.amazon.smithy.model.SourceLocation
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.traits.StringTrait

class JsonNotificationTrait(value: String, sourceLocation: SourceLocation) : StringTrait(ID, value, sourceLocation) {
    class Provider : StringTrait.Provider<JsonNotificationTrait>(ID, ::JsonNotificationTrait)
    companion object {
        val ID: ShapeId = ShapeId.from("traits#jsonNotification")
    }
}
