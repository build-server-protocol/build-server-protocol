package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class PythonOptionsItem {
  @NonNull
  BuildTargetIdentifier target
  @NonNull
  List<String> interpreterOptions

  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> interpreterOptions){
    this.target = target
    this.interpreterOptions = interpreterOptions
  }
}