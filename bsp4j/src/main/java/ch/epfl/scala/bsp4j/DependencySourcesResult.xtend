package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class DependencySourcesResult {
  @NonNull
  List<DependencySourcesItem> items

  new(@NonNull List<DependencySourcesItem> items){
    this.items = items
  }
}