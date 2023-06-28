package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class TestFinish {
    @NonNull
    String displayName
    String message
    @NonNull
    TestStatus status
    Location location
    String dataKind
    @JsonAdapter(JsonElementTypeAdapter.Factory)
    Object data

    new(@NonNull String displayName, @NonNull TestStatus status){
        this.displayName = displayName
        this.status = status
    }
}
