package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class DependencySourcesItem {
  @NonNull
  BuildTargetIdentifier target
  @NonNull
  List<String> sources

  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> sources){
    this.target = target
    this.sources = sources
  }
}
