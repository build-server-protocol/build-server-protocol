package org.jetbrains.bsp.generators.utils

fun String.camelToSnakeCase(): String {
    val pattern = "(?<=[^A-Z])[A-Z]".toRegex()
    return this.replace(pattern, "_$0").lowercase()
}

fun String.kebabToScreamingSnakeCase(): String {
    return this.replace('-', '_').uppercase()
}

fun String.snakeToUpperCamelCase(): String {
    return this.toUpperCamelCase("_")
}

fun String.kebabToUpperCamelCase(): String {
    return this.toUpperCamelCase("-")
}

private fun String.toUpperCamelCase(splitStr: String): String {
    return this.lowercase().split(splitStr).joinToString("") { word ->
        word.replaceFirstChar {
            it.uppercase()
        }
    }
}
