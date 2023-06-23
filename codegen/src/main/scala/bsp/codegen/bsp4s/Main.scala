package bsp.codegen.bsp4s

import bsp.codegen.{ExtensionLoader, FilesGenerator, ModelLoader}
import bsp.codegen.ir.SmithyToIR

object Codegen {
  def run(outputDir: os.Path): List[os.Path] = {
    val model = ModelLoader.loadModel()
    val ir = new SmithyToIR(model)
    val namespaces = ExtensionLoader.namespaces()

    val definitions = namespaces.flatMap(ir.definitions)

    val scalaRenderer = new ScalaRenderer("ch.epfl.scala.bsp", definitions)

    val bspFileContents = scalaRenderer.renderDefinitions().render
    val endpointsFileContents = scalaRenderer.renderEndpoints().render

    val basePath = scalaRenderer.baseRelPath.resolveFrom(outputDir)
    val bspPath = basePath / "Bsp.scala"
    val endpointsPath = basePath / "endpoints" / "Endpoints.scala"

    os.write.over(bspPath, bspFileContents, createFolders = true)
    os.write.over(endpointsPath, endpointsFileContents, createFolders = true)

    List(bspPath, endpointsPath)
  }
}

object Main extends FilesGenerator(Codegen.run)
