package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class TaskProgressParams {
  @NonNull
  TaskId taskId
  Long eventTime
  String message
  Long total
  Long progress
  String unit
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory)
  Object data

  new(@NonNull TaskId taskId){
    this.taskId = taskId
  }
}