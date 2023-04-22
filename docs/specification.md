---
id: specification
title: Build Server Protocol
sidebar_label: Specification
---

This document is the specification of the Build Server Protocol (BSP).

Edits to this specification can be made via a pull request against this markdown
document, see "edit" button at the bottom of this page on the website.

## Motivation

The goal of BSP is to reduce the effort required by tooling developers to
integrate between available IDEs and build tools. Currently, every IDE must
implement a custom integration for each supported build tool in order to extract
information such as source directory layouts or compiler options. Likewise, new
build tools are expected to integrate with all available IDEs. The growing
number of IDEs and build tools in the wider programming community means tooling
developers spend a lot of time working on these integrations.

The Build Server Protocol defines common functionality that both build tools
(servers) and IDEs (clients) understand. This common functionality enables
tooling developers to provide their end users the best developer experience
while supporting build tools and language servers with less effort and time.

## Background

The Build Server Protocol takes inspiration from the Language Server Protocol
(LSP). Unlike in the Language Server Protocol, the language server or IDE is
referred to as the “client” and a build tool such as sbt/Gradle/Bazel is
referred to as the “server”.

The best way to read this document is by considering it as a wishlist from the
perspective of an IDE developer.

The code listings in this document are written using TypeScript syntax. Every
data strucuture in this document has a direct translation to JSON and Protobuf.

## Relationship with LSP

BSP can be used together with LSP in the same architecture. The diagram below
illustrates an example how an LSP server can also act as a BSP client.

![](https://i.imgur.com/q4KEas9.png)

BSP can also be used without LSP. In the example above, IntelliJ acts as a BSP
client even if IntelliJ does not use LSP.

## Status

The Build Server Protocol is not an approved standard. Everything in this
document is subject to change and open for discussions, including core data
structures.

The creation of BSP clients and servers is under active development.

In the clients space, IntelliJ has been the first language server to implement
BSP. The integration is available in the nightly releases of the Scala plugin.
Other language servers, like [Dotty IDE](https://github.com/lampepfl/dotty) and
[scalameta/metals](https://github.com/scalameta/metals), are currently working
or planning to work on a BSP integrations.

On the server side,

- [Bloop](https://github.com/scalacenter/bloop) was the first
  server to implement BSP
- sbt added built-in support in [1.4.0](https://github.com/sbt/sbt/pull/5538),
- Mill ships with [built-in BSP support](https://com-lihaoyi.github.io/mill/mill/Intro_to_Mill.html#_build_server_protocol_bsp)
- Bazel support is provided by [bazel-bsp](https://github.com/JetBrains/bazel-bsp)

We're looking for third parties that implement BSP natively in other build tools
such as Gradle.

The Build Server Protocol has been designed to be language-agnostic. We're
looking for ways to collaborate with other programming language communities and
build tool authors.

The best way to share your thoughts on the Build Server Protocol or to get
involved in its development is to open an issue or pull request to this
repository. Any help on developing integrations will be much appreciated.

## Base protocol

The base protocol is identical to the language server base protocol. See
<https://microsoft.github.io/language-server-protocol/specification> for more
details.

Like the language server protocol, the build server protocol defines a set of
JSON-RPC request, response and notification messages which are exchanged using
the base protocol.

## Basic Json Structures

In addition to basic data structures in the
[General section of the Language Server Protocol](https://microsoft.github.io/language-server-protocol/specification#general),
the Build Server Protocol defines the following additional data structures.

### Build Target

Build target contains metadata about an artifact (for example library, test, or
binary artifact). Using vocabulary of other build tools:

- sbt: a build target is a combined project + config. Example:
  - a regular JVM project with main and test configurations will have 2 build
    targets, one for main and one for test.
  - a single configuration in a single project that contains both Java and Scala
    sources maps to one BuildTarget.
  - a project with crossScalaVersions 2.11 and 2.12 containing main and test
    configuration in each will have 4 build targets.
  - a Scala 2.11 and 2.12 cross-built project for Scala.js and the JVM with main
    and test configurations will have 8 build targets.
- Pants: a pants target corresponds one-to-one with a BuildTarget
- Bazel: a bazel target corresponds one-to-one with a BuildTarget

The general idea is that the BuildTarget data structure should contain only
information that is fast or cheap to compute.

```ts
export interface BuildTarget {
  /** The target’s unique identifier */
  id: BuildTargetIdentifier;

  /** A human readable name for this target.
   * May be presented in the user interface.
   * Should be unique if possible.
   * The id.uri is used if None. */
  displayName?: String;

  /** The directory where this target belongs to. Multiple build targets are allowed to map
   * to the same base directory, and a build target is not required to have a base directory.
   * A base directory does not determine the sources of a target, see buildTarget/sources. */
  baseDirectory?: Uri;

  /** Free-form string tags to categorize or label this build target.
   * For example, can be used by the client to:
   * - customize how the target should be translated into the client's project model.
   * - group together different but related targets in the user interface.
   * - display icons or colors in the user interface.
   * Pre-defined tags are listed in `BuildTargetTag` but clients and servers
   * are free to define new tags for custom purposes.
   */
  tags: String[];

  /** The capabilities of this build target. */
  capabilities: BuildTargetCapabilities;

  /** The set of languages that this target contains.
   * The ID string for each language is defined in the LSP. */
  languageIds: String[];

  /** The direct upstream build target dependencies of this build target */
  dependencies: BuildTargetIdentifier[];

  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  dataKind?: String;

  /** Language-specific metadata about this target.
   * See ScalaBuildTarget as an example. */
  data?: any;
}

export namespace BuildTargetDataKind {
  /** The `data` field contains a `ScalaBuildTarget` object. */
  export const Scala = "scala";

  /** The `data` field contains a `SbtBuildTarget` object. */
  export const Sbt = "sbt";
}

export namespace BuildTargetTag {
  /** Target contains re-usable functionality for downstream targets. May have any
   * combination of capabilities. */
  export const Library = "library";

  /** Target contains source code for producing any kind of application, may have
   * but does not require the `canRun` capability. */
  export const Application = "application";

  /** Target contains source code for testing purposes, may have but does not
   * require the `canTest` capability. */
  export const Test = "test";

  /** Target contains source code for integration testing purposes, may have
   * but does not require the `canTest` capability.
   * The difference between "test" and "integration-test" is that
   * integration tests traditionally run slower compared to normal tests
   * and require more computing resources to execute.
   */
  export const IntegrationTest = "integration-test";

  /** Target contains source code to measure performance of a program, may have
   * but does not require the `canRun` build target capability.
   */
  export const Benchmark = "benchmark";

  /** Target should be ignored by IDEs. */
  export const NoIDE = "no-ide";

  /** Actions on the target such as build and test should only be invoked manually
   * and explicitly. For example, triggering a build on all targets in the workspace
   * should by default not include this target.
   *
   * The original motivation to add the "manual" tag comes from a similar functionality
   * that exists in Bazel, where targets with this tag have to be specified explicitly
   * on the command line.
   */
  export const Manual = "manual";
}

export interface BuildTargetCapabilities {
  /** This target can be compiled by the BSP server. */
  canCompile: Boolean;
  /** This target can be tested by the BSP server. */
  canTest: Boolean;
  /** This target can be run by the BSP server. */
  canRun: Boolean;
  /** This target can be debugged by the BSP server. */
  canDebug: Boolean;
}
```

### Build Target Identifier

A unique identifier for a target, can use any URI-compatible encoding as long as
it is unique within the workspace. Clients should not infer metadata out of the
URI structure such as the path or query parameters, use `BuildTarget` instead.

```ts
export interface BuildTargetIdentifier {
  /** The target’s Uri */
  uri: Uri;
}
```

### Task Id

The Task Id allows clients to _uniquely_ identify a BSP task and establish a
client-parent relationship with another task id.

```ts
export interface TaskId {
  /** A unique identifier */
  id: String;

  /** The parent task ids, if any. A non-empty parents field means
   * this task is a sub-task of every parent task id. The child-parent
   * relationship of tasks makes it possible to render tasks in
   * a tree-like user interface or inspect what caused a certain task
   * execution. */
  parents?: String[];
}
```

### Status Code

Included in notifications of tasks or requests to signal the completion state.

```ts
export namespace StatusCode {
  /** Execution was successful. */
  export const Ok = 1;
  /** Execution failed. */
  export const Error = 2;
  /** Execution was cancelled. */
  export const Cancelled = 3;
}
```

### Uri

```ts
/**  A resource identifier that is a valid URI according
 * to rfc3986: * https://tools.ietf.org/html/rfc3986 */
type Uri = String;
```

## Actual Protocol

Unlike the language server protocol, the build server protocol does not support
dynamic registration of capabilities. The motivation for this change is
simplicity. If a motivating example for dynamic registration comes up this
decision can be reconsidered. The server and client capabilities must be
communicated through the initialize request.

### Server Lifetime

Like the language server protocol, the current protocol specification defines
that the lifetime of a build server is managed by the client (e.g. a language
server like Dotty IDE). It is up to the client to decide when to start
(process-wise) and when to shutdown a server.

#### Initialize Build Request

Like the language server protocol, the initialize request is sent as the first
request from the client to the server. If the server receives a request or
notification before the initialize request it should act as follows:

- For a request the response should be an error with code: -32002. The message
  can be picked by the server.

- Notifications should be dropped, except for the exit notification. This will
  allow the exit of a server without an initialize request.

Until the server has responded to the initialize request with an
InitializeBuildResult, the client must not send any additional requests or
notifications to the server.

Request:

- method: ‘build/initialize’

- params: InitializeBuildParams defined as follows

```ts
export interface InitializeBuildParams {
  /** Name of the client */
  displayName: String;

  /** The version of the client */
  version: String;

  /** The BSP version that the client speaks */
  bspVersion: String;

  /** The rootUri of the workspace */
  rootUri: Uri;

  /** The capabilities of the client */
  capabilities: BuildClientCapabilities;

  /** Additional metadata about the client */
  data?: any;
}

export interface BuildClientCapabilities {
  /** The languages that this client supports.
   * The ID strings for each language is defined in the LSP.
   * The server must never respond with build targets for other
   * languages than those that appear in this list. */
  languageIds: String[];
}
```

Response:

- result: InitializeBuildResult defined as follows

```ts
export interface InitializeBuildResult {
  /** Name of the server */
  displayName: String;

  /** The version of the server */
  version: String;

  /** The BSP version that the server speaks */
  bspVersion: String;

  /** The capabilities of the build server */
  capabilities: BuildServerCapabilities;

  /** Additional metadata about the server */
  data?: any;
}

export interface BuildServerCapabilities {
  /** The languages the server supports compilation via method buildTarget/compile. */
  compileProvider?: CompileProvider;

  /** The languages the server supports test execution via method buildTarget/test */
  testProvider?: TestProvider;

  /** The languages the server supports run via method buildTarget/run */
  runProvider?: RunProvider;

  /** The languages the server supports debugging via method debugSession/start */
  debugProvider?: DebugProvider;

  /** The server can provide a list of targets that contain a
   * single text document via the method buildTarget/inverseSources */
  inverseSourcesProvider?: Boolean;

  /** The server provides sources for library dependencies
   * via method buildTarget/dependencySources */
  dependencySourcesProvider?: Boolean;

  /** The server can provide a list of dependency modules (libraries with meta information)
   * via method buildTarget/dependencyModules */
  dependencyModulesProvider?: Boolean;

  /** The server provides all the resource dependencies
   * via method buildTarget/resources */
  resourcesProvider?: Boolean;

  /** The server provides all output paths
   * via method buildTarget/outputPaths */
  outputPathsProvider?: Boolean;

  /** The server sends notifications to the client on build
   * target change events via buildTarget/didChange */
  buildTargetChangedProvider?: Boolean;

  /** The server can respond to `buildTarget/jvmRunEnvironment` requests with the
   * necessary information required to launch a Java process to run a main class. */
  jvmRunEnvironmentProvider?: Boolean;

  /** The server can respond to `buildTarget/jvmTestEnvironment` requests with the
   * necessary information required to launch a Java process for testing or
   * debugging. */
  jvmTestEnvironmentProvider?: Boolean;

  /** Reloading the build state through workspace/reload is supported */
  canReload?: Boolean;
}

export interface CompileProvider {
  languageIds: String[];
}

export interface RunProvider {
  languageIds: String[];
}

export interface DebugProvider {
  languageIds: String[];
}

export interface TestProvider {
  languageIds: String[];
}
```

Clients can use these capabilities to notify users what BSP endpoints can and
cannot be used and why.

#### Initialized Build Notification

Like the language server protocol, the initialized notification is sent from the
client to the server after the client received the result of the initialize
request but before the client is sending any other request or notification to
the server. The server can use the initialized notification for example to
initialize intensive computation such as dependency resolution or compilation.
The initialized notification may only be sent once.

Notification:

- method: ‘build/initialized’
- params: InitializedBuildParams defined as follows

```ts
export interface InitializedBuildParams {}
```

#### Shutdown Build Request

Like the language server protocol, the shutdown build request is sent from the
client to the server. It asks the server to shut down, but to not exit
(otherwise the response might not be delivered correctly to the client). There
is a separate exit notification that asks the server to exit.

Request:

- method: `build/shutdown`
- params: `null`

Response:

- result: `null`
- error: code and message set in case an exception happens during shutdown
  request.

#### Exit Build Notification

Like the language server protocol, a notification to ask the server to exit its
process. The server should exit with success code 0 if the shutdown request has
been received before; otherwise with error code 1.

Notification:

- method: `build/exit`
- params: `null`

#### Show message

The show message notification is sent from a server to a client to ask the
client to display a particular message in the user interface.

Notification:

- method: `build/showMessage`
- params: `ShowMessageParams` defined as follows:

```ts
export interface ShowMessageParams {
  /** The message type. See {@link MessageType}. */
  type: Int;

  /** The task id if any. */
  task?: TaskId;

  /** The request id that originated this notification. */
  originId?: String;

  /** The actual message. */
  message: String;
}
```

where `MessageType` is defined as follows:

```ts
export namespace MessageType {
  /** An error message. */
  export const Error = 1;
  /** A warning message. */
  export const Warning = 2;
  /** An information message. */
  export const Info = 3;
  /** A log message. */
  export const Log = 4;
}
```

A `build/showMessage` notification is similar to LSP's `window/showMessage`,
except for a few additions like `id` and `originId`.

The `originId` field helps clients know which request originated a notification
in case several requests are handled by the client at the same time. It will
only be populated if the client defined it in the request that triggered this
notification.

#### Log message

The log message notification is sent from the server to the client to ask the
client to log a particular message.

Notification:

- method: ‘build/logMessage’
- params: LogMessageParams defined as follows:

```ts
export interface LogMessageParams {
  /** The message type. See {@link MessageType} */
  type: Int;

  /** The task id if any. */
  task?: TaskId;

  /** The request id that originated this notification. */
  originId?: String;

  /** The actual message */
  message: String;
}
```

Where type is defined as `build/showMessage`.

A `build/logMessage` notification is similar to LSP's `window/logMessage`,
except for a few additions like `id` and `originId`.

The `originId` field helps clients know which request originated a notification
in case several requests are handled by the client at the same time. It will
only be populated if the client defined it in the request that triggered this
notification.

#### Publish Diagnostics

The Diagnostics notification are sent from the server to the client to signal
results of validation runs.

Notification:

- method: `build/publishDiagnostics`
- params: `PublishDiagnosticsParams` defined as follows:

```ts
export interface PublishDiagnosticsParams {
  /** The document where the diagnostics are published. */
  textDocument: TextDocumentIdentifier;

  /** The build target where the diagnostics origin.
   * It is valid for one text document to belong to multiple
   * build targets, for example sources that are compiled against multiple
   * platforms (JVM, JavaScript). */
  buildTarget: BuildTargetIdentifier;

  /** The request id that originated this notification. */
  originId?: String;

  /** The diagnostics to be published by the client. */
  diagnostics: Diagnostic[];

  /** Whether the client should clear the previous diagnostics
   * mapped to the same `textDocument` and `buildTarget`. */
  reset: Boolean;
}
```

where `Diagnostic` is defined as it is in LSP.

When `reset` is true, the client must clean all previous diagnostics associated
with the same `textDocument` and `buildTarget` and set instead the diagnostics
in the request. This is the same behaviour as `PublishDiagnosticsParams` in the
LSP. When `reset` is false, the diagnostics are added to the last active
diagnostics, allowing build tools to stream diagnostics to the client.

It is the server's responsibility to manage the lifetime of the diagnostics by
using the appropriate value in the `reset` field. Clients generate new
diagnostics by calling any BSP endpoint that triggers a `buildTarget/compile`,
such as `buildTarget/compile`, `buildTarget/test` and `buildTarget/run`.

If the computed set of diagnostic is empty, the server must push an empty array
with `reset` set to true, in order to clear previous diagnostics.

The optional `originId` field in the definition of `PublishDiagnosticsParams`
can be used by clients to know which request originated the notification. This
field will be defined if the client defined it in the original request that
triggered this notification.

### Workspace Build Targets Request

The workspace build targets request is sent from the client to the server to ask
for the list of all available build targets in the workspace.

Request:

- method: 'workspace/buildTargets'
- params: `WorkspaceBuildTargetsParams`, defined as follows

```ts
export interface WorkspaceBuildTargetsParams {}
```

Response:

- result: `WorkspaceBuildTargetsResult`, defined as follows

```ts
export interface WorkspaceBuildTargetsResult {
  /** The build targets in this workspace that
   * contain sources with the given language ids. */
  targets: BuildTarget[];
}
```

### Reload request

The `reload` request is sent from the client to instruct the build server to reload
the build configuration. This request should be supported by build tools that keep
their state in memory. If the `reload` request returns with an error, it's expected
that other requests respond with the previously known "good" state.

Request:

- method: `workspace/reload`
- params: `null`

Response:

- result: `null`
- error: code and message in case an error happens during reload. For example,
  when the build configuration is invalid.

### Build Target Changed Notification

The build target changed notification is sent from the server to the client to
signal a change in a build target. The server communicates during the initialize
handshake whether this method is supported or not.

Notification:

- method: 'buildTarget/didChange'
- params: `DidChangeBuildTargetParams` defined as follows:

```ts
export interface DidChangeBuildTarget {
  changes: BuildTargetEvent[];
}

export interface BuildTargetEvent {
  /** The identifier for the changed build target */
  target: BuildTargetIdentifier;

  /** The kind of change for this build target */
  kind?: Int;

  /** Any additional metadata about what information changed. */
  data?: any;
}
```

where the `kind` is defined as follows:

```ts
export namespace BuildTargetEventKind {
  /** The build target is new. */
  export const Created = 1;

  /** The build target has changed. */
  export const Changed = 2;

  /** The build target has been deleted. */
  export const Deleted = 3;
}
```

The `BuildTargetEventKind` information can be used by clients to trigger
reindexing or update the user interface with the new information.

### Build Target Sources Request

The build target sources request is sent from the client to the server to query
for the list of text documents and directories that are belong to a build
target. The sources response must not include sources that are external to the
workspace, see `buildTarget/dependencySources`.

- method: `buildTarget/sources`
- params: `SourcesParams`

```ts
export interface SourcesParams {
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `BuildTargetSourcesResult`, defined as follows

```ts
export interface SourcesResult {
  items: SourcesItem[];
}

export interface SourcesItem {
  target: BuildTargetIdentifier;
  /** The text documents or and directories that belong to this build target. */
  sources: SourceItem[];
  /** The root directories from where source files should be relativized.
   *
   * Example: ["file://Users/name/dev/metals/src/main/scala"]
   */
  roots?: Uri[];
}

export interface SourceItem {
  /** Either a text document or a directory. A directory entry must end with a forward
   * slash "/" and a directory entry implies that every nested text document within the
   * directory belongs to this source item.
   */
  uri: Uri;

  /** Type of file of the source item, such as whether it is file or directory.
   */
  kind: SourceItemKind;

  /** Indicates if this source is automatically generated by the build and is not
   * intended to be manually edited by the user. */
  generated: Boolean;
}

export namespace SourceItemKind {
  /** The source item references a normal file.  */
  export const File: Int = 1;
  /** The source item references a directory. */
  export const Directory: Int = 2;
}
```

### Inverse Sources Request

The inverse sources request is sent from the client to the server to query for
the list of build targets containing a text document. The server communicates
during the initialize handshake whether this method is supported or not. This
request can be viewed as the inverse of `buildTarget/sources`, except it only
works for text documents and not directories.

- method: `textDocument/inverseSources`
- params: `InverseSourcesParams`, defined as follows

```ts
export interface InverseSourcesParams {
  textDocument: TextDocumentIdentifier;
}
```

Response:

- result: `InverseSourcesResult`, defined as follows

```ts
export interface InverseSourcesResult {
  targets: BuildTargetIdentifier[];
}
```

### Dependency Sources Request

The build target dependency sources request is sent from the client to the
server to query for the sources of build target dependencies that are external
to the workspace. The dependency sources response must not include source files
that belong to a build target within the workspace, see `buildTarget/sources`.

The server communicates during the initialize handshake whether this method is
supported or not. This method can for example be used by a language server on
`textDocument/definition` to "Go to definition" from project sources to
dependency sources.

- method: `buildTarget/dependencySources`
- params: `DependencySourcesParams`

```ts
export interface DependencySourcesParams {
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `DependencySourcesResult`, defined as follows

```ts
export interface DependencySourcesResult {
  items: DependencySourcesItem[];
}
export interface DependencySourcesItem {
  target: BuildTargetIdentifier;
  /** List of resources containing source files of the
   * target's dependencies.
   * Can be source files, jar files, zip files, or directories. */
  sources: Uri[];
}
```

### Dependency Modules Request

The build target dependency modules request is sent from the client to the
server to query for the libraries of build target dependencies that are external
to the workspace including meta information about library and their sources.
It's an extended version of `buildTarget/sources`.

- method: `buildTarget/dependencyModules`
- params: `DependencyModulesParams`

```ts
export interface DependencyModulesParams {
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `DependencyModulesResult`, defined as follows

```ts
export interface DependencyModulesResult {
  items: DependencyModulesItem[];
}
export interface DependencyModulesItem {
  target: BuildTargetIdentifier;
  modules: DependencyModule[];
}
export interface DependencyModule {
  /** Module name */
  name: String;

  /** Module version */
  version: String;

  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  dataKind?: String;

  /** Language-specific metadata about this module.
   * See MavenDependencyModule as an example. */
  data?: any;
}
```

### Resources Request

The build target resources request is sent from the client to the server to
query for the list of resources of a given list of build targets.

A resource is a data dependency required to be present in the runtime classpath
when a build target is run or executed. The server communicates during the
initialize handshake whether this method is supported or not.

This request can be used by a client to highlight the resources in a project
view, for example.

- method: `buildTarget/resources`
- params: `ResourcesParams`

```ts
export interface ResourcesParams {
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `ResourcesResult`, defined as follows

```ts
export interface ResourcesResult {
  items: ResourcesItem[];
}
export interface ResourcesItem {
  target: BuildTargetIdentifier;
  /** List of resource files. */
  resources: Uri[];
}
```

### Output Paths Request

The build target output paths request is sent from the client to the server to
query for the list of output paths of a given list of build targets.

An output path is a file or directory that contains output files such as build
artifacts which IDEs may decide to exclude from indexing. The server communicates
during the initialize handshake whether this method is supported or not.

- method: `buildTarget/outputPaths`
- params: `OutputPathsParams`

```ts
export interface OutputPathsParams {
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `OutputPathsResult`, defined as follows

```ts
export interface OutputPathsResult {
  items: OutputPathsItem[];
}

export interface OutputPathsItem {
  /** A build target to which output paths item belongs.
   */
  target: BuildTargetIdentifier;

  /** Output paths.
   */
  outputPaths: OutputPathItem[];
}

export interface OutputPathItem {
  /** Either a file or a directory. A directory entry must end with a forward
   * slash "/" and a directory entry implies that every nested path within the
   * directory belongs to this output item.
   */
  uri: Uri;

  /** Type of file of the output item, such as whether it is file or directory.
   */
  kind: OutputPathItemKind;
}

export namespace OutputPathItemKind {
  /** The output path item references a normal file.  */
  export const File: Int = 1;
  /** The output path item references a directory. */
  export const Directory: Int = 2;
}
```

### Task Notifications

The BSP server can inform the client on the execution state of any task in the
build tool. The execution of some tasks, such as compilation or tests, must
always be reported by the server.

The server may also send additional task notifications for actions not covered
by the protocol, such as resolution or packaging. BSP clients can then display
this information to their users at their discretion.

When beginning a task, the server may send `build/taskStart`, intermediate
updates may be sent in `build/taskProgress`.

If a `build/taskStart` notification has been sent, the server must send
`build/taskFinish` on completion of the same task. Conversely, a
`build/taskFinish` notification must always be sent after a `build/taskStart`
with the same `taskId` was sent.

`build/taskStart`, `build/taskProgress` and `build/taskFinish` notifications for
the same task must use the same `taskId`.

Tasks that are spawned by another task should reference the originating task's
`taskId` in their own `taskId`'s `parent` field. Tasks spawned directly by a
request should reference the request's `originId` parent.

#### Task Started

Notification:

- method: `build/taskStart`
- params: `TaskStartParams` defined as follows:

```ts
export interface TaskStartParams {
  /** Unique id of the task with optional reference to parent task id */
  taskId: TaskId;

  /** Timestamp of when the event started in milliseconds since Epoch. */
  eventTime?: Long;

  /** Message describing the task. */
  message?: String;

  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified.
   * Kind names for specific tasks like compile, test, etc are specified in the protocol.
   */
  dataKind?: String;

  /** Optional metadata about the task.
   * Objects for specific tasks like compile, test, etc are specified in the protocol.
   */
  data?: any;
}
```

#### Task Progress

After a `taskStart` and before `taskFinish` for a `taskId`, the server may send
any number of progress notifications.

- method: `build/taskProgress`
- params: `TaskProgressParams` defined as follows:

```ts
export interface TaskProgressParams {
  /** Unique id of the task with optional reference to parent task id */
  taskId: TaskId;

  /** Timestamp of when the progress event was generated in milliseconds since Epoch. */
  eventTime?: Long;

  /** Message describing the task progress.
   * Information about the state of the task at the time the event is sent. */
  message?: String;

  /** If known, total amount of work units in this task. */
  total?: Long;

  /** If known, completed amount of work units in this task. */
  progress?: Long;

  /** Name of a work unit. For example, "files" or "tests". May be empty. */
  unit?: String;

  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified.
   * Kind names for specific tasks like compile, test, etc are specified in the protocol.
   */
  dataKind?: String;

  /** Optional metadata about the task.
   * Objects for specific tasks like compile, test, etc are specified in the protocol.
   */
  data?: any;
}
```

#### Task Finished

- method: `build/taskFinish`
- params: `TaskFinishParams` defined as follows:

```ts
export interface TaskFinishParams {
  /** Unique id of the task with optional reference to parent task id */
  taskId: TaskId;

  /** Timestamp of the event in milliseconds. */
  eventTime?: Long;

  /** Message describing the finish event. */
  message?: String;

  /** Task completion status. */
  status: StatusCode;

  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified.
   * Kind names for specific tasks like compile, test, etc are specified in the protocol.
   */
  dataKind?: String;

  /** Optional metadata about the task.
   * Objects for specific tasks like compile, test, etc are specified in the protocol.
   */
  data?: any;
}
```

#### Task Data

Task progress notifications may contain an arbitrary interface in their `data`
field. The kind of interface that is contained in a notification must be
specified in the `dataKind` field.

There are predefined kinds of objects for test and compile tasks, as described
in the [Compile Request](#compile-request) and [Test Request](#test-request)
sections. These are declared by predefined `dataKind` strings in task
notifications:

```ts
export namespace TaskDataKind {
  /** `data` field must contain a CompileTask object. */
  export const CompileTask = "compile-task";

  /** `data` field must contain a CompileReport object. */
  export const CompileReport = "compile-report";

  /** `data` field must contain a TestTask object. */
  export const TestTask = "test-task";

  /** `data` field must contain a TestReport object. */
  export const TestReport = "test-report";

  /** `data` field must contain a TestStart object. */
  export const TestStart = "test-start";

  /** `data` field must contain a TestFinish object. */
  export const TestFinish = "test-finish";
}
```

### Compile Request

The compile build target request is sent from the client to the server to
compile the given list of build targets. The server communicates during the
initialize handshake whether this method is supported or not. This method can
for example be used by a language server before `textDocument/rename` to ensure
that all workspace sources typecheck correctly and are up-to-date.

- method: `buildTarget/compile`
- params: `CompileParams`

```ts
export interface CompileParams {
  /** A sequence of build targets to compile. */
  targets: BuildTargetIdentifier[];

  /** A unique identifier generated by the client to identify this request.
   * The server may include this id in triggered notifications or responses. */
  originId?: String;

  /** Optional arguments to the compilation process. */
  arguments?: String[];
}
```

Response:

- result: `CompileResult`, defined as follows
- error: JSON-RPC code and message set in case an exception happens during the
  request.

```ts
export interface CompileResult {
  /** An optional request id to know the origin of this report. */
  originId?: String;

  /** A status code for the execution. */
  statusCode: Int;

  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  dataKind?: String;

  /** A field containing language-specific information, like products
   * of compilation or compiler-specific metadata the client needs to know. */
  data?: any;
}
```

#### Compile Notifications

The beginning of a compilation unit may be signalled to the client with a
`build/taskStart` notification. When the compilation unit is a build target, the
notification's `dataKind` field must be "compile-task" and the `data` field must
include a `CompileTask` object:

```ts
export interface CompileTask {
  target: BuildTargetIdentifier;
}
```

The completion of a compilation task should be signalled with a
`build/taskFinish` notification. When the compilation unit is a build target,
the notification's `dataKind` field must be `compile-report` and the `data`
field must include a `CompileReport` object:

```ts
export interface CompileReport {
  /** The build target that was compiled. */
  target: BuildTargetIdentifier;

  /** An optional request id to know the origin of this report. */
  originId?: String;

  /** The total number of reported errors compiling this target. */
  errors: Int;

  /** The total number of reported warnings compiling the target. */
  warnings: Int;

  /** The total number of milliseconds it took to compile the target. */
  time?: Int;

  /** The compilation was a noOp compilation. */
  noOp?: Boolean;
}
```

The server is free to send any number of `build/publishDiagnostics` and
`build/logMessage` notifications during compilation before completing the
response. Any number of tasks triggered by the requests may be communicated with
`build/task*` notifications.

### Test Request

The test build target request is sent from the client to the server to test the
given list of build targets. The server communicates during the initialize
handshake whether this method is supported or not.

- method: `buildTarget/test`
- params: `TestParams`

```ts
export interface TestParams {
  /** A sequence of build targets to test. */
  targets: BuildTargetIdentifier[];

  /** A unique identifier generated by the client to identify this request.
   * The server may include this id in triggered notifications or responses. */
  originId?: String;

  /** Optional arguments to the test execution engine. */
  arguments?: String[];

  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  dataKind?: String;

  /** Language-specific metadata about for this test execution.
   * See ScalaTestParams as an example. */
  data?: any;
}
```

Response:

- result: `TestResult`, defined as follows
- error: JSON-RPC code and message set in case an exception happens during the
  request.

```ts
export interface TestResult {
  /** An optional request id to know the origin of this report. */
  originId?: String;

  /** A status code for the execution. */
  statusCode: Int;

  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  dataKind?: String;

  data?: any;
}
```

The field `data` may contain test-related language-specific information.

#### Test Notifications

The beginning of a testing unit may be signalled to the client with a
`build/taskStart` notification. When the testing unit is a build target, the
notification's `dataKind` field must be `test-task` and the `data` field must
include a `TestTask` object.

```ts
export interface TestTask {
  target: BuildTargetIdentifier;
}
```

The completion of a test task should be signalled with a `build/taskFinish`
notification. When the testing unit is a build target, the notification's
`dataKind` field must be `test-report` and the `data` field must include a
`TestReport` object:

```ts
export interface TestReport {
  /** The build target that was compiled. */
  target: BuildTargetIdentifier;

  /** The total number of successful tests. */
  passed: Int;

  /** The total number of failed tests. */
  failed: Int;

  /** The total number of ignored tests. */
  ignored: Int;

  /** The total number of cancelled tests. */
  cancelled: Int;

  /** The total number of skipped tests. */
  skipped: Int;

  /** The total number of milliseconds tests take to run (e.g. doesn't include compile times). */
  time?: Int;
}
```

This request may trigger a compilation or other tasks on the selected build
targets.

The server may send any number of `build/task*`, `build/publishDiagnostics` and
`build/logMessage` notifications to communicate about tasks triggered by the
request to the client.

#### Test Notifications

The server may inform about individual tests or groups of tests in task
notifications that reference the originating task in their `taskId`. For
example, the server can send a `taskStart`/`taskFinish` for each test suite in a
target, and likewise for each individual test in the suite. The server's
implementation decides the granularity at which tests are reported. For example,
if it only has information about all the tests in a suite at a time, it could
report a TestFinish for each test once the suite is done.

Where applicable, notifications about tests should use the `taskId` to reference
parent tasks so that the client's user interface can display test execution in a
tree view.

Individual test start notifications should specify `test-started` in the
`dataKind` field and include the `TestStart` interface and test finish
notifications should specify `test-finished` in the `dataKind` field and include
the `TestFinish` interface in the `data` field.

```ts
export interface TestStart {
  /** Name or description of the test. */
  displayName: String;

  /** Source location of the test, as LSP location. */
  location?: Location;
}

export interface TestFinish {
  /** Name or description of the test. */
  displayName: String;

  /** Information about completion of the test, for example an error message. */
  message?: String;

  /** Completion status of the test. */
  status: Int;

  /** Source location of the test, as LSP location. */
  location?: Location;

  /** Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified. */
  dataKind?: String;

  /** Optionally, structured metadata about the test completion.
   * For example: stack traces, expected/actual values. */
  data?: any;
}

export namespace TestStatus {
  /** The test passed successfully. */
  export const Passed: Int = 1;

  /** The test failed. */
  export const Failed: Int = 2;

  /** The test was marked as ignored. */
  export const Ignored: Int = 3;

  /** The test execution was cancelled. */
  export const Cancelled: Int = 4;

  /** The was not included in execution. */
  export const Skipped: Int = 5;
}
```

### Run Request

The run request is sent from the client to the server to run a build target. The
server communicates during the initialize handshake whether this method is
supported or not.

- method: `buildTarget/run`
- params: `RunParams`

```ts
export interface RunParams {
  /** The build target to run. */
  target: BuildTargetIdentifier;

  /** A unique identifier generated by the client to identify this request.
   * The server may include this id in triggered notifications or responses. */
  originId?: String;

  /** Optional arguments to the executed application. */
  arguments?: String[];

  /** Kind of data to expect in the data field. If this field is not set, the kind of data is not specified. */
  dataKind?: String;

  /** Language-specific metadata for this execution.
   * See ScalaMainClass as an example. */
  data?: any;
}
```

Note that an empty run request is valid. Run will be executed in the target as
specified in the build tool.

Response:

- result: `RunResult`, defined as follows
- error: JSON-RPC code and message set in case an exception happens during the
  request.

```ts
export interface RunResult {
  /** An optional request id to know the origin of this report. */
  originId?: String;

  /** A status code for the execution. */
  statusCode: Int;
}
```

This request may trigger a compilation on the selected build targets. The server
is free to send any number of `build/task*`, `build/publishDiagnostics` and
`build/logMessage` notifications during compilation before completing the
response.

The client will get a `originId` field in `RunResult` if the `originId` field in
the `RunParams` is defined.

### Debug Request

The debug request is sent from the client to the server to debug build target(s). The
server launches a [Microsoft DAP](https://microsoft.github.io/debug-adapter-protocol/) server
and returns a connection URI for the client to interact with.

- method: `debugSession/start`
- params: `DebugSessionParams`

```ts
export interface DebugSessionParams {
  /** A sequence of build targets affected by the debugging action. */
  targets: BuildTargetIdentifier[];

  /** The kind of data to expect in the `data` field. */
  dataKind: String;

  /** Language-specific metadata for this execution.
   * See ScalaMainClass as an example. */
  data: any;
}
```

For the scala-specific `data` options please reference the [Scala Extension
documentation](./extensions/scala.md)

Response:

- result: `DebugSessionAddress`, defined as follows
- error: JSON-RPC code and message set in case an exception happens during the
  request.

```ts
export interface DebugSessionAddress {
  /** The Debug Adapter Protocol server's connection uri */
  uri: Uri;
}
```

### Clean Cache Request

The clean cache request is sent from the client to the server to reset any state
associated with a given build target. The state can live either in the build
tool or in the file system.

The build tool defines the exact semantics of the clean cache request:

1. Stateless build tools are free to ignore the request and respond with a
   successful response.
2. Stateful build tools must ensure that invoking compilation on a target that
   has been cleaned results in a full compilation.

- method: `buildTarget/cleanCache`
- params: `CleanCacheParams`

```ts
export interface CleanCacheParams {
  /** The build targets to clean. */
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `CleanCacheResult`, defined as follows
- error: JSON-RPC code and message set in case an exception happens during the
  request.

```ts
export interface CleanCacheResult {
  /** Optional message to display to the user. */
  message?: String;
  /** Indicates whether the clean cache request was performed or not. */
  cleaned: Boolean;
}
```
