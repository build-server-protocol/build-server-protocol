package bsp.codegen.common.util

fun String.camelToSnakeCase(): String {
  val pattern = "(?<=[^A-Z])[A-Z]".toRegex()
  return this.replace(pattern, "_$0").lowercase()
}

fun String.kebabToScreamingSnakeCase(): String {
  return this.replace('-', '_').uppercase()
}

fun String.kebabToSnakeCase(): String {
  return this.replace('-', '_').lowercase()
}

fun String.camelCaseUpperCamelCase(): String {
  return this.replaceFirstChar { it.uppercase() }
}

fun String.snakeToUpperCamelCase(): String {
  return this.toUpperCamelCase("_")
}

fun String.kebabToUpperCamelCase(): String {
  return this.toUpperCamelCase("-")
}

private fun String.toUpperCamelCase(splitStr: String): String {
  return this.lowercase().split(splitStr).joinToString("") { word ->
    word.replaceFirstChar { it.uppercase() }
  }
}
