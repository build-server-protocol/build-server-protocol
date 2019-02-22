---
title: Build Server Protocol
---

# ![bsp logo](../resources/buildServerProtocol64.svg) Build Server Protocol

This document describes version 2.0 (WIP) of the build server protocol.

Edits to this specification can be made via a pull request against this markdown document.

## Motivation

The problem this document aims to address is the multiplied effort required to integrate between
available language servers/editors and build tools. Currently, every language server must implement
a custom integration for each supported build tool in order to extract compilation information such
as classpaths, source directories or compiler diagnostics. Likewise, new build tools are expected to
integrate with all available IDEs. The growing number of language servers and build tools in the
wider programming community means tooling developers spend a lot of time working on these
integrations.

The Build Server Protocol aims to define common functionality that both build tools (servers) and
language servers/editors (clients) understand. This common functionality enables tooling developers
to provide their end users the best developer experience while supporting build tools and language
servers with less effort and time.

- [Build Server Protocol](#build-server-protocol)
  - [Motivation](#motivation)
  - [Background](#background)
  - [Status](#status)
  - [Base protocol](#base-protocol)
  - [Basic Json Structures](#basic-json-structures)
    - [Build Target](#build-target)
    - [Build Target Identifier](#build-target-identifier)
    - [Task Id](#task-id)
    - [Status Code](#status-code)
    - [Uri](#uri)
  - [Actual Protocol](#actual-protocol)
    - [Server Lifetime](#server-lifetime)
      - [Initialize Build Request](#initialize-build-request)
      - [Initialized Build Notification](#initialized-build-notification)
      - [Shutdown Build Request](#shutdown-build-request)
      - [Exit Build Notification](#exit-build-notification)
      - [Show message](#show-message)
      - [Log message](#log-message)
      - [Publish Diagnostics](#publish-diagnostics)
    - [Workspace Build Targets Request](#workspace-build-targets-request)
    - [Build Target Changed Notification](#build-target-changed-notification)
    - [Build Target Sources Request](#build-target-sources-request)
    - [Inverse Sources Request](#inverse-sources-request)
    - [Dependency Sources Request](#dependency-sources-request)
    - [Resources Request](#resources-request)
    - [Task Notifications](#task-notifications)
      - [Task Started](#task-started)
      - [Task Progress](#task-progress)
      - [Task Finished](#task-finished)
      - [Task Data](#task-data)
    - [Compile Request](#compile-request)
      - [Compile Notifications](#compile-notifications)
    - [Test Request](#test-request)
      - [Test Notifications](#test-notifications)
      - [Test Notifications](#test-notifications-1)
    - [Run Request](#run-request)
    - [Clean Cache Request](#clean-cache-request)
  - [Extensions](#extensions)
    - [Scala](#scala)
      - [Scala Build Target](#scala-build-target)
      - [Scala Test Params](#scala-test-params)
      - [Scalac Options Request](#scalac-options-request)
      - [Scala Test Classes Request](#scala-test-classes-request)
      - [Scala Main Classes Request](#scala-main-classes-request)
    - [Sbt](#sbt)
  - [BSP Connection Protocol](#bsp-connection-protocol)
    - [The BSP Connection Details](#the-bsp-connection-details)
      - [Default Locations for BSP Connection Files](#default-locations-for-bsp-connection-files)
      - [Policy around Connection Files Generation](#policy-around-connection-files-generation)
      - [Build Tool Commands to Start BSP Servers](#build-tool-commands-to-start-bsp-servers)
        - [Example with `my-build-tool`](#example-with-my-build-tool)
    - [Clients Connecting to BSP Servers](#clients-connecting-to-bsp-servers)
  - [Appendix](#appendix)
    - [Scala Bindings](#scala-bindings)
    - [FAQ](#faq)

## Background

The Build Server Protocol takes inspiration from the Language Server Protocol (LSP).
Unlike in the Language Server Protocol, the language server or editor is referred to as the “client” and a build tool such as sbt/pants/gradle/bazel is referred to as the “server”.

The best way to read this document is by considering it as a wishlist from the perspective of an IDE
developer.

The code listings in this document are written using Scala syntax.
Every data strucuture in this document has a direct translation to JSON and Protobuf.
See [Appendix](#appendix) for schema definitions that can be used to automatically generate
bindings for different target languages.


## Status

The Build Server Protocol is not an approved standard. Everything in this
document is subject to change and open for discussions, including core data
structures.

The creation of BSP clients and servers is under active development.

In the clients space, IntelliJ has been the first language server to implement BSP. The integration
is available in the nightly releases of the Scala plugin. Other language servers, like [Dotty
IDE](https://github.com/lampepfl/dotty) and [scalameta/metals](https://github.com/scalameta/metals),
are currently working or planning to work on a BSP integrations.

On the server side, [bloop](https://github.com/scalacenter/bloop) is the
first server to implement BSP. There are ongoing efforts to implement BSP
in popular build tools like [sbt](https://github.com/sbt/sbt/issues/3890).

We're looking for third parties that implement BSP natively in other build tools like Gradle, Bazel
or Pants.

The Build Server Protocol has been designed to be language-agnostic. We're looking for ways to
collaborate with other programming language communities and build tool authors.

The best way to share your thoughts on the Build Server Protocol or to get involved in its
development is to open an issue or pull request to this repository. Any help on developing
integrations will be much appreciated.

## Base protocol

The base protocol is identical to the language server base protocol. See
<https://microsoft.github.io/language-server-protocol/specification> for
more details.

Like the language server protocol, the build server protocol defines a
set of JSON-RPC request, response and notification messages which are
exchanged using the base protocol.

## Basic Json Structures

In addition to basic data structures in the [General section of the Language Server
Protocol](https://microsoft.github.io/language-server-protocol/specification#general), the Build
Server Protocol defines the following additional data structures.

### Build Target

Build target contains metadata about an artifact (for example library, test, or binary artifact).
Using vocabulary of other build tools:

* sbt: a build target is a combined project + config. Example:
  * a regular JVM project with main and test configurations will have
    2 build targets, one for main and one for test.
  * a single configuration in a single project that contains both Java and Scala sources maps to one BuildTarget.
  * a project with crossScalaVersions 2.11 and 2.12 containing main and test configuration in each will have 4 build targets.
  * a Scala 2.11 and 2.12 cross-built project for Scala.js and the JVM with main and test configurations will have 8 build targets.
* pants: a pants target corresponds one-to-one with a BuildTarget
* bazel: a bazel target corresponds one-to-one with a BuildTarget

The general idea is that the BuildTarget data structure should contain only information that is is fast or cheap to compute.

```scala
trait BuildTarget {

  /** The target’s unique identifier */
  def id: BuildTargetIdentifier

  /** A human readable name for this target.
    * May be presented in the user interface.
    * Should be unique if possible.
    * The id.uri is used if None. */
  def displayName: Option[String]
  
  /** The directory where this target belongs to. Multiple build targets are allowed to map
    * to the same base directory, and a build target is not required to have a base directory. 
    * A base directory does not determine the sources of a target, see buildTarget/sources. */
  def baseDirectory: Option[Uri]

  /** Free-form string tags to categorize or label this build target.
    * For example, can be used by the client to:
    * - customize how the target should be translated into the client's project model.
    * - group together different but related targets in the user interface.
    * - display icons or colors in the user interface.
    * Pre-defined tags are listed in `BuildTargetTag` but clients and servers
    * are free to define new tags for custom purposes.
    */
  def tags: List[String]
  
  /** The capabilities of this build target. */
  def capabilities: BuildTargetCapabilities

  /** The set of languages that this target contains.
    * The ID string for each language is defined in the LSP. */
  def languageIds: List[String]

  /** The direct upstream build target dependencies of this build target */
  def dependencies: List[BuildTargetIdentifer]
  
  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  def dataKind: Option[String]

  /** Language-specific metadata about this target.
    * See ScalaBuildTarget as an example. */
  def data: Option[Json] // Note, matches `any` in the LSP.
}

object BuildTargetDataKind {

  /** The `data` field contains a `ScalaBuildTarget` object. */
  val Scala = "scala"
  
  /** The `data` field contains a `SbtBuildTarget` object. */
  val Sbt = "sbt"
    
}

object BuildTargetTag {

  /** Target contains re-usable functionality for downstream targets. May have any
    * combination of capabilities. */
  val Library = "library"

  /** Target contains source code for producing any kind of application, may have
    * but does not require the `canRun` capability. */
  val Application = "application"

  /** Target contains source code for testing purposes, may have but does not
    * require the `canTest` capability. */
  val Test = "test"

  /** Target contains source code for integration testing purposes, may have
    * but does not require the `canTest` capability.
    * The difference between "test" and "integration-test" is that
    * integration tests traditionally run slower compared to normal tests
    * and require more computing resources to execute.
    */
  val IntegrationTest = "integration-test"

  /** Target contains source code to measure performance of a program, may have
    * but does not require the `canRun` build target capability.
    */
  val Benchmark = "benchmark"

  /** Target should be ignored by IDEs. */
  val NoIDE = "no-ide"
}

trait BuildTargetCapabilities {
  /** This target can be compiled by the BSP server. */
  def canCompile: Boolean
  /** This target can be tested by the BSP server. */
  def canTest: Boolean
  /** This target can be run by the BSP server. */
  def canRun: Boolean
}
```

### Build Target Identifier

A unique identifier for a target, can use any URI-compatible encoding as long as it is unique within the workspace.
Clients should not infer metadata out of the URI structure such as the path or query parameters, 
use `BuildTarget` instead.

```scala
trait BuildTargetIdentifer {
  /** The target’s Uri */
  def uri: Uri
}
```

### Task Id 

The Task Id allows clients to *uniquely* identify a BSP task and establish a client-parent
relationship with another task id.

```scala
trait TaskId {
  /** A unique identifier */
  def id: String

  /** The parent task ids, if any. A non-empty parents field means
    * this task is a sub-task of every parent task id. The child-parent
    * relationship of tasks makes it possible to render tasks in
    * a tree-like user interface or inspect what caused a certain task
    * execution. */
  def parents: Option[List[String]]
}
```

### Status Code

Included in notifications of tasks or requests to signal the completion state.

```scala
object StatusCode {
  /** Execution was successful. */
  val Ok = 1
  /** Execution failed. */
  val Error = 2
  /** Execution was cancelled. */
  val Cancelled = 3
}
```

### Uri

```scala
/**  A resource identifier that is a valid URI according
  * to rfc3986: * https://tools.ietf.org/html/rfc3986 */
type Uri = String
```

## Actual Protocol

Unlike the language server protocol, the build server protocol does not
support dynamic registration of capabilities.
The motivation for this change is simplicity.
If a motivating example for dynamic registration comes up this decision can be reconsidered.
The server and client capabilities must be communicated through the initialize request.

### Server Lifetime

Like the language server protocol, the current protocol specification
defines that the lifetime of a build server is managed by the client
(e.g. a language server like Dotty IDE). It is up to the client to
decide when to start (process-wise) and when to shutdown a server.

#### Initialize Build Request

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
  /** Name of the client */
  def displayName: String

  /** The version of the client */
  def version: String

  /** The BSP version that the client speaks */
  def bspVersion: String

  /** The rootUri of the workspace */
  def rootUri: Uri

  /** The capabilities of the client */
  def capabilities: BuildClientCapabilities

  /** Additional metadata about the client */
  def data: Option[Json]
}

trait BuildClientCapabilities {
  /** The languages that this client supports.
    * The ID strings for each language is defined in the LSP.
    * The server must never respond with build targets for other
    * languages than those that appear in this list. */
  def languageIds: List[String]

}
```

Response:

* result: InitializeBuildResult defined as follows

```scala
trait InitializeBuildResult {
  /** Name of the server */
  def displayName: String

  /** The version of the server */
  def version: String

  /** The BSP version that the server speaks */
  def bspVersion: String

  /** The capabilities of the build server */
  def capabilities: BuildServerCapabilities

  /** Additional metadata about the server */
  def data: Option[Json]
}


trait BuildServerCapabilities {
  /** The languages the server supports compilation via method buildTarget/compile. */
  def compileProvider: Option[CompileProvider]
  
  /** The languages the server supports test execution via method buildTarget/test */
  def testProvider: Option[TestProvider]
  
  /** The languages the server supports run via method buildTarget/run */
  def runProvider: Option[RunProvider]
  
  /** The server can provide a list of targets that contain a
    * single text document via the method buildTarget/inverseSources */
  def inverseSourcesProvider: Option[Boolean]
  
  /** The server provides sources for library dependencies
    * via method buildTarget/dependencySources */
  def dependencySourcesProvider: Option[Boolean]
  
  /** The server provides all the resource dependencies
    * via method buildTarget/resources */
  def resourcesProvider: Option[Boolean]
  
  /** The server sends notifications to the client on build
    * target change events via buildTarget/didChange */
  def buildTargetChangedProvider: Option[Boolean]
}

trait CompileProvider {
  def languageIds: List[String]
}

trait RunProvider {
  def languageIds: List[String]
}

trait TestProvider {
  def languageIds: List[String]
}
```

Clients can use these capabilities to notify users what BSP endpoints can
and cannot be used and why.

#### Initialized Build Notification

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

#### Shutdown Build Request

Like the language server protocol, the shutdown build request is sent
from the client to the server. It asks the server to shut down, but to
not exit (otherwise the response might not be delivered correctly to the
client). There is a separate exit notification that asks the server to
exit.

Request:

* method: `build/shutdown`
* params: `null`

Response:

* result: `null`
* error: code and message set in case an exception happens during
  shutdown request.

#### Exit Build Notification

Like the language server protocol, a notification to ask the server to
exit its process. The server should exit with success code 0 if the
shutdown request has been received before; otherwise with error code 1.

Notification:

* method: `build/exit`
* params: `null`

#### Show message

The show message notification is sent from a server to a client to ask the
client to display a particular message in the user interface.

Notification:

* method: `build/showMessage`
* params: `ShowMessageParams` defined as follows:

```scala
trait ShowMessageParams {
  /** The message type. See {@link MessageType}. */
  def type: Int

  /** The task id if any. */
  def task: Option[TaskId]

  /** The request id that originated this notification. */
  def originId: Option[String]

  /** The actual message. */
  def message: String
}
```

where `MessageType` is defined as follows:

```scala
object MessageType {
  /** An error message. */
  final val Error = 1
  /** A warning message. */
  final val Warning = 2
  /** An information message. */
  final val Info = 3
  /** A log message. */
  final val Log = 4
}
```

A `build/showMessage` notification is similar to LSP's `window/showMessage`, except for a few
additions like `id` and `originId`.

The `originId` field helps clients know which request originated a notification in case several
requests are handled by the client at the same time. It will only be populated if the client
defined it in the request that triggered this notification.

#### Log message

The log message notification is sent from the server to the client to ask the
client to log a particular message.

Notification:

* method: ‘build/logMessage’
* params: LogMessageParams defined as follows:

```scala
trait LogMessageParams {
  /** The message type. See {@link MessageType} */
  def type: Int
  
  /** The task id if any. */
  def task: Option[TaskId]
  
  /** The request id that originated this notification. */
  def originId: Option[String]

  /** The actual message */
  def message: String
}
```

Where type is defined as `build/showMessage`.

A `build/logMessage` notification is similar to LSP's `window/logMessage`, except for a few
additions like `id` and `originId`.

The `originId` field helps clients know which request originated a notification in case several
requests are handled by the client at the same time. It will only be populated if the client
defined it in the request that triggered this notification.

#### Publish Diagnostics

The Diagnostics notification are sent from the server to the client to signal results of validation
runs.

Notification:

* method: `build/publishDiagnostics`
* params: `PublishDiagnosticsParams` defined as follows:

```scala
trait PublishDiagnosticsParams {
  /** The document where the diagnostics are published. */
  def textDocument: TextDocumentIdentifier

  /** The build target where the diagnostics origin.
    * It is valid for one text document to belong to multiple
    * build targets, for example sources that are compiled against multiple 
    * platforms (JVM, JavaScript). */
  def buildTarget: BuildTargetIdentifier
  
  /** The request id that originated this notification. */
  def originId: Option[String]
  
  /** The diagnostics to be published by the client. */
  def diagnostics: List[Diagnostic]

  /** Whether the client should clear the previous diagnostics
    * mapped to the same `textDocument` and `buildTarget`. */
  def reset: Boolean
}
```

where `Diagnostic` is defined as it is in LSP.

When `reset` is true, the client must clean all previous diagnostics
associated with the same `textDocument` and `buildTarget` and set instead the
diagnostics in the request. This is the same behaviour as
`PublishDiagnosticsParams` in the LSP. When `reset` is false, the diagnostics
are added to the last active diagnostics, allowing build tools to stream
diagnostics to the client.

It is the server's responsibility to manage the lifetime of the diagnostics by
using the appropriate value in the `reset` field. Clients generate new
diagnostics by calling any BSP endpoint that triggers a `buildTarget/compile`,
such as `buildTarget/compile`, `buildTarget/test` and `buildTarget/run`.

The optional `originId` field in the definition of `PublishDiagnosticsParams`
can be used by clients to know which request originated the notification. This
field will be defined if the client defined it in the original request that
triggered this notification.

### Workspace Build Targets Request

The workspace build targets request is sent from the client to the server to ask for the list of all available build targets in the workspace.

Request:

* method: 'workspace/buildTargets'
* params: `WorkspaceBuildTargetsParams`, defined as follows

```scala
trait WorkspaceBuildTargetsParams {
}
```

Response:

* result: `WorkspaceBuildTargetsResult`, defined as follows

```scala
trait WorkspaceBuildTargetsResult {
  /** The build targets in this workspace that
    * contain sources with the given language ids. */
  def targets: List[BuildTarget]
}
```

### Build Target Changed Notification

The build target changed notification is sent from the server to the client to signal a change in a build target.
The server communicates during the initialize handshake whether this method is supported or not.

Notification:

* method: 'buildTarget/didChange'
* params: `DidChangeBuildTargetParams` defined as follows:

```scala
trait DidChangeBuildTarget {
  def changes: List[BuildTargetEvent]
}

trait BuildTargetEvent {
  /** The identifier for the changed build target */
  def target: BuildTargetIdentifier
  
  /** The kind of change for this build target */
  def kind: Option[Int]

  /** Any additional metadata about what information changed. */
  def data: Option[Json]
}
```

where the `kind` is defined as follows:

```scala
object BuildTargetEventKind {
  /** The build target is new. */
  val Created = 1
  
  /** The build target has changed. */
  val Changed = 2
  
  /** The build target has been deleted. */
  val Deleted = 3
}
```

The `BuildTargetEventKind` information can be used by clients to trigger reindexing or update the
user interface with the new information.

### Build Target Sources Request

The build target sources request is sent from the client to the server to query for the list of text documents and directories that are belong to a build target.
The sources response must not include sources that are external to the workspace, see `buildTarget/dependencySources`.

* method: `buildTarget/sources`
* params: `BuildTargetSourcesParams`

```scala
trait SourcesParams {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `BuildTargetSourcesResult`, defined as follows

```scala
trait SourcesResult {
  def items: List[SourcesItem]
}

trait SourcesItem {
  def target: BuildTargetIdentifer
  /** The text documents or and directories that belong to this build target. */
  def sources: List[SourceItem]
}

trait SourceItem {
  /** Either a text document or a directory. A directory entry must end with a forward
    * slash "/" and a directory entry implies that every nested text document within the 
    * directory belongs to this source item.
    */
  def uri: Uri
  
  /** Type of file of the source item, such as whether it is file or directory. 
   */
  def kind: SourceItemKind  
  
  /** Indicates if this source is automatically generated by the build and is not
    * intended to be manually edited by the user. */
  def generated: Boolean
}

object SourceItemKind {
  /** The source item references a normal file.  */
  val File: Int = 1
  /** The source item references a directory. */
  val Directory: Int = 2
}

```


### Inverse Sources Request

The inverse sources request is sent from the client to the server to query for the list of build targets containing a text document.
The server communicates during the initialize handshake whether this method is supported or not.
This request can be viewed as the inverse of `buildTarget/sources`, except it only works for text documents and not directories.

* method: `textDocument/inverseSources`
* params: `InverseSourcesParams`, defined as follows

```scala
trait InverseSourcesParams {
  def textDocument: TextDocumentIdentifier
}
```

Response:

* result: `InverseSourcesResult`, defined as follows

```scala
trait InverseSourcesResult {
  def targets: List[BuildTargetIdentifier]
}
```

### Dependency Sources Request

The build target dependency sources request is sent from the client to the server to query for the sources of build target dependencies that are external to the workspace.
The dependency sources response must not include source files that belong to a build target within the workspace, see `buildTarget/sources`.

The server communicates during the initialize handshake whether this method is supported or not.
This method can for example be used by a language server on `textDocument/definition` to "Go to definition" from project sources to dependency sources.

* method: `buildTarget/dependencySources`
* params: `DependencySourcesParams`

```scala
trait DependencySourcesParams {
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
  /** List of resources containing source files of the
    * target's dependencies.
    * Can be source files, jar files, zip files, or directories. */
  def sources: List[Uri]
}
```

### Resources Request

The build target resources request is sent from the client to the server to query for the list of
resources of a given list of build targets.

A resource is a data dependency required to be present in the runtime classpath when a build target
is run or executed. The server communicates during the initialize handshake whether this method is
supported or not.

This request can be used by a client to highlight the resources in a project view, for example.

* method: `buildTarget/resources`
* params: `ResourcesParams`

```scala
trait ResourcesParams {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `ResourcesResult`, defined as follows

```scala
trait ResourcesResult {
  def items: List[ResourcesItem]
}
trait ResourcesItem {
  def target: BuildTargetIdentifier
  /** List of resource files. */
  def resources: List[Uri]
}
```

### Task Notifications

The BSP server can inform the client on the execution state of any task in the build tool. 
The execution of some tasks, such as compilation or tests, must always be reported by the server.

The server may also send additional task notifications for actions not covered by the protocol, 
such as resolution or packaging. BSP clients can then display this information to their users at their discretion.

When beginning a task, the server may send `build/taskStart`, intermediate updates may be sent in
`build/taskProgress`.

If a `build/taskStart` notification has been sent, the server must send `build/taskFinish`
on completion of the same task. Conversely, a `build/taskFinish` notification must always be sent after a 
`build/taskStart` with the same `taskId` was sent.

`build/taskStart`, `build/taskProgress` and `build/taskFinish` notifications for the same task must use the same `taskId`.

Tasks that are spawned by another task should reference the originating task's `taskId` in their own `taskId`'s
`parent` field. Tasks spawned directly by a request should reference the request's `originId` parent.

#### Task Started

Notification:

* method: `build/taskStart`
* params: `TaskStartParams` defined as follows:

```scala
trait TaskStartParams {
    /** Unique id of the task with optional reference to parent task id */
    def taskId: TaskId

    /** Timestamp of when the event started in milliseconds since Epoch. */
    def eventTime: Option[Long]

    /** Message describing the task. */
    def message: Option[String]
    
    /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified.
      * Kind names for specific tasks like compile, test, etc are specified in the protocol.
      */
    def dataKind: Option[String]

    /** Optional metadata about the task.
      * Objects for specific tasks like compile, test, etc are specified in the protocol. 
      */
    def data: Option[Json]
}
```

#### Task Progress

After a `taskStart` and before `taskFinish` for a `taskId`, the server may send any number of progress notifications.

* method: `build/taskProgress`
* params: `TaskProgressParams` defined as follows:

```scala
trait TaskProgressParams {
    /** Unique id of the task with optional reference to parent task id */
    def taskId: TaskId

    /** Timestamp of when the progress event was generated in milliseconds since Epoch. */
    def eventTime: Option[Long]

    /** Message describing the task progress. 
    * Information about the state of the task at the time the event is sent. */
    def message: Option[String]

    /** If known, total amount of work units in this task. */
    def total: Option[Long]

    /** If known, completed amount of work units in this task. */
    def progress: Option[Long]

    /** Name of a work unit. For example, "files" or "tests". May be empty. */
    def unit: Option[String]
    
    /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified.
      * Kind names for specific tasks like compile, test, etc are specified in the protocol.
      */
    def dataKind: Option[String]

    /** Optional metadata about the task.
      * Objects for specific tasks like compile, test, etc are specified in the protocol. 
      */
    def data: Option[Json]
}

```

#### Task Finished

* method: `build/taskFinish`
* params: `TaskFinishParams` defined as follows:

```scala
trait TaskFinishParams {
    /** Unique id of the task with optional reference to parent task id */
    def taskId: TaskId

    /** Timestamp of the event in milliseconds. */
    def eventTime: Option[Long]

    /** Message describing the finish event. */
    def message: Option[String]

    /** Task completion status. */
    def status: StatusCode

    /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified.
      * Kind names for specific tasks like compile, test, etc are specified in the protocol.
      */
    def dataKind: Option[String]

    /** Optional metadata about the task.
      * Objects for specific tasks like compile, test, etc are specified in the protocol. 
      */
    def data: Option[Json]
}
```

#### Task Data

Task progress notifications may contain an arbitrary object in their `data` field. The kind of object that is contained
in a notification must be specified in the `dataKind` field.

There are predefined kinds of objects for test and compile tasks, as described in the [Compile Request](#compile-request)
and [Test Request](#test-request) sections. These are declared by predefined `dataKind` strings in task notifications: 

```scala
object TaskDataKind {

  /** `data` field must contain a CompileTask object. */
  val CompileTask = "compile-task"
  
  /** `data` field must contain a CompileReport object. */
  val CompileReport = "compile-report"
  
  /** `data` field must contain a TestTask object. */
  val TestTask = "test-task"
  
  /** `data` field must contain a TestReport object. */
  val TestReport = "test-report"

  /** `data` field must contain a TestStart object. */  
  val TestStart = "test-start"
  
  /** `data` field must contain a TestFinish object. */
  val TestFinish = "test-finish"
}
```


### Compile Request

The compile build target request is sent from the client to the server to compile the given list of build targets.
The server communicates during the initialize handshake whether this method is supported or not.
This method can for example be used by a language server before `textDocument/rename` to ensure that all workspace sources typecheck correctly and are up-to-date.

* method: `buildTarget/compile`
* params: `CompileParams`

```scala
trait CompileParams {
  /** A sequence of build targets to compile. */
  def targets: List[BuildTargetIdentifier]
  
  /** A unique identifier generated by the client to identify this request.
    * The server may include this id in triggered notifications or responses. */
  def originId: Option[String]

  /** Optional arguments to the compilation process. */
  def arguments: Option[List[String]]
}
```

Response:

* result: `CompileResult`, defined as follows
* error: JSON-RPC code and message set in case an exception happens during the request.

```scala
trait CompileResult {
  /** An optional request id to know the origin of this report. */
  def originId: Option[String]
  
  /** A status code for the execution. */
  def statusCode: Int
  
  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  def dataKind: String

  /** A field containing language-specific information, like products
    * of compilation or compiler-specific metadata the client needs to know. */
  def data: Option[Json] // Note, matches `any | null` in the LSP.
}
```

#### Compile Notifications

The beginning of a compilation unit may be signalled to the client with a `build/taskStart` notification.
When the compilation unit is a build target, the notification's `dataKind` field must be "compile-task" and the 
`data` field must include a `CompileTask` object:

```scala
trait CompileTask {
  def target: BuildTargetIdentifier
}
```

The completion of a compilation task should be signalled with a `build/taskFinish` notification.
When the compilation unit is a build target, the notification's `dataKind` field must be `compile-report` and the 
`data` field must include a `CompileReport` object:

```scala
trait CompileReport {
  /** The build target that was compiled. */
  def target: BuildTargetIdentifier
  
  /** An optional request id to know the origin of this report. */
  def originId: Option[String]

  /** The total number of reported errors compiling this target. */
  def errors: Int

  /** The total number of reported warnings compiling the target. */
  def warnings: Int

  /** The total number of milliseconds it took to compile the target. */
  def time: Option[Int]
}
```

The server is free to send any number of `build/publishDiagnostics` and `build/logMessage`
notifications during compilation before completing the response. Any number of tasks triggered by the requests 
may be communicated with `build/task*` notifications.

### Test Request

The test build target request is sent from the client to the server to test the given list of build targets.
The server communicates during the initialize handshake whether this method is supported or not.

* method: `buildTarget/test`
* params: `TestParams`

```scala
trait TestParams {
  /** A sequence of build targets to test. */
  def targets: List[BuildTargetIdentifier]
  
  /** A unique identifier generated by the client to identify this request.
    * The server may include this id in triggered notifications or responses. */
  def originId: Option[String]
  
  /** Optional arguments to the test execution engine. */
  def arguments: Option[List[String]]
  
  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  def dataKind: Option[String]

  /** Language-specific metadata about for this test execution.
    * See ScalaTestParams as an example. */
  def data: Option[Json]
}
```

Response:

* result: `TestResult`, defined as follows
* error: JSON-RPC code and message set in case an exception happens during the request.

```scala
trait TestResult {
  /** An optional request id to know the origin of this report. */
  def originId: Option[String]
  
  /** A status code for the execution. */
  def statusCode: Int
  
  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  def dataKind: Option[String]

  def data: Option[Json] // Note, matches `any | null` in the LSP.
}
```

The field `data` may contain test-related language-specific information.

#### Test Notifications

The beginning of a testing unit may be signalled to the client with a `build/taskStart` notification.
When the testing unit is a build target, the notification's `dataKind` field must be `test-task` and the 
`data` field must include a `TestTask` object.

```scala
trait TestTask {
  def target: BuildTargetIdentifier
}
```

The completion of a test task should be signalled with a `build/taskFinish` notification.
When the testing unit is a build target, the notification's `dataKind` field must be `test-report` and the 
`data` field must include a `TestTask` object:

```scala
trait TestReport {
  /** The build target that was compiled. */
  def target: BuildTargetIdentifier
  
  /** The total number of successful tests. */
  def passed: Int

  /** The total number of failed tests. */
  def failed: Int
  
  /** The total number of ignored tests. */
  def ignored: Int

  /** The total number of cancelled tests. */
  def cancelled: Int

  /** The total number of skipped tests. */
  def skipped: Int

  /** The total number of milliseconds tests take to run (e.g. doesn't include compile times). */
  def time: Option[Int]
}
```

This request may trigger a compilation or other tasks on the selected build targets.

The server may send any number of `build/task*`, `build/publishDiagnostics` and `build/logMessage` notifications
to communicate about tasks triggered by the request to the client.

#### Test Notifications

The server may inform about individual tests or groups of tests in task notifications that reference the originating task in their `taskId`.
For example, the server can send a `taskStart`/`taskFinish` for each test suite in a target, and likewise for each individual test in the suite.
The server's implementation decides the granularity at which tests are reported. For example, if it only has information 
about all the tests in a suite at a time, it could report a TestFinish for each test once the suite is done.

Where applicable, notifications about tests should use the `taskId` to reference parent tasks so that the client's user 
interface can display test execution in a tree view.

Individual test start notifications should specify `test-started` in the `dataKind` field and include the `TestStart` object and
test finish notifications should specify `test-finished` in the `dataKind` field and include the `TestFinish` object in the `data` field.

```scala
trait TestStart {
  /** Name or description of the test. */
  def displayName: String
  
  /** Source location of the test, as LSP location. */
  def location: Option[Location]
}

trait TestFinish {
  /** Name or description of the test. */
  def displayName: String

  /** Information about completion of the test, for example an error message. */
  def message: Option[String]

  /** Completion status of the test. */
  def status: Int
  
  /** Source location of the test, as LSP location. */
  def location: Option[Location]
  
  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  def dataKind: Option[String]

  /** Optionally, structured metadata about the test completion.
    * For example: stack traces, expected/actual values. */
  def data: Option[Json]
}


trait TestStatus {
    /** The test passed successfully. */
    val Passed: Int = 1

    /** The test failed. */
    val Failed: Int = 2

    /** The test was marked as ignored. */
    val Ignored: Int = 3

    /** The test execution was cancelled. */
    val Cancelled: Int = 4

    /** The was not included in execution. */
    val Skipped: Int = 5
}
```

### Run Request

The run request is sent from the client to the server to run a build target. The server communicates
during the initialize handshake whether this method is supported or not.

* method: `buildTarget/run`
* params: `RunParams`

```scala
trait RunParams {
  /** The build target to run. */
  def target: BuildTargetIdentifier
  
  /** A unique identifier generated by the client to identify this request.
    * The server may include this id in triggered notifications or responses. */
  def originId: Option[String]
  
  /** Optional arguments to the executed application. */
  def arguments: Option[List[String]]
}
```

Note that an empty run request is valid. Run will be executed in the target as specified in the
build tool.

Response:

* result: `RunResult`, defined as follows
* error: JSON-RPC code and message set in case an exception happens during the request.

```scala
trait RunResult {
  /** An optional request id to know the origin of this report. */
  def originId: Option[String] 
  
  /** A status code for the execution. */
  def statusCode: Int
}
```

This request may trigger a compilation on the selected build targets. The server is free to send any
number of `build/task*`, `build/publishDiagnostics` and `build/logMessage` notifications
during compilation before completing the response.

The client will get a `originId` field in `RunResult` if the `originId` field in the
`RunParams` is defined.

### Clean Cache Request

The clean cache request is sent from the client to the server to reset any state associated with a given build target.
The state can live either in the build tool or in the file system.

The build tool defines the exact semantics of the clean cache request:

1. Stateless build tools are free to ignore the request and respond with a successful response.
2. Stateful build tools must ensure that invoking compilation on a target that has been cleaned results in a full compilation.

* method: `buildTarget/cleanCache`
* params: `CleanCacheParams`

```scala
trait CleanCacheParams {
  /** The build targets to clean. */
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `CleanCacheResult`, defined as follows
* error: JSON-RPC code and message set in case an exception happens during the request.

```scala
trait CleanCacheResult {
  /** Optional message to display to the user. */
  def message: Option[String]
  /** Indicates whether the clean cache request was performed or not. */
  def cleaned: Boolean
}
```

## Extensions

The build server protocol is designed to be extended with language specific data structures and methods.

### Scala

The following section contains Scala-specific extensions to the build server protocol.

#### Scala Build Target

`ScalaBuildTarget` is a basic data structure that contains scala-specific metadata for compiling a target containing Scala sources.
This metadata is embedded in the `data: Option[Json]` field of the `BuildTarget` definition, when the `dataKind` field contains "scala".

```scala
trait ScalaBuildTarget {
  /** The Scala organization that is used for a target. */
  def scalaOrganization: String

  /** The scala version to compile this target */
  def scalaVersion: String

  /** The binary version of scalaVersion.
    * For example, 2.12 if scalaVersion is 2.12.4. */
  def scalaBinaryVersion: String

  /** The target platform for this target */
  def platform: Int

  /** A sequence of Scala jars such as scala-library, scala-compiler and scala-reflect. */
  def jars: List[String]
}

object ScalaPlatform {
  val JVM = 1
  val JS = 2
  val Native = 3
}
```

#### Scala Test Params

`ScalaTestParams` contains scala-specific metadata for testing Scala targets.
This metadata is embedded in the `data: Option[Json]` field of the `buildTarget/test` request when the `dataKind` field
contains "scala-test".

```scala
trait ScalaTestParams {
  /** The test classes to be run in this test execution.
    * It is the result of `buildTarget/scalaTestClasses`. */
  def testClasses: Option[List[ScalaTestClassesItem]]
}
```

#### Scalac Options Request

The build target scalac options request is sent from the client to the server to query for the list of compiler options necessary to compile in a given list of targets.

* method: `buildTarget/scalacOptions`
* params: `ScalacOptionsParams`

```scala
trait ScalacOptionsParams {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `ScalacOptionsResult`, defined as follows

```scala
trait ScalacOptionsResult {
  def items: List[ScalacOptionsItem]
}

trait ScalacOptionsItem {
    def target: BuildTargetIdentifier

    /** Additional arguments to the compiler.
      * For example, -deprecation. */
    def options: List[String]
    
    /** The dependency classpath for this target, must be
      * identical to what is passed as arguments to
      * the -classpath flag in the command line interface
      * of scalac. */
    def classpath: List[Uri]
    
    /** The output directory for classfiles produced by this target */
    def classDirectory: Uri
}
```

#### Scala Test Classes Request

The build target scala test options request is sent from the client to the server to query for the
list of fully qualified names of test clases in a given list of targets.

This method can for example be used by a client to:
  
* Show a list of the discovered classes that can be tested.
* Attach a "Run test suite" button above the definition of a test suite via `textDocument/codeLens`.

(To render the code lens, the language server needs to map the fully qualified names of the test
targets to the defining source file via `textDocument/definition`. Then, once users click on the
button, the language server can pass the fully qualified name of the test class as an argument to
the `buildTarget/test` request.)

* method: `buildTarget/scalaTestClasses`
* params: `ScalaTestClassesParams`

```scala
trait ScalaTestClassesParams {
  def targets: List[BuildTargetIdentifier]
  
  /** An optional number uniquely identifying a client request. */
  def originId: Option[String]
}
```

Response:

* result: `ScalaTestClassesResult`, defined as follows
* error: code and message set in case an exception happens during
  shutdown request.

```scala
trait ScalaTestClassesResult {
  def items: List[ScalaTestClassesItem]
  
  /** An optional id of the request that triggered this result. */
  def originId: Option[String]

}

trait ScalaTestClassesItem {
  /** The build target that contains the test classes. */
  def target: BuildTargetIdentifier

  /** The fully qualified names of the test classes in this target */
  def classes: List[String]
}
```

This request may trigger a compilation on the selected build targets. The server is free to send any
number of `build/task*`, `build/publishDiagnostics` and `build/logMessage` notifications
during compilation before completing the response.

The client will get a `originId` field in `ScalaTestClassesResult` if the `originId` field in the
`ScalaTestClassesParams` is defined.

#### Scala Main Classes Request

The build target main classes request is sent from the client to the server to query for the list of
main classes that can be fed as arguments to `buildTarget/run`. This method can be used for the same
use cases than the [Scala Test Classes Request](#scala-test-classes-request) enables.

* method: `buildTarget/scalaMainClasses`
* params: `ScalaMainClassesParams`

```scala
trait ScalaMainClassesParams {
  def targets: List[BuildTargetIdentifier]
  
  /** An optional number uniquely identifying a client request. */
  def originId: Option[String]
}
```

Response:

* result: `ScalaMainClassesResult`, defined as follows
* error: code and message set in case an exception happens during shutdown request.

```scala
trait ScalaMainClassesResult {
  def items: List[ScalaMainClassesItem]
  
  /** An optional id of the request that triggered this result. */
  def originId: Option[String]

}

trait ScalaMainClassesItem {
  /** The build target that contains the test classes. */
  def target: BuildTargetIdentifier

  /** The main class item. */
  def classes: List[ScalaMainClass]
}

trait ScalaMainClass {
  /** The main class to run. */
  def class: String

  /** The user arguments to the main entrypoint. */
  def arguments: List[String]
  
  /** The jvm options for the application. */
  def jvmOptions: List[String]
}

```

This request may trigger a compilation on the selected build targets. The server is free to send any
number of `build/taskStart`, `build/taskProgress`, `build/taskFinish`, `build/publishDiagnostics` and
`build/logMessage` notifications during compilation before completing the response.

The client will get a `originId` field in `ScalaMainClassesResult` if the `originId` field in the
`ScalaMainClassesParams` is defined.

### Sbt

The following section contains sbt-specific extensions to the build server protocol. This extension
allows BSP clients to provide language support for sbt build files.

`SbtBuildTarget` is a basic data structure that contains sbt-specific metadata for providing editor
support for sbt build files. This metadata is embedded in the `data: Option[Json]` field of the
`BuildTarget` definition when the `dataKind` field contains "sbt".

```scala
trait SbtBuildTarget {
  /** The sbt version. Useful to support version-dependent syntax. */
  def sbtVersion: String
  
  /** A sequence of Scala imports that are automatically imported in the sbt build files. */
  def autoImports: List[String]
  
  /** The Scala build target describing the scala
   * version and scala jars used by this sbt version. */
  def scalaBuildTarget: ScalaBuildTarget
  
  /** An optional parent if the target has an sbt meta project. */
  def parent: Option[BuildTargetIdentifier]
  
  /** The inverse of parent, list of targets that have this build target
    * defined as their parent. It can contain normal project targets or
    * sbt build targets if this target represents an sbt meta-meta build. */
  def children: List[BuildTargetIdentifier]
}
```

For example, say we have a project in `/foo/bar` defining projects `A` and `B` and two meta builds
`M1` (defined in `/foo/bar/project`) and `M2` (defined in `/foo/bar/project/project`).

The sbt build target for `M1` will have `A` and `B` as the defined targets and `M2` as the parent.
Similarly, the sbt build target for `M2` will have `M1` as the defined target and no parent.

Clients can use this information to reconstruct the tree of sbt meta builds. The
`parent` information can be defined from `children` but it's provided by the server to
simplify the data processing on the client side.

## BSP Connection Protocol

The Build Server Protocol defines a standard convention for clients to
connect to BSP servers. This protocol has been designed such that:

1. Clients do not require beforehand knowledge about a specific build tool
   to be able to connect to its server.
1. Clients can connect to build tools installed at the machine and at the
   workspace level.
1. Multiple build tools can run in the same workspace directory.

### The BSP Connection Details

The following JSON object defines the BSP connection details:

```scala
trait BspConnectionDetails {
  /** The name of the build tool. */
  def name: String
  /** The version of the build tool. */
  def version: String
  /** The bsp version of the build tool. */
  def bspVersion: String
  /** A collection of languages supported by this BSP server. */
  def languages: List[String]
  /** Command arguments runnable via system processes to start a BSP server */
  def argv: List[String]
}
```

Every build tool supporting BSP must implement a build-tool-specific command to
generate the BSP connection details in one of the standard BSP locations for
BSP connection files.

BSP connection files:

1. must be unique per build tool name and version to enable different versions
   of the same build tool to select different BSP connection mechanisms.
1. can be updated by the build tool at any point in time, including during the
   startup of the build tool in a workspace.
1. can be added to version control if and only if they do not contain
   machine-dependent information like absolute paths or workspace-specific
   data.

This is an example of a BSP connection file:

```json
{
 "name": "My Build Tool",
 "version": "21.3",
 "bspVersion": "2.0.0",
 "languages": ["scala", "javascript", "rust"],
 "argv": ["my-build-tool", "bsp"]
}
```

#### Default Locations for BSP Connection Files

A BSP connection file can be located in a number of locations. BSP connection files
may be located in the project workspace, or for bsp servers installed locally, 
in a system-wide or user-specific data directory, depending on the operating system:

|           | Unix + Mac                                          | Windows                    |
|-----------|-----------------------------------------------------|----------------------------|
| Workspace | `<workspace-dir>/.bsp/`                             | `<workspace-dir>\.bsp\`    |
| User      | `$XDG_DATA_HOME/bsp/`                               | `%LOCALAPPDATA%\bsp\`      |
|           | `$HOME/Library/Application Support/bsp/` (Mac only) |                            |
| System    | `$XDG_DATA_DIRS/bsp/`                               | `%PROGRAMDATA%\bsp\`       |
|           | `/Library/Application Support/bsp/` (Mac only)      |                            |

Note that:

1. `<workspace-dir>` refers to the workspace base directory.
1. `$XDG_DATA_HOME` and `$XDG_DATA_DIRS` are defined by the [XDG Base Directory
Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-0.6.html)
1. `%LOCALAPPDATA%` and `%PROGRAMDATA%` are defined by the 
[Windows Documentation](https://docs.microsoft.com/en-gb/windows/desktop/shell/csidl) 
(see also: [Default Known Folders](https://docs.microsoft.com/en-gb/windows/desktop/shell/knownfolderid))
1. on Macs, both standard macOS and Unix directories are supported

The workspace location always has higher priority than the user or system location, so if
a client finds a BSP connection file that meets its criteria inside a workspace
location it must pick it over other BSP connection files in the user or system
location.

Workspace-defined build tools must not write BSP connection files to the user or system
locations. That location is only reserved for BSP connection files that do not
contain any workspace-specific data.

#### Policy around Connection Files Generation

To have a successful first-time connection to servers, at least one BSP
connection file must exist before users import a project in an IDE or invoke
a BSP client in a workspace.

Build tools installed globally by the user should write a BSP connection file
to the system location to minimize the chances that a client doesn't discover
it. The BSP connection file should also be deleted when the build tool is
uninstalled.

However, in the more general case, build tools are required to implement
a command to generate a BSP connection file either in the user or system
location. This command must be runnable in the workspace base directory.

With such command, the following workflows become possible:

1. Users can manually install a BSP connection file for any build tool.
1. Clients can implement smart discovery capabilities to:
    1. Detect the build tool(s) used in a workspace.
    1. Invoke the command to generate a BSP connection file for them.

These workflows help improve the user experience for clients that want a more
out-of-the-box experience and provide a escape hatch for users to generate BSP
connection files for exotic and unsupported build tools.

#### Build Tool Commands to Start BSP Servers

The most important data field in the connection file is the `argv` JSON field.
The `argv` field contains the command arguments that start a BSP server via
system process.

Clients must meet the following requirements when using `argv` via system process:

1. The first element of the `argv` collection can be a simple name, a relative
   path or an absolute path. A relative path is always relative to the
   workspace base directory, so the client must prepend the value of the
   workspace folder to the relative path before spawning `argv`.
1. `argv` must always be invoked in the workspace base directory.
1. `argv` must be invoked with the same environment variables of the client.

Build tools must make sure that their `argv` invocation:

1. Creates a fresh BSP connection to a server every time. This is required in
   case there is more than one client connecting to a server or a server
   crashes and a client wants to reconnect.
1. Uses `stdin` to send messages and `stdout` to receive responses to/from the
   BSP server.
1. Uses `stderr` to report execution progress to the user.

The use of `stdin` and `stdout` to communicate with the build server simplifies
the life of clients and allows build tools to implement their own underlying
protocol to connect to a local/remote build tool instance/daemon.

In addition, build tools can use the `argv` invocation for other purposes such
as:

1. Spawn a daemon if it's not already running.
1. Install the build tool if it's not already installed in a user's machine.

##### Example with `my-build-tool`

To illustrate the responsibilities of the build tool, let's go through a small
example where:

1. The `my-build-tool` build tool is installed in the user's machine.
1. The `argv` field is set to `["my-build-tool", "bsp"]`.
1. There is no running build tool instance in a workspace directory
   `<workspace>`.
1. `my-build-tool` supports BSP connections with a running instance of the
   build tool via [UNIX domain
   sockets](https://en.wikipedia.org/wiki/Unix_domain_socket) and [Windows
   Named
   Pipes](https://docs.microsoft.com/en-us/windows/desktop/ipc/named-pipes).

The invocation of `my-build-tool bsp`, with current working directory
`<workspace>`, will need to:

1. Run a background process of the build tool for the given `<workspace>`.
1. Pick the best way to connect to the running process depending on the machine
   it runs. For example, it would use UNIX sockets in a Linux machine.
1. Fire up a BSP server in the build tool with script-specific connection details.
   In the case of Unix sockets, the script will generate the socket file and
   pass it to the background process of the build tool.
1. Connect to the running BSP server, forward anything that comes from
   `stdin` to the BSP server and print anything that comes from the server's
   output streams to `stdout`. Execution progress will be shown in `stderr`.

If the build tool is already running for a given project, the `argv` invocation
will only perform the last two steps.

### Clients Connecting to BSP Servers

The BSP Connection Protocol aims to simplify clients the process of connecting
to servers.

Clients can connect to servers by locating connection files in the standard BSP
locations. BSP clients must look up connection files first in the bsp user
location and, only if the lookup of a connection file meeting certain criteria
fails, continue the search in the system location.

When more than a BSP connection file is found, BSP clients can use
connection metadata to pick only the BSP servers they are interested in. If there
are still ambiguities, BSP clients are free to choose how to react, for example
by asking the end user to select a build server.

When no BSP connection file is found (because, for example, the user has not
run the build tool command to generate BSP connection details), the BSP client
can:

1. Fail gracefully.
1. Ask users to type the command to generate the BSP connection details with
   their preferred build tool and then connect to the BSP server.
1. Discover the build tool used in a project manually, run the command to
   generate the BSP connection details and then connect to the BSP server.

When BSP clients have found a valid connection file, they can connect to the
server by running the `argv` invocation via system process; listening to its
system output and writing to its system input. If the `argv` invocation fails,
the output in `stderr` must be shown to the user.

## Appendix

### Scala Bindings

A Scala library implementation of this communication protocol is available in this repository.
The public API of this library currently has three direct Scala dependencies:

* Scribe - for logging
* Monix - for asynchronous programming primitives
* Circe - for JSON serialization and parsing of protocol data structures

### FAQ

**Q**: What's the relationship between BSP and LSP?

**A**: They are complementary protocols.
While LSP specifies endpoints for communication between an **editor acting as client** and language server, BSP specifies endpoints between a **language server acting as client** and build server.
For example, in order to respond to a `textDocument/definition` request from an editor client, a language server could query a build tool via BSP for the classpath of a module.

**Q**: What's the relationship between implementations of BSP and implementations of LSP like
[dragos/dragos-vscode-scala](https://github.com/dragos/dragos-vscode-scala),
[Dotty IDE](https://marketplace.visualstudio.com/items?itemName=lampepfl.dotty) or
[Metals](https://github.com/scalameta/metals)?

**A**: Currently, those language servers each implement custom integrations for each supported build tool to extract build metadata.
Those language servers could instead implement a BSP client to extract build metadata from any build tools that implement BSP, sharing a single BSP server implementation.
Likewise, a new build tool could implement a BSP server and support a wide range of language servers out-of-the-box.

**Q**: Should non-Scala participants in the protocol generate data types from `bsp.proto` or is it preferable to use pre-generated artifacts in maven (or other repos)?

**A**: BSP uses JSON on the wire like LSP, it is not necessary to use `bsp.proto`.
The `bsp.proto` schema is provided as a language-agnostic reference schema for the shape of BSP data structures, similarly to how LSP messages are defined using TypeScript interfaces.
Like with LSP, it is left to BSP participant to figure out how to produce JSON payloads with BSP data structures.
