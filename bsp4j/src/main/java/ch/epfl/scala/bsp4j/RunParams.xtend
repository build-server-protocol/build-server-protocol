package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import java.util.List
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class RunParams {
    @NonNull
    BuildTargetIdentifier target
    String originId
    List<String> arguments
    String dataKind
    @JsonAdapter(JsonElementTypeAdapter.Factory)
    Object data

    new(@NonNull BuildTargetIdentifier target){
        this.target = target
    }
}
