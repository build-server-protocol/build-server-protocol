package org.jetbrains.bsp.util

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.reflect.KClass

open class StringIntUnionSerializer<T : Any>(
    clazz: KClass<T>,
    private val stringSerializer: DeserializationStrategy<T>,
    private val intSerializer: DeserializationStrategy<T>
) : JsonContentPolymorphicSerializer<T>(clazz) {

    private val unsupportedTypeException =
        SerializationException("Unsupported type, value must be either a string or an integer")

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<T> =
        when {
            element is JsonPrimitive && element.jsonPrimitive.isString -> stringSerializer
            element is JsonPrimitive && element.intOrNull != null -> intSerializer
            else -> throw unsupportedTypeException
        }
}
