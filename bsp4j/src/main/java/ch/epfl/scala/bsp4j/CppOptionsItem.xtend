package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class CppOptionsItem {
  @NonNull
  BuildTargetIdentifier target
  @NonNull
  List<String> copts
  @NonNull
  List<String> defines
  @NonNull
  List<String> linkopts
  Boolean linkshared

  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> copts, @NonNull List<String> defines, @NonNull List<String> linkopts){
    this.target = target
    this.copts = copts
    this.defines = defines
    this.linkopts = linkopts
  }
}