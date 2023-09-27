package bsp.codegen.bsp4s

import bsp.codegen.ir.SmithyToIR
import org.jetbrains.bsp.generators.Loader
import org.jetbrains.bsp.generators.FilesGenerator

import java.io.File
import java.nio.file.Path
import java.util
import scala.jdk.CollectionConverters._

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("Usage: bsp4j <name> <output directory> <generator script path>")
      return
    }

    val model = Loader.INSTANCE.getModel
    val ir = new SmithyToIR(model)
    val namespaces = Loader.INSTANCE.getNamespaces.asScala.toList

    val definitions = namespaces.flatMap(ir.definitions).sortBy(_.shapeId.getName)
    val version = Loader.INSTANCE.getProtocolVersion

    val scalaRenderer = new ScalaRenderer("ch.epfl.scala.bsp", definitions, version)

    val codegenFiles = scalaRenderer.render().asJava

    val name = args(0)
    val output = Path.of(args(1))
    val generatorScript = new File(args(2))

    val generator = new FilesGenerator(name, output, generatorScript, codegenFiles, new util.ArrayList[String]())
    generator.generateFiles()
    generator.writeScript()
  }
}
