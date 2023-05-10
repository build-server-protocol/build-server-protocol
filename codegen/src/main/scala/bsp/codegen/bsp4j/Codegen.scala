package bsp.codegen.bsp4j

import bsp.codegen.ModelLoader
import bsp.codegen.ir.SmithyToIR

object Codegen {

  def run(outputDir: os.Path): List[os.Path] = {
    val model = ModelLoader.loadModel()
    val definitions = new SmithyToIR(model).definitions("bsp")
    val renderer = new JavaRenderer("ch.epfl.scala.bsp4j")
    val codegenFiles = definitions.flatMap(renderer.render)

    val preconditionsPath = outputDir / "org" / "eclipse" / "lsp4j" / "util" / "Preconditions.java"
    os.copy.over(
      os.pwd / "codegen" / "src" / "main" / "resources" / "Preconditions.java",
      preconditionsPath,
      createFolders = true
    )
    // TODO: dehardcode "codegen" path above
    // Ughhh, for some reason extend expects this file to be present in this specific location

    codegenFiles.map { file =>
      val fullPath = outputDir / file.path
      os.write.over(fullPath, file.contents, createFolders = true)
      fullPath
    }
  }

}
