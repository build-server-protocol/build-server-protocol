package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import java.util.Map
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class JvmEnvironmentItem {
    @NonNull
    BuildTargetIdentifier target
    @NonNull
    List<String> classpath
    @NonNull
    List<String> jvmOptions
    @NonNull
    String workingDirectory
    @NonNull
    Map<String, String> environmentVariables
    List<JvmMainClass> mainClasses

    new(@NonNull BuildTargetIdentifier target, @NonNull List<String> classpath, @NonNull List<String> jvmOptions, @NonNull String workingDirectory, @NonNull Map<String, String> environmentVariables){
        this.target = target
        this.classpath = classpath
        this.jvmOptions = jvmOptions
        this.workingDirectory = workingDirectory
        this.environmentVariables = environmentVariables
    }
}
