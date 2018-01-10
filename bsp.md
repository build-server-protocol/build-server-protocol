# 1. Build Server Protocol

<!-- TOC -->

* [1. Build Server Protocol](#1-build-server-protocol)
  * [1.1. Base protocol](#11-base-protocol)
  * [1.2. Basic Json Structures](#12-basic-json-structures)
    * [1.2.1. Build Target](#121-build-target)
    * [1.2.2. Build Target Identifier](#122-build-target-identifier)
  * [1.3. Actual Protocol](#13-actual-protocol)
    * [1.3.1. Server Lifetime](#131-server-lifetime)
      * [1.3.1.1. Initialize Build Request](#1311-initialize-build-request)
      * [1.3.1.2. Initialized Build Notification](#1312-initialized-build-notification)
      * [1.3.1.3. Shutdown Build Request](#1313-shutdown-build-request)
      * [1.3.1.4. Exit Build Notification](#1314-exit-build-notification)
    * [1.3.2. Workspace Build Targets Request](#132-workspace-build-targets-request)
    * [1.3.3. Build Target Changed Notification](#133-build-target-changed-notification)
    * [1.3.4. Dependency Classpath Request](#134-dependency-classpath-request)
    * [1.3.5. Build Target Text Documents Request](#135-build-target-text-documents-request)
    * [1.3.6. Text Document Build Targets Request](#136-text-document-build-targets-request)
    * [1.3.7. Dependency Sources Request](#137-dependency-sources-request)
    * [1.3.8. Compile Request](#138-compile-request)
  * [1.4. Extensions](#14-extensions)
    * [1.4.1. Scala](#141-scala)
      * [1.4.1.1. Scala Build Target](#1411-scala-build-target)
      * [1.4.1.2. Scalac Options Request](#1412-scalac-options-request)

<!-- /TOC -->

This document describes version 0.x of the build server protocol (BSP).
The build server protocol takes inspiration from the language server
protocol (LSP). Unlike in the language server protocol, the language
server is referred to as the “client” and a build tool such as
sbt/pants/gradle/bazel is referred to as the “server”.

## 1.1. Base protocol

The base protocol is identical to the language server base protocol. See
<https://microsoft.github.io/language-server-protocol/specification> for
more details.

Like the language server protocol, the build server protocol defines a
set of JSON-RPC request, response and notification messages which are
exchanged using the base protocol.

## 1.2. Basic Json Structures

In addition to basic data structures in the language server protocol,
the build server protocol defines the following additional data
structures.

### 1.2.1. Build Target

Build target contains static metadata about an artifact (for example library, test, or binary artifact).
Compared to the build tools:

* sbt: a build target is a combined project + config. Example:
  * a regular JVM project with main and test configurations will have
    two build targets, one for main and one for test.
  * a single configuration in a single project that contains both Java and Scala sources maps to one BuildTarget.
  * a project with crossScalaVersions 2.11 and 2.12 containing main and test configuration in each will have four build targets.
  * a Scala 2.11 and 2.12 cross-built project for Scala.js and the JVM with main and test configurations will have six build targets.
* pants: a pants target corresponds one-to-one with a BuildTarget
* bazel: a bazel target corresponds one-to-one with a BuildTarget

The general idea is that the BuildTarget data structure should contain only information that is is fast or cheap to compute.

```scala
trait BuildTarget {

  /** The target’s unique identifier */
  def id: BuildTargetIdentifier

  /** A human readable name for this target. May be presented in the user interface. Should be unique if possible. The id.uri is used if None. */
  def name: Option[String]

  /** The set of languages that this target contains */
  def languageIds: List[String]

  /** Language-specific metadata about this target. See ScalaBuildTarget as an example. */
  def data: Option[Json] // Note, `Json` is represented as any in the LSP.
}
```

### 1.2.2. Build Target Identifier

A unique identifier for a target.

```scala
trait BuildTargetIdentifer {
  /** The target’s URI */
  def uri: URI
}
```

## 1.3. Actual Protocol

Unlike the language server protocol, the build server protocol does not
support dynamic registration of capabilities.
The motivation for this change is simplicity.
If a motivating example for dynamic registration comes this difference can be reconsidered.
The server and client capabilities must be communicated through the initialize request.

### 1.3.1. Server Lifetime

Like the language server protocol, the current protocol specification
defines that the lifetime of a build server is managed by the client
(e.g. a language server like Dotty IDE). It is up to the client to
decide when to start (process-wise) and when to shutdown a server.

#### 1.3.1.1. Initialize Build Request

Like the language server protocol, the initialize request is sent as the
first request from the client to the server. If the server receives a
request or notification before the initialize request it should act as
follows:

* For a request the response should be an error with code: -32002.
  The message can be picked by the server.

* Notifications should be dropped, except for the exit notification.
  This will allow the exit of a server without an initialize
  request.

Until the server has responded to the initialize request with an
InitializeBuildResult, the client must not send any additional requests
or notifications to the server.

Request:

* method: ‘build/initialize’

* params: InitializeBuildParams defined as follows

```scala
trait InitializeBuildParams {

  /** The rootUri of the workspace */
  def rootUri: DocumentUri

  /** The capabilities of the client */
  def capabilities: BuildClientCapabilities

}

trait BuildClientCapabilities {
}
```

Response:

* result: InitializeBuildResult defined as follows

```scala
trait InitializeBuildResult {
  /** The capabilities of the build server */
  capabilities: BuildServerCapabilities
}

trait BuildServerCapabilities {
  /** The server can compile targets via method buildTarget/compile */
  compileProvider: Boolean
  /** The server can provide a list of targets that contain a single text document via the method textDocument/buildTargets */
  textDocumentBuildTargetsProvider: Boolean
  /** The server provides sources for library dependencies via method buildTarget/dependencySources */
  dependencySourcesProvider: Boolean
  /** The server sends notifications to the client on build target change events via buildTarget/didChange */
  buildTargetChangedProvider: Boolean
}
```

#### 1.3.1.2. Initialized Build Notification

Like the language server protocol, the initialized notification is sent
from the client to the server after the client received the result of
the initialize request but before the client is sending any other
request or notification to the server. The server can use the
initialized notification for example to initialize intensive computation
such as dependency resolution or compilation. The initialized
notification may only be sent once.

Notification:

* method: ‘build/initialized’
* params: InitializedBuildParams defined as follows

```scala
trait InitializedBuildParams {

}
```

#### 1.3.1.3. Shutdown Build Request

Like the language server protocol, the shutdown build request is sent
from the client to the server. It asks the server to shut down, but to
not exit (otherwise the response might not be delivered correctly to the
client). There is a separate exit notification that asks the server to
exit.

Request:

* method: ‘shutdown’
* params: `null`

Response:

* result: `null`
* error: code and message set in case an exception happens during
  shutdown request.

#### 1.3.1.4. Exit Build Notification

Like the language server protocol, a notification to ask the server to
exit its process. The server should exit with success code 0 if the
shutdown request has been received before; otherwise with error code 1.

Notification:

* method: ‘exit’
* params: `null`

### 1.3.2. Workspace Build Targets Request

The workspace build targets request is sent from the client to the server to ask for the list of all available build targets in the workspace.

Request:

* method: 'workspace/buildTargets'
* params: `WorkspaceBuildTargetsRequest`, defined as follows

```scala
trait WorkspaceBuildTargetsRequest {
  /* The result should only contain targets for these languages. Language ID strings are defined by the LSP. */
  def languageIds: List[String]
}
```

Response:

* result: `WorkspaceBuildTargetsResult`, defined as follows

```scala
trait WorkspaceBuildTargetsResult {
  /* The build targets in this workspace that contain sources with the given language ids. */
  def targets: List[BuildTarget]
}
```

### 1.3.3. Build Target Changed Notification

The build target changed notification is sent from the server to the client to signal a change in a build target.
The server communicates during the initialize handshake whether this method is supported or not.

Notification:

* method: 'buildTarget/didChange'
* params: `DidChangeBuildTarget` defined as follows:

```scala
trait DidChangeBuildTarget {
  def changes: List[BuildTargetEvent]
}
trait BuildTargetEvent {
  /** The identifier for the changed build target */
  def uri: URI
  /** The kind of change for this build target */
  def kind: Option[BuildTargetEventKind]
}
object BuildTargetEventKind {
  val Created = 1
  val Changed = 2
  val Deleted = 3
}
```

### 1.3.4. Dependency Classpath Request

The dependency classpath request is sent from the client to the server to query for the classpath entries of a given list of build targets.

* method: `buildTarget/dependencyClasspath`
* params: `DependencyClasspathRequest`, defined as follows

```scala
trait DependencyClasspathRequest {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `DependencyClasspathResult`, defined as follows

```scala
trait DependencyClasspathResult {
  def items: List[DependencyClasspathItem]
}
trait DependencyClasspathItem {
  def target: BuildTargetIdentifier
  /** The -classpath passed to the Scala compiler. */
  def classpath: List[URI]
}
```

### 1.3.5. Build Target Text Documents Request

The build target text documents request is sent from the client to the server to query for the list of source files that are part of a given list of build targets.

* method: `buildTarget/textDocuments`
* params: `BuildTargetTextDocumentsRequest`

```scala
trait BuildTargetTextDocumentsRequest {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `BuildTargetTextDocumentsResponse`, defined as follows

```scala
trait BuildTargetTextDocumentsResponse {
  /** The source files used by this target */
  def textDocuments: List[TextDocumentIdentifier]
}
```

### 1.3.6. Text Document Build Targets Request

The text document build targets request is sent from the client to the server to query for the list of targets containing the given text document.
The server communicates during the initialize handshake whether this method is supported or not.

This request may be considered as the inverse of `buildTarget/textDocuments`.
This method can be used by a language server on `textDocument/didOpen` to lookup which compiler instance to use to compile that given text document.
In the case there are multiple targets (for example JVM/JS) containing the same source file, the language server can may present in the editor multiple options via `textDocument/codeLens` to configure how to dis-ambiguate.

* method: `textDocument/buildTargets`
* params: `TextDocumentBuildTargetsRequest`, defined as follows

```scala
trait TextDocumentBuildTargetsRequest {
  def textDocument: TextDocumentIdentifier
  /** If defined, only return a subset of these targets. If empty, all targets containing this document are returned. */
  def targets: Option[List[BuildTargetIdentifier]]
}
```

Response:

* result: `TextDocumentBuildTargetsResult`, defined as follows

```scala
trait TextDocumentBuildTargetsResult {
  def targets: List[BuildTargetIdentifier]
}
```

### 1.3.7. Dependency Sources Request

The build target dependency sources request is sent from the client to the server to query for the list of sources for the dependency classpath of a given list of build targets.
The server communicates during the initialize handshake whether this method is supported or not.
This method can be used by a language server on `textDocument/definition` to "Go to definition" from project sources to dependency sources.

* method: `buildTarget/dependencySources`
* params: `DependencySourcesRequest`

```scala
trait DependencySourcesRequest {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `DependencySourcesResult`, defined as follows

```scala
trait DependencySourcesResult {
  def items: List[DependencySourcesItem]
}
trait DependencySourcesItem {
  def target: BuildTargetIdentifier
  /**  List of resources containing source files of the target's dependencies. Can be jar files, zip files, or directories. */
  def sources: List[URI]
}
```

### 1.3.8. Compile Request

The compile build target request is sent from the client to the server to compile the given list of build targets.
The server communicates during the initialize handshake whether this method is supported or not.
This method can for example be used by a language server before `textDocument/rename` to ensure that all workspace sources typecheck correctly and are up-to-date.

* method: `buildTarget/compile`
* params: `CompileRequest`

```scala
trait CompileRequest {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `CompileResult`, defined as follows

```scala
trait CompileResult {
  def items: List[CompileResultItem]
}
trait CompileResultItem {
  /** The total number of reported errors compiling this target. */
  def errors: Int
  /** The total number of reported warnings compiling the target. */
  def warnings: Int
  /** The total number of milliseconds it took to compile the target. */
  def totalTime: Option[Long]
  /** The total number of lines of code in the given target. */
  def totalLines: Option[Long]
}
```

The server is free to send any number of `textDocument/publishDiagnostics` and `window/logMessage` notifications during compilation before completing the response.
The client is free to forward these messages to the LSP editor client.

## 1.4. Extensions

The build server protocol is designed to be extended with language specific data structures and methods.

### 1.4.1. Scala

The following section contains Scala-specific extensions to the build server protocol.

#### 1.4.1.1. Scala Build Target

`ScalaBuildTarget` is a basic data structure that contains scala-specific metadata for compiling a target containing Scala sources.
This metadata is embedded in the `data: Option[Json]` field of the `BuildTarget` definition.

```scala
trait ScalaBuildTarget {

  /** The Scala organization that is used for a target. */
  def scalaOrganization: String

  /** The scala version to compile this target */
  def scalaVersion: String

  /** The binary version of scalaVersion. For example, 2.12 if scalaVersion is 2.12.4. */
  def scalaBinaryVersion: String

  /** The output directory for classfiles produced by this target */
  def classDirectory: URI
}
```

#### 1.4.1.2. Scalac Options Request

The build target scalac options request is sent from the client to the server to query for the list of compiler options necessessary to compile this target.

* method: `buildTarget/scalacOptions`
* params: `ScalacOptionsRequest`

```scala
trait ScalacOptionsRequest {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `ScalacOptionsResult`, defined as follows

```scala
trait ScalacOptionsResult {
  def items: List[ScalcOptionItem]
}
trait ScalacOptionsItem {
  def target: BuildTargetIdentifier
  /** The flags passed to the compiler to compile this target. Should include compiler plugins. */
  def options: List[String]
}
```
