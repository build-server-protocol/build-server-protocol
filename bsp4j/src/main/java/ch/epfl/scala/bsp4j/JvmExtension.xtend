package ch.epfl.scala.bsp4j

import java.util.Map
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class JvmBuildTarget {
  String javaHome
  String javaVersion
  new(String javaHome, String javaVersion) {
    this.javaHome = javaHome
    this.javaVersion = javaVersion
  }
}

@JsonRpcData
class JvmTestEnvironmentParams {
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
class JvmTestEnvironmentResult {
  @NonNull List<JvmEnvironmentItem> items
  new(@NonNull List<JvmEnvironmentItem> items) {
    this.items = items
  }
}

