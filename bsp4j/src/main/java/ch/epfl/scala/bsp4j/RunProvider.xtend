package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class RunProvider {
  @NonNull
  List<String> languageIds

  new(@NonNull List<String> languageIds){
    this.languageIds = languageIds
  }
}
