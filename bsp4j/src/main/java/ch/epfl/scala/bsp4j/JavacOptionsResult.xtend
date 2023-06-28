package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class JavacOptionsResult {
  @NonNull
  List<JavacOptionsItem> items

  new(@NonNull List<JavacOptionsItem> items){
    this.items = items
  }
}
