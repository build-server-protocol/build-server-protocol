package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class TaskId {
  @NonNull
  String id
  List<String> parents

  new(@NonNull String id){
    this.id = id
  }
}
