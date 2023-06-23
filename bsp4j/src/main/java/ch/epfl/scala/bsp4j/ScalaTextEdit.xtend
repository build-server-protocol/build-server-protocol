package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaTextEdit {
  @NonNull
  Range range
  @NonNull
  String newText

  new(@NonNull Range range, @NonNull String newText){
    this.range = range
    this.newText = newText
  }
}