package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaTestSuites {
    @NonNull
    List<ScalaTestSuiteSelection> suites
    @NonNull
    List<String> jvmOptions
    @NonNull
    List<String> environmentVariables

    new(@NonNull List<ScalaTestSuiteSelection> suites, @NonNull List<String> jvmOptions, @NonNull List<String> environmentVariables){
        this.suites = suites
        this.jvmOptions = jvmOptions
        this.environmentVariables = environmentVariables
    }
}
