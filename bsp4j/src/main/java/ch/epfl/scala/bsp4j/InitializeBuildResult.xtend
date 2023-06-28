package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class InitializeBuildResult {
    @NonNull
    String displayName
    @NonNull
    String version
    @NonNull
    String bspVersion
    @NonNull
    BuildServerCapabilities capabilities
    String dataKind
    @JsonAdapter(JsonElementTypeAdapter.Factory)
    Object data

    new(@NonNull String displayName, @NonNull String version, @NonNull String bspVersion, @NonNull BuildServerCapabilities capabilities) {
        this.displayName = displayName
        this.version = version
        this.bspVersion = bspVersion
        this.capabilities = capabilities
    }
}
