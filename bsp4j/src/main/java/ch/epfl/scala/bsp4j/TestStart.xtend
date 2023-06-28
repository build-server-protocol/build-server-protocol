package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class TestStart {
  @NonNull
  String displayName
  Location location

  new(@NonNull String displayName){
    this.displayName = displayName
  }
}
