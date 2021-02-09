package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class CppBuildTarget {
  @NonNull String version
  @NonNull List<String> options
  CppCompiler compiler
  String cCompiler
  String cppCompiler
  new(@NonNull String version, @NonNull List<String> options, CppCompiler compiler, String cCompiler, String cppCompiler) {
    this.version = version
    this.options = options
    this.compiler = compiler
    this.cCompiler = cCompiler
    this.cppCompiler = cppCompiler
  }
}

