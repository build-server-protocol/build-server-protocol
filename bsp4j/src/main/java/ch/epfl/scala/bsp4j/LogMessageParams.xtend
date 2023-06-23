package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class LogMessageParams {
  @NonNull
  MessageType type
  TaskId task
  String originId
  @NonNull
  String message

  new(@NonNull MessageType type, @NonNull String message){
    this.type = type
    this.message = message
  }
}