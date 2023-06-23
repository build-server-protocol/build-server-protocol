package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class JvmRunEnvironmentResult {
  @NonNull
  List<JvmEnvironmentItem> items

  new(@NonNull List<JvmEnvironmentItem> items){
    this.items = items
  }
}