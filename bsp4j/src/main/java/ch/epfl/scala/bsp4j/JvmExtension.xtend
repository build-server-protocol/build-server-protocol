package ch.epfl.scala.bsp4j

import java.util.Map
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class JvmEnvironmentParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class JvmEnvironmentItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> classpath
  @NonNull List<String> jvmOptions
  @NonNull String workingDirectory
  @NonNull Map<String, String> environmentVariables
  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> classpath, @NonNull List<String> jvmOptions,
      @NonNull String workingDirectory, @NonNull Map<String,String> environmentVariables) {
    this.target = target
    this.classpath = classpath
    this.jvmOptions = jvmOptions
    this.workingDirectory = workingDirectory
    this.environmentVariables = environmentVariables
  }
}

@JsonRpcData
class JvmEnvironmentResult {
  @NonNull List<JvmEnvironmentItem> items
  new(@NonNull List<JvmEnvironmentItem> items) {
    this.items = items
  }
}

