package bsp.codegen.docs

import bsp.codegen.ModelLoader
import bsp.codegen.ir.SmithyToIR

object Codegen {
  def docs(namespace: String): String = {
    val model = ModelLoader.loadModel()
    val docTree = new SmithyToIR(model).docTree(namespace)
    MarkdownRenderer.render(docTree)
  }

  def printDocs(namespace: String): Unit = println(docs(namespace))
}
