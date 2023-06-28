package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.JsonAdapter
import java.util.List
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class Diagnostic {
  @NonNull
  Range range
  DiagnosticSeverity severity
  String code
  String source
  @NonNull
  String message
  List<DiagnosticRelatedInformation> relatedInformation
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory)
  Object data

  new(@NonNull Range range, @NonNull String message){
    this.range = range
    this.message = message
  }
}
