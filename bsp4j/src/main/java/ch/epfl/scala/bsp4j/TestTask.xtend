package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class TestTask {
  @NonNull
  BuildTargetIdentifier target

  new(@NonNull BuildTargetIdentifier target){
    this.target = target
  }
}