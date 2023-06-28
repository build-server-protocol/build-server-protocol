package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class TaskStartParams {
  @NonNull
  TaskId taskId
  Long eventTime
  String message
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory)
  Object data

  new(@NonNull TaskId taskId){
    this.taskId = taskId
  }
}
