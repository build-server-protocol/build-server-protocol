package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class RustBuildTarget {
  String edition
  String compiler
  new(String edition, 
    String compiler) {
     this.edition = edition
     this.compiler = compiler
  }
}

@JsonRpcData
class RustOptionsParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class RustOptionsResult {
  @NonNull List<RustOptionsItem> items
  new(@NonNull List<RustOptionsItem> items) {
    this.items = items
  }
}

@JsonRpcData
class RustOptionsItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> compilerOptions
  new(@NonNull BuildTargetIdentifier target,
      @NonNull List<String> compilerOptions) {
    this.target = target
    this.compilerOptions = compilerOptions
   }
}
