package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaMainClassesItem {
  @NonNull
  BuildTargetIdentifier target
  @NonNull
  List<ScalaMainClass> classes

  new(@NonNull BuildTargetIdentifier target, @NonNull List<ScalaMainClass> classes){
    this.target = target
    this.classes = classes
  }
}
