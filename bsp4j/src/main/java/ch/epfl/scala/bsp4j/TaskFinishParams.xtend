package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class TaskFinishParams {
    @NonNull
    TaskId taskId
    Long eventTime
    String message
    @NonNull
    StatusCode status
    String dataKind
    @JsonAdapter(JsonElementTypeAdapter.Factory)
    Object data

    new(@NonNull TaskId taskId, @NonNull StatusCode status) {
        this.taskId = taskId
        this.status = status
    }
}
