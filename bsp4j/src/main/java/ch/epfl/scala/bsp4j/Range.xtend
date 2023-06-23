package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class Range {
  @NonNull
  Position start
  @NonNull
  Position end

  new(@NonNull Position start, @NonNull Position end){
    this.start = start
    this.end = end
  }
}