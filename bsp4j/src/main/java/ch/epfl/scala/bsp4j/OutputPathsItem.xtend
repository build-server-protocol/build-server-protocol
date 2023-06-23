package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class OutputPathsItem {
  @NonNull
  BuildTargetIdentifier target
  @NonNull
  List<OutputPathItem> outputPaths

  new(@NonNull BuildTargetIdentifier target, @NonNull List<OutputPathItem> outputPaths){
    this.target = target
    this.outputPaths = outputPaths
  }
}