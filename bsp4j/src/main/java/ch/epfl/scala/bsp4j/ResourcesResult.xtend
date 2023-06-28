package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ResourcesResult {
  @NonNull
  List<ResourcesItem> items

  new(@NonNull List<ResourcesItem> items){
    this.items = items
  }
}
