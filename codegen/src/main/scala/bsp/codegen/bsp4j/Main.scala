package bsp.codegen.bsp4j

import bsp.codegen.bsp4j.Codegen.run
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
    val namespaces = ExtensionLoader.namespaces()
    val ir = new SmithyToIR(model)
    val definitions = namespaces.flatMap(ir.definitions)
    val version = ProtocolVersionLoader.version()
    val renderer = new JavaRenderer("ch.epfl.scala.bsp4j", definitions, version)
    renderer.render()
  }
}

object Main extends FilesGenerator(run()) {}
