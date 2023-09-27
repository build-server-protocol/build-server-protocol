package org.jetbrains.bsp.generators.ir

import software.amazon.smithy.model.shapes.ShapeId

sealed interface Def {
    val name get() = shapeId.name
    val shapeId: ShapeId
    val hints: List<Hint>

    data class Alias(override val shapeId: ShapeId, val aliasedType: Type, override val hints: List<Hint>) : Def
    data class Structure(
        override val shapeId: ShapeId,
        val fields: List<Field>,
        override val hints: List<Hint>,
    ) : Def

    data class OpenEnum<A>(
        override val shapeId: ShapeId,
        val enumType: EnumType<A>,
        val values: List<EnumValue<A>>,
        override val hints: List<Hint>
    ) : Def

    data class ClosedEnum<A>(
        override val shapeId: ShapeId,
        val enumType: EnumType<A>,
        val values: List<EnumValue<A>>,
        override val hints: List<Hint>
    ) : Def

    data class Service(
        override val shapeId: ShapeId,
        val operations: List<Operation>,
        override val hints: List<Hint>
    ) : Def

    data class DataKinds(
        override val shapeId: ShapeId,
        val kindsEnumId: ShapeId,
        val kinds: List<PolymorphicDataKind>,
        override val hints: List<Hint>
    ) : Def
}

sealed interface JsonRpcMethodType {
    object Request : JsonRpcMethodType
    object Notification : JsonRpcMethodType
}

data class Operation(
    val shapeId: ShapeId,
    val inputType: IrShape,
    val outputType: IrShape,
    val jsonRpcMethodType: JsonRpcMethodType,
    val jsonRpcMethod: String,
    val hints: List<Hint>
) {
    val name: String
        get() = shapeId.name
}

sealed interface EnumType<A> {
    object IntEnum : EnumType<Int>
    object StringEnum : EnumType<String>
}

data class EnumValue<A>(val name: String, val value: A, val hints: List<Hint>)

sealed interface Type {
    data class Set(val member: IrShape) : Type
    data class List(val member: IrShape) : Type
    data class Map(val key: IrShape, val value: IrShape) : Type
    object Ref : Type

    // Should be data objects
    object Unit : Type
    object Bool : Type
    object String : Type
    object Int : Type
    object Long : Type
    object Json : Type
}

data class IrShape(val shapeId: ShapeId, val type: Type) {
    companion object {
        val Unit: IrShape = IrShape(ShapeId.from("smithy.api#Unit"), Type.Unit)
        val Bool: IrShape = IrShape(ShapeId.from("smithy.api#Boolean"), Type.Bool)
        val String: IrShape = IrShape(ShapeId.from("smithy.api#String"), Type.String)
        val Int: IrShape = IrShape(ShapeId.from("smithy.api#Integer"), Type.Int)
        val Long: IrShape = IrShape(ShapeId.from("smithy.api#Long"), Type.Long)
        val Json: IrShape = IrShape(ShapeId.from("smithy.api#Document"), Type.Json)
    }
}

data class Field(
    val name: String,
    val type: IrShape,
    val required: Boolean,
    val hints: List<Hint>
)

data class PolymorphicDataKind(
    val kind: String,
    val shape: IrShape,
    val hints: List<Hint>
)

sealed interface Hint {
    data class Documentation(val string: String) : Hint
    data class Deprecated(val message: String) : Hint

    data class JsonRename(val name: String) : Hint
    object Unstable: Hint
}

