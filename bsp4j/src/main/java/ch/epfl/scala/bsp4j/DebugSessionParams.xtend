package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import java.util.List
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class DebugSessionParams {
    @NonNull
    List<BuildTargetIdentifier> targets
    String dataKind
    @JsonAdapter(JsonElementTypeAdapter.Factory)
    Object data

    new(@NonNull List<BuildTargetIdentifier> targets){
        this.targets = targets
    }
}
