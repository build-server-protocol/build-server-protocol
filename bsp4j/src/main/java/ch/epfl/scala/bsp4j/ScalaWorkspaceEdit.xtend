package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List

@JsonRpcData
class ScalaWorkspaceEdit {
  List<ScalaTextEdit> changes

  new(){
  }
}