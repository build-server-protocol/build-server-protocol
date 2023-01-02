package ch.epfl.scala.bsp4j

import java.util.Map
import java.util.List
import com.google.gson.annotations.SerializedName
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
  String originId

  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
    this.originId = null
  }

  new(@NonNull List<BuildTargetIdentifier> targets, String originId) {
    this.targets = targets
    this.originId = originId
  }
}

@JsonRpcData
class JvmMainClass {
  @NonNull String className
  @NonNull List<String> arguments
    new(@NonNull String className, @NonNull List<String> arguments) {
    this.className = className
    this.arguments = arguments
  }
}

@JsonRpcData
class JvmEnvironmentItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> classpath
  @NonNull List<String> jvmOptions
  @NonNull String workingDirectory
  @NonNull Map<String, String> environmentVariables
  List<JvmMainClass> mainClasses
  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> classpath, @NonNull List<String> jvmOptions,
      @NonNull String workingDirectory, @NonNull Map<String,String> environmentVariables) {
    this.target = target
    this.classpath = classpath
    this.jvmOptions = jvmOptions
    this.workingDirectory = workingDirectory
    this.environmentVariables = environmentVariables
    this.mainClasses = mainClasses
  }
}

@JsonRpcData
class JvmTestEnvironmentResult {
  @NonNull List<JvmEnvironmentItem> items
  new(@NonNull List<JvmEnvironmentItem> items) {
    this.items = items
  }
}


@JsonRpcData
class JvmRunEnvironmentParams {
  @NonNull List<BuildTargetIdentifier> targets
  String originId

  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
    this.originId = null
  }

  new(@NonNull List<BuildTargetIdentifier> targets, String originId) {
    this.targets = targets
    this.originId = originId
  }
}

@JsonRpcData
class JvmRunEnvironmentResult {
  @NonNull List<JvmEnvironmentItem> items
  new(@NonNull List<JvmEnvironmentItem> items) {
    this.items = items
  }
}
