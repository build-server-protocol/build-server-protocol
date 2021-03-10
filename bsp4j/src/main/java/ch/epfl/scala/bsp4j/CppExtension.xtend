package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class CppBuildTarget {
  @NonNull String version
  String compiler
  String cCompiler
  String cppCompiler
  new(@NonNull String version,
   String compiler,
   String cCompiler,
   String cppCompiler) {
    this.version = version
    this.compiler = compiler
    this.cCompiler = cCompiler
    this.cppCompiler = cppCompiler
  }
}

@JsonRpcData
class CppOptionsParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class CppOptionsResult {
  @NonNull List<CppOptionsItem> items
  new(@NonNull List<CppOptionsItem> items) {
    this.items = items
  }
}

@JsonRpcData
class CppOptionsItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> copts
  @NonNull List<String> defines
  @NonNull List<String> linkopts
  boolean linkshared
  new(@NonNull BuildTargetIdentifier target,
      @NonNull List<String> copts,
      @NonNull List<String> defines,
      @NonNull List<String> linkopts) {
    this.target = target
    this.copts = copts
    this.defines = defines
    this.linkopts = linkopts
    this.linkshared = linkshared
   }
}
