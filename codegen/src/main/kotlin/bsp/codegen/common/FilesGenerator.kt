package bsp.codegen.common

import java.nio.file.Path

class FilesGenerator(
    val output: Path,
    private val codegenFiles: List<CodegenFile>,
) {

  fun generateFiles() {
    if (codegenFiles.isEmpty()) {
      throw RuntimeException("No files to generate")
    }

    codegenFiles.forEach {
      val fullPath = output.resolve(it.path)
      fullPath.parent.toFile().mkdirs()
      fullPath.toFile().writeText(it.contents)
    }
  }
}
