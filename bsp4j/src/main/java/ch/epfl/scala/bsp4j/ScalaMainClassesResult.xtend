package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaMainClassesResult {
  @NonNull
  List<ScalaMainClassesItem> items
  String originId

  new(@NonNull List<ScalaMainClassesItem> items){
    this.items = items
  }
}
