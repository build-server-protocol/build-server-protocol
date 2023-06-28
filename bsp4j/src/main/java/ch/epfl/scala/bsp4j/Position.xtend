package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class Position {
  @NonNull
  Integer line
  @NonNull
  Integer character

  new(@NonNull Integer line, @NonNull Integer character){
    this.line = line
    this.character = character
  }
}
