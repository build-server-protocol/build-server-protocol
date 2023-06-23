package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class DependencyModulesItem {
  @NonNull
  BuildTargetIdentifier target
  @NonNull
  List<DependencyModule> modules

  new(@NonNull BuildTargetIdentifier target, @NonNull List<DependencyModule> modules){
    this.target = target
    this.modules = modules
  }
}