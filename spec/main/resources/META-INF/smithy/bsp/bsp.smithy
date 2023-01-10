$version: "2"

namespace bsp

use jsonrpc#jsonRPC
use jsonrpc#enumKind

@jsonRPC
service BuildServer {
}


///  A resource identifier that is a valid URI according
/// to rfc3986: * https://tools.ietf.org/html/rfc3986
string URI

list URIs {
  member: URI
}

/// Represents an arbitrary piece of data, in Json format
document Json

/// A unique identifier for a target, can use any URI-compatible encoding as long as it is unique within the workspace.
/// Clients should not infer metadata out of the URI structure such as the path or query parameters, use BuildTarget instead.
structure BuildTargetIdentifier {
  /// The target’s Uri
  uri: URI
}

list BuildTargetIdentifiers {
  member: BuildTargetIdentifier
}

/// Build target contains metadata about an artifact (for example library, test, or binary artifact). Using vocabulary of other build tools:
///
/// * sbt: a build target is a combined project + config. Example:
///   * a regular JVM project with main and test configurations will have 2 build targets, one for main and one for test.
///   * a single configuration in a single project that contains both Java and Scala sources maps to one BuildTarget.
///   * a project with crossScalaVersions 2.11 and 2.12 containing main and test configuration in each will have 4 build targets.
///   * a Scala 2.11 and 2.12 cross-built project for Scala.js and the JVM with main and test configurations will have 8 build targets.
/// * Pants: a pants target corresponds one-to-one with a BuildTarget
/// * Bazel: a bazel target corresponds one-to-one with a BuildTarget
///
/// The general idea is that the BuildTarget data structure should contain only information that is fast or cheap to compute.
structure BuildTarget {
  /// The target’s unique identifier
  @required
  id: BuildTargetIdentifier

  /// A human readable name for this target.
  /// May be presented in the user interface.
  /// Should be unique if possible.
  /// The id.uri is used if None.
  displayName: String

  /// The directory where this target belongs to. Multiple build targets are allowed to map
  /// to the same base directory, and a build target is not required to have a base directory.
  /// A base directory does not determine the sources of a target, see buildTarget/sources. */
  baseDirectory: URI

  /// Free-form string tags to categorize or label this build target.
  /// For example, can be used by the client to:
  /// - customize how the target should be translated into the client's project model.
  /// - group together different but related targets in the user interface.
  /// - display icons or colors in the user interface.
  /// Pre-defined tags are listed in `BuildTargetTag` but clients and servers
  /// are free to define new tags for custom purposes.
  tags: BuildTargetTags

  /// The direct upstream build target dependencies of this build target
  dependencies: BuildTargetIdentifiers

  /// Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified.
  dataKind: BuildTargetDataKind

  /// Language-specific metadata about this target.
  /// See ScalaBuildTarget as an example.
  data: Json
}

@enumKind("open")
enum BuildTargetDataKind {
  /// The `data` field contains a `ScalaBuildTarget` object
  SCALA = "scala"

  /// The `data` field contains a `SbtBuildTarget` object.
  SBT = "sbt"
}

list StringList {
  member: String
}

structure BuildTargetCapabilities {
  /// This target can be compiled by the BSP server.
  @required
  canCompile: Boolean
  /// This target can be tested by the BSP server.
  @required
  canTest: Boolean
  /// This target can be run by the BSP server.
  @required
  canRun: Boolean
  /// This target can be debugged by the BSP server.
  @required
  canDebug: Boolean
}


@enumKind("open")
enum BuildTargetTag {
  /// Target contains re-usable functionality for downstream targets. May have any
  /// combination of capabilities.
  LIBRARY = "library"

  /// Target contains source code for producing any kind of application, may have
  /// but does not require the `canRun` capability.
  APPLICATION = "application"

  /// Target contains source code for testing purposes, may have but does not
  /// require the `canTest` capability.
  TEST = "test"

  /// Target contains source code for integration testing purposes, may have
  /// but does not require the `canTest` capability.
  /// The difference between "test" and "integration-test" is that
  /// integration tests traditionally run slower compared to normal tests
  /// and require more computing resources to execute.
  INTEGRATION_TEST = "integration-test"

  /// Target contains source code to measure performance of a program, may have
  /// but does not require the `canRun` build target capability.
  BENCHMARK = "benchmark"

  /// Target should be ignored by IDEs.
  NO_IDE = "no-ide"

  /// Actions on the target such as build and test should only be invoked manually
  /// and explicitly. For example, triggering a build on all targets in the workspace
  /// should by default not include this target.
  ///
  /// The original motivation to add the "manual" tag comes from a similar functionality
  /// that exists in Bazel, where targets with this tag have to be specified explicitly
  /// on the command line.
  ///
  MANUAL = "manual"
}

list BuildTargetTags {
  member: BuildTargetTag
}

/// The Task Id allows clients to uniquely identify a BSP task and establish a client-parent relationship with another task id.
structure TaskId {
  /// A unique identifier
  id: Identifier

  // The parent task ids, if any. A non-empty parents field means
  // this task is a sub-task of every parent task id. The child-parent
  // relationship of tasks makes it possible to render tasks in
  // a tree-like user interface or inspect what caused a certain task
  // execution.
  parents: Identifiers
}

string Identifier
list Identifiers {
  member: Identifier
}

/// Included in notifications of tasks or requests to signal the completion state.
@enumKind("closed")
intEnum StatusCode {
  /// Execution was successful.
  OK = 1
  /// Execution failed.
  ERROR = 2
  ///
  CANCELLED = 3
}

structure JvmBuildTarget {
  @required
  javaHome: String
  @required
  javaVersion: String
}

structure ScalaBuildTarget {
  @required
  scalaOrganization: String

  @required
  scalaVersion: String

  @required
  scalaBinaryVersion: String

  @required
  platform: ScalaPlatform

  @required
  jars: URIs

  jvmBuildTarget: JvmBuildTarget
}

@enumKind("closed")
intEnum ScalaPlatform {
  JVM = 1
  JS = 2
  NATIVE = 3
}
