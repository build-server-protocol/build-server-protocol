package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class CleanCacheResult {
  String message
  @NonNull
  Boolean cleaned

  new(@NonNull Boolean cleaned){
    this.cleaned = cleaned
  }
}
