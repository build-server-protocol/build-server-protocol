package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class BuildTargetEvent {
  @NonNull
  BuildTargetIdentifier target
  BuildTargetEventKind kind
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory)
  Object data

  new(@NonNull BuildTargetIdentifier target){
    this.target = target
  }
}
