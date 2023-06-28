package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaTestClassesResult {
  @NonNull
  List<ScalaTestClassesItem> items

  new(@NonNull List<ScalaTestClassesItem> items){
    this.items = items
  }
}
