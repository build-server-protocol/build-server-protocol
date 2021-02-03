package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class CppBuildTarget {
  @NonNull CppPlatform platform
  String cCompiler
  String cppCompiler
  new(@NonNull CppPlatform platform, String cCompiler, String cppCompiler) {
    this.platform = platform
    this.cCompiler = cCompiler
    this.cppCompiler = cppCompiler
  }
}

@JsonRpcData
class CppDependenciesSourcesParams {
  @NonNull List<BuildTargetIdentifier> targets
  String originId
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class CppDependenciesSourcesResult {
  @NonNull List<CppDependenciesSourcesItem> items
  new(@NonNull List<CppDependenciesSourcesItem> items) {
    this.items = items
  }
}

@JsonRpcData
class CppDependenciesSourcesItem {
  @NonNull BuildTargetIdentifier target
  String packageNamePrefix
  new(@NonNull BuildTargetIdentifier target) {
    this.target = target
  }
}
