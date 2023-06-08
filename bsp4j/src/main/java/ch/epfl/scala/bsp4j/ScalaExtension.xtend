package ch.epfl.scala.bsp4j

import java.util.List
import com.google.gson.annotations.SerializedName
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData
import ch.epfl.scala.bsp4j.Range

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
  List<String> jvmOptions
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
  String framework
  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> classes) {
    this.target = target
    this.classes = classes
    this.framework = null
  }

  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> classes, String framework) {
    this.target = target
    this.classes = classes
    this.framework = framework
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
  List<String> environmentVariables
  new(@NonNull String className, @NonNull List<String> arguments, @NonNull List<String> jvmOptions) {
    this.className = className
    this.arguments = arguments
    this.jvmOptions = jvmOptions
  }
}

@JsonRpcData
class ScalaTestSuites {
  @NonNull List<ScalaTestSuiteSelection> suites
  /**
   * Additional jvmOptions which will be passed to the forked JVM
   */
  @NonNull List<String> jvmOptions
  /**
   * Enviroment variables should be an array of strings in format KEY=VALUE
   */
  @NonNull List<String> environmentVariables
  new(@NonNull List<ScalaTestSuiteSelection> suites, @NonNull List<String> jvmOptions, @NonNull List<String> environmentVariables) {
    this.suites = suites
    this.jvmOptions = jvmOptions
    this.environmentVariables = environmentVariables
  }
}

@JsonRpcData
class ScalaTestSuiteSelection {
  /**
    * Fully qualified name of the test suite class
    */
  @NonNull String className
  /**
    * List of tests which should be run within this test suite.
    * Empty collection means that all of them are supposed to be executed.
    */
  @NonNull List<String> tests

  new(@NonNull String className, @NonNull List<String> tests) {
    this.className = className
    this.tests = tests
  }
}

@JsonRpcData
class ScalaDiagnostic {
  List<ScalaAction> actions
}

@JsonRpcData
class ScalaAction {
  @NonNull String title
  String description
  ScalaWorkspaceEdit edit
  new(@NonNull String title) {
    this.title = title
  }
}

@JsonRpcData
class ScalaWorkspaceEdit {
  List<ScalaTextEdit> changes
  new(List<ScalaTextEdit> changes) {
    this.changes = changes
  }
}

@JsonRpcData
class ScalaTextEdit {
  @NonNull Range range
  @NonNull String newText
  new(@NonNull Range range, @NonNull String newText) {
    this.range = range
    this.newText = newText
  }
}
