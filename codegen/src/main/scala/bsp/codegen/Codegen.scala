package bsp.codegen

object Codegen {

  def run(outputDir: os.Path): List[os.Path] = {
    val model = ModelLoader.loadModel()
    val definitions = new SmithyToIR(model).definitions("bsp")
    val renderer = new JavaRenderer("ch.epfl.scala.bsp4j")
    val codegenFiles = definitions.map(renderer.render)

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

  def docs(namespace: String): String = {
    val model = ModelLoader.loadModel()
    val docTree = new SmithyToIR(model).docTree(namespace)
    MarkdownRenderer.render(docTree)
  }

  def printDocs(namespace: String): Unit = println(docs(namespace))

  def main(args: Array[String]): Unit = {
    printDocs("bsp")
  }
}
