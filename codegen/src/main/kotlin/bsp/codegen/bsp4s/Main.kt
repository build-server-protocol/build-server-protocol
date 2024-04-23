package bsp.codegen.bsp4s

import bsp.codegen.common.FilesGenerator
import bsp.codegen.common.Loader
import bsp.codegen.common.ir.AbstractionLevel
import bsp.codegen.common.ir.IrConfig
import bsp.codegen.common.ir.SmithyToIr
import bsp.codegen.common.ir.TypeAliasing
import kotlin.io.path.Path
import software.amazon.smithy.model.shapes.ShapeId

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    if (args.size != 1) {
      println("Usage: bsp4s <output directory>")
      return
    }

    val model = Loader.model

    val config =
        IrConfig(
            strings = TypeAliasing { it == ShapeId.fromParts("bsp", "URI") },
            maps = TypeAliasing.Pure,
            dataWithKind = AbstractionLevel.AsType,
            openEnums = AbstractionLevel.AsType,
            untaggedUnions = AbstractionLevel.AsType,
        )

    val ir = SmithyToIr(model, config)
    val namespaces = Loader.namespaces.toList()

    val definitions = namespaces.flatMap { ir.definitions(it) }.sortedBy { it.shapeId.name }
    val version = Loader.protocolVersion

    val scalaRenderer = ScalaRenderer("ch.epfl.scala.bsp", definitions, version)

    val codegenFiles = scalaRenderer.render()

    val output = Path(args[0])

    val generator = FilesGenerator(output, codegenFiles)
    generator.generateFiles()
  }
}
