package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ResourcesItem {
  @NonNull
  BuildTargetIdentifier target
  @NonNull
  List<String> resources

  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> resources){
    this.target = target
    this.resources = resources
  }
}