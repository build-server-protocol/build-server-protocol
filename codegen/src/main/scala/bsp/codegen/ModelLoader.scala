package bsp.codegen

import software.amazon.smithy.model.Model

object ModelLoader {

  // By virtue of this module depending on the `spec` module, the models can be found on the classpath.
  def loadModel(): Model = Model.assembler().discoverModels().assemble().unwrap()

}
