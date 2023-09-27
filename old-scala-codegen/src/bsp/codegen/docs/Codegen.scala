package bsp.codegen.docs

import bsp.codegen.ir.SmithyToIR
import org.jetbrains.bsp.generators.Loader

object Codegen {
  def docs(namespace: String): String = {
    val model = Loader.INSTANCE.getModel
    val docTree = new SmithyToIR(model).docTree(namespace)
    MarkdownRenderer.render(docTree)
  }

  def printDocs(namespace: String): Unit = println(docs(namespace))
}
