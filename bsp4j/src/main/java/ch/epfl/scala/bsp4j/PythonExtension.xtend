package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class PythonBuildTarget {
  String version
  String interpreter
  new(String version, String interpreter) {
    this.version = version
    this.interpreter = interpreter
  }
}

@JsonRpcData
class PythonOptionsParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class PythonOptionsResult {
  @NonNull List<PythonOptionsItem> items
  new(@NonNull List<PythonOptionsItem> items) {
    this.items = items
  }
}

@JsonRpcData
class PythonOptionsItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> interpreterOptions
  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> interpreterOptions) {
    this.target = target
    this.interpreterOptions = interpreterOptions
   }
}
