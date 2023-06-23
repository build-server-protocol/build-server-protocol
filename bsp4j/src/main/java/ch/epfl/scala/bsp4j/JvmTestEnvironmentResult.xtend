package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class JvmTestEnvironmentResult {
  @NonNull
  List<JvmEnvironmentItem> items

  new(@NonNull List<JvmEnvironmentItem> items){
    this.items = items
  }
}