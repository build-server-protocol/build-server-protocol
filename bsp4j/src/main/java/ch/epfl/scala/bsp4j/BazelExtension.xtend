package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class BazelDependenciesParams {
  @NonNull List<BuildTargetIdentifier> targets
  String originId
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class BazelDependenciesResult {
  @NonNull List<BazelDependenciesItem> items
  new(@NonNull List<BazelDependenciesItem> items) {
    this.items = items
  }
}

@JsonRpcData
class BazelDependenciesItem {
  @NonNull BuildTargetIdentifier target
  String packageNamePrefix
  new(@NonNull BuildTargetIdentifier target, String packageNamePrefix) {
    this.target = target
    this.packageNamePrefix = packageNamePrefix
  }
}
