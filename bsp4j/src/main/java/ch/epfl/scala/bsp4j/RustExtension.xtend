package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class RustBuildTarget {
  \\TODO add Rust-specific fields
  new() {
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
  \\TODO add Rust-specific fields
  new(@NonNull BuildTargetIdentifier target) {
    this.target = target
   }
}
