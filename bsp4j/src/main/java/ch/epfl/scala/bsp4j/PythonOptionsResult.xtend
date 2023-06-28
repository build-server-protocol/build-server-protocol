package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class PythonOptionsResult {
  @NonNull
  List<PythonOptionsItem> items

  new(@NonNull List<PythonOptionsItem> items){
    this.items = items
  }
}
