package ch.epfl.scala.bsp4j

import java.util.List
import com.google.gson.annotations.SerializedName
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class ScalaBuildTarget {
  @NonNull String scalaOrganization
  @NonNull String scalaVersion
  @NonNull String scalaBinaryVersion
  @NonNull ScalaPlatform platform
  @NonNull List<String> jars
  JvmBuildTarget jvmBuildTarget
  new(@NonNull String scalaOrganization, @NonNull String scalaVersion, @NonNull String scalaBinaryVersion,
      @NonNull ScalaPlatform platform, @NonNull List<String> jars) {
    this.scalaOrganization = scalaOrganization
    this.scalaVersion = scalaVersion
    this.scalaBinaryVersion = scalaBinaryVersion
    this.platform = platform
    this.jars = jars
  }
}

@JsonRpcData
class ScalaTestParams {
  List<ScalaTestClassesItem> testClasses
}

@JsonRpcData
class ScalacOptionsParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class ScalacOptionsResult {
  @NonNull List<ScalacOptionsItem> items
  new(@NonNull List<ScalacOptionsItem> items) {
    this.items = items
  }
}

@JsonRpcData
class ScalacOptionsItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> options
  @NonNull List<String> classpath
  @NonNull String classDirectory
  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> options, @NonNull List<String> classpath,
      @NonNull String classDirectory) {
    this.target = target
    this.options = options
    this.classpath = classpath
    this.classDirectory = classDirectory
   }
}

@JsonRpcData
class ScalaTestClassesParams {
  @NonNull List<BuildTargetIdentifier> targets
  String originId
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class ScalaTestClassesResult {
  @NonNull List<ScalaTestClassesItem> items
  new(@NonNull List<ScalaTestClassesItem> items) {
    this.items = items
  }
}

@JsonRpcData
class ScalaTestClassesItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> classes
  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> classes) {
    this.target = target
    this.classes = classes
  }
}

@JsonRpcData
class ScalaMainClassesParams {
  @NonNull List<BuildTargetIdentifier> targets
  String originId
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class ScalaMainClassesResult {
  @NonNull List<ScalaMainClassesItem> items
  new(@NonNull List<ScalaMainClassesItem> items) {
    this.items = items
  }
}

@JsonRpcData
class ScalaMainClassesItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<ScalaMainClass> classes
  new(@NonNull BuildTargetIdentifier target, @NonNull List<ScalaMainClass> classes) {
    this.target = target
    this.classes = classes
  }
}

@JsonRpcData
class ScalaMainClass {
  @NonNull @SerializedName("class") String className
  @NonNull List<String> arguments
  @NonNull List<String> jvmOptions
  new(@NonNull String className, @NonNull List<String> arguments, @NonNull List<String> jvmOptions) {
    this.className = className
    this.arguments = arguments
    this.jvmOptions = jvmOptions
  }
}
