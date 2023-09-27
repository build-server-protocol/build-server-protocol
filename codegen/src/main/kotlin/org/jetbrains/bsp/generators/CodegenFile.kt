package org.jetbrains.bsp.generators

import java.nio.file.Path

data class CodegenFile(val path: Path, val contents: String)
