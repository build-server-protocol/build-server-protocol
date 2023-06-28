package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import java.util.List
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class BuildTarget {
    @NonNull
    BuildTargetIdentifier id
    String displayName
    String baseDirectory
    @NonNull
    List<String> tags
    @NonNull
    List<String> languageIds
    @NonNull
    List<BuildTargetIdentifier> dependencies
    @NonNull
    BuildTargetCapabilities capabilities
    String dataKind
    @JsonAdapter(JsonElementTypeAdapter.Factory)
    Object data

    new(@NonNull BuildTargetIdentifier id, @NonNull List<String> tags, @NonNull List<String> languageIds, @NonNull List<BuildTargetIdentifier> dependencies, @NonNull BuildTargetCapabilities capabilities){
        this.id = id
        this.tags = tags
        this.languageIds = languageIds
        this.dependencies = dependencies
        this.capabilities = capabilities
    }
}
