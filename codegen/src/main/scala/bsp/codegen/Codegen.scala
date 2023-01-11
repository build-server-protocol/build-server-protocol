package bsp.codegen

import software.amazon.smithy.model.Model

object Codegen {

  case class CodegenFile(path: os.RelPath, contents: String)

  def run(model: Model, packagePrefix: String): List[CodegenFile] = ???

}
