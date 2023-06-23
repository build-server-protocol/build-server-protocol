package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class DiagnosticRelatedInformation {
  @NonNull
  Location location
  @NonNull
  String message

  new(@NonNull Location location, @NonNull String message){
    this.location = location
    this.message = message
  }
}