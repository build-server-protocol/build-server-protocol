package bsp.codegen.bsp4s

import bsp.codegen.{
  CodegenFile,
  ExtensionLoader,
  FilesGenerator,
  ModelLoader,
  ProtocolVersionLoader
}
import bsp.codegen.ir.SmithyToIR

object Codegen {
  def run(): List[CodegenFile] = {
    val model = ModelLoader.loadModel()
    val ir = new SmithyToIR(model)
    val namespaces = ExtensionLoader.namespaces()

    val definitions = namespaces.flatMap(ir.definitions).sortBy(_.shapeId.getName)
    val version = ProtocolVersionLoader.version()

    val scalaRenderer = new ScalaRenderer("ch.epfl.scala.bsp", definitions, version)

    scalaRenderer.render()
  }
}

object Main extends FilesGenerator(Codegen.run())
