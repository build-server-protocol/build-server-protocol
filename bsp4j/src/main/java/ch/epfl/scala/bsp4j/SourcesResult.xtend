package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class SourcesResult {
  @NonNull
  List<SourcesItem> items

  new(@NonNull List<SourcesItem> items){
    this.items = items
  }
}