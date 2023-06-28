package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class InitializeBuildParams {
  @NonNull
  String rootUri
  @NonNull
  String displayName
  @NonNull
  String version
  @NonNull
  String bspVersion
  @NonNull
  BuildClientCapabilities capabilities
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory)
  Object data

  new(@NonNull String rootUri, @NonNull String displayName, @NonNull String version, @NonNull String bspVersion, @NonNull BuildClientCapabilities capabilities){
    this.rootUri = rootUri
    this.displayName = displayName
    this.version = version
    this.bspVersion = bspVersion
    this.capabilities = capabilities
  }
}
