package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class CppBuildTarget {
  @NonNull String version
  @NonNull List<String> copts
  @NonNull List<String> defines
  @NonNull List<String> linkopts
  boolean linkshared
  String compiler
  String cCompiler
  String cppCompiler
  new(@NonNull String version,
   @NonNull List<String> copts,
   @NonNull List<String> defines,
   @NonNull List<String> linkopts,
   String compiler,
   String cCompiler,
   String cppCompiler) {
    this.version = version
    this.copts = copts
    this.defines = defines
    this.linkopts = linkopts
    this.linkshared = linkshared
    this.compiler = compiler
    this.cCompiler = cCompiler
    this.cppCompiler = cppCompiler
  }
}

