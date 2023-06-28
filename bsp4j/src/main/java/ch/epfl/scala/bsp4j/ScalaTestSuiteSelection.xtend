package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaTestSuiteSelection {
  @NonNull
  String className
  @NonNull
  List<String> tests

  new(@NonNull String className, @NonNull List<String> tests){
    this.className = className
    this.tests = tests
  }
}
