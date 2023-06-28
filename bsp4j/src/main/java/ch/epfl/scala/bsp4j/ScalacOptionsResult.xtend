package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalacOptionsResult {
  @NonNull
  List<ScalacOptionsItem> items

  new(@NonNull List<ScalacOptionsItem> items){
    this.items = items
  }
}
