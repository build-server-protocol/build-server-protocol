package bsp.codegen.docs

import bsp.codegen.common.dsl.CodeBlock
import bsp.codegen.common.dsl.code
import bsp.codegen.common.ir.Def
import bsp.codegen.common.ir.EnumType
import bsp.codegen.common.ir.EnumValue
import bsp.codegen.common.ir.Field
import bsp.codegen.common.ir.Hint
import bsp.codegen.common.ir.Type
import bsp.codegen.common.util.snakeToUpperCamelCase
import kotlin.math.max

class TypescriptRenderer {
  fun render(definition: Def): CodeBlock? {
    return when (definition) {
      is Def.Alias -> renderAlias(definition)
      is Def.Structure -> renderStructure(definition)
      is Def.ClosedEnum<*> -> renderClosedEnum(definition)
      is Def.OpenEnum<*> -> renderOpenEnum(definition)
      is Def.Service -> throw RuntimeException("Service should not be rendered")
      is Def.DataKinds -> throw RuntimeException("DataKinds should not be rendered")
      is Def.UntaggedUnion -> throw RuntimeException("UntaggedUnion should not be rendered")
    }
  }

  fun renderAlias(definition: Def.Alias): CodeBlock = code {
    -"export type ${definition.shapeId.name} = ${renderType(definition.aliasedType)};"
  }

  private fun <T> List<T>.splitAtLast(): Pair<List<T>, List<T>> {
    val sizeWithoutLast = max(0, this.size - 1)
    return Pair(this.take(sizeWithoutLast), this.takeLast(1))
  }

  fun renderStructure(definition: Def.Structure): CodeBlock = code {
    val (withoutLast, last) = definition.fields.splitAtLast()
    block("export interface ${definition.shapeId.name}") {
      withoutLast.forEach {
        include(renderDocumentation(it.hints))
        -renderTSField(it)
        newline()
      }
      last.forEach {
        include(renderDocumentation(it.hints))
        -renderTSField(it)
      }
    }
  }

  fun renderClosedEnum(definition: Def.ClosedEnum<*>): CodeBlock = code {
    block("export enum ${definition.shapeId.name}") {
      definition.values.forEach {
        include(renderDocumentation(it.hints))
        -"${renderStaticValue(definition.enumType)(it)},"
        newline()
      }
    }
  }

  fun renderOpenEnum(definition: Def.OpenEnum<*>): CodeBlock = code {
    val enumType =
        renderType(
            when (definition.enumType) {
              is EnumType.IntEnum -> Type.Int
              is EnumType.StringEnum -> Type.String
            })
    -"export type ${definition.shapeId.name} = $enumType;"
    newline()
    block("export namespace ${definition.shapeId.name}") {
      definition.values.forEach {
        include(renderDocumentation(it.hints))
        -"export const ${renderStaticValue(definition.enumType)(it)};"
        newline()
      }
    }
  }

  fun renderStaticValue(enumType: EnumType<*>): (EnumValue<*>) -> String = { value ->
    when (enumType) {
      is EnumType.IntEnum -> "${value.name.snakeToUpperCamelCase()} = ${value.value}"
      is EnumType.StringEnum -> "${value.name.snakeToUpperCamelCase()} = \"${value.value}\""
    }
  }

  fun renderTSField(field: Field): String =
      "${field.name}${"?".takeUnless { field.required } ?: ""}: ${renderType(field.type)};"

  fun renderType(tpe: Type): String =
      when (tpe) {
        is Type.Ref -> tpe.shapeId.name
        Type.Bool -> "boolean"
        Type.Int -> "number"
        Type.Json -> "any"
        is Type.List -> "${renderType(tpe.member)}[]"
        Type.Long -> "number"
        is Type.Map -> "Map<${renderType(tpe.key)}, ${renderType(tpe.value)}>"
        is Type.Set -> "Set<${renderType(tpe.member)}>"
        Type.String -> "string"
        Type.Unit -> "void"
        is Type.UntaggedUnion -> tpe.members.joinToString(" | ") { renderType(it) }
      }

  fun renderDocumentation(hints: List<Hint>): CodeBlock = code {
    val lines =
        hints.flatMap {
          when (it) {
            is Hint.Documentation -> it.string.split(System.lineSeparator())
            is Hint.Deprecated -> listOf("Deprecated: ${it.message}")
            Hint.Unstable -> listOf("Unstable: may change in the future")
            is Hint.JsonRename -> listOf()
          }
        }

    val commentLines = lines.toMutableList()

    if (commentLines.isNotEmpty()) {
      commentLines[0] = "/** ${commentLines[0]}"
      val lastIndex = commentLines.size - 1
      for (i in 1..lastIndex) {
        commentLines[i] = " * ${commentLines[i]}"
      }
      commentLines[lastIndex] = "${commentLines[lastIndex]} */"
    }

    commentLines.forEach { -it }
  }
}
