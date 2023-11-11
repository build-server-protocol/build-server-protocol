package ch.epfl.scala.bsp
package endpoints

import jsonrpc4s.Endpoint
import jsonrpc4s.Endpoint.unitCodec
object Run extends Run
trait Run {

  /** **Unstable** (may change in future versions) Notification sent from the server to the client
    * when the target being run or tested prints something to stdout.
    */
  object printStdout extends Endpoint[PrintParams, Unit]("run/printStdout")

  /** **Unstable** (may change in future versions) Notification sent from the server to the client
    * when the target being run or tested prints something to stderr.
    */
  object printStderr extends Endpoint[PrintParams, Unit]("run/printStderr")

  /** **Unstable** (may change in future versions) Notification sent from the client to the server
    * when the user wants to send input to the stdin of the running target.
    */
  object readStdin extends Endpoint[ReadParams, Unit]("run/readStdin")
}
object Build extends Build
trait Build {

  /** The show message notification is sent from a server to a client to ask the client to display a
    * particular message in the user interface.
    *
    * A build/showMessage notification is similar to LSP's window/showMessage, except for a few
    * additions like id and originId.
    */
  object showMessage extends Endpoint[ShowMessageParams, Unit]("build/showMessage")

  /** The log message notification is sent from a server to a client to ask the client to log a
    * particular message in its console.
    *
    * A build/logMessage notification is similar to LSP's window/logMessage, except for a few
    * additions like id and originId.
    */
  object logMessage extends Endpoint[LogMessageParams, Unit]("build/logMessage")

  /** The Diagnostics notification are sent from the server to the client to signal results of
    * validation runs.
    *
    * When reset is true, the client must clean all previous diagnostics associated with the same
    * textDocument and buildTarget and set instead the diagnostics in the request. This is the same
    * behaviour as PublishDiagnosticsParams in the LSP. When reset is false, the diagnostics are
    * added to the last active diagnostics, allowing build tools to stream diagnostics to the
    * client.
    *
    * It is the server's responsibility to manage the lifetime of the diagnostics by using the
    * appropriate value in the reset field. Clients generate new diagnostics by calling any BSP
    * endpoint that triggers a buildTarget/compile, such as buildTarget/compile, buildTarget/test
    * and buildTarget/run.
    *
    * If the computed set of diagnostic is empty, the server must push an empty array with reset set
    * to true, in order to clear previous diagnostics.
    *
    * The optional originId field in the definition of PublishDiagnosticsParams can be used by
    * clients to know which request originated the notification. This field will be defined if the
    * client defined it in the original request that triggered this notification.
    */
  object publishDiagnostics
      extends Endpoint[PublishDiagnosticsParams, Unit]("build/publishDiagnostics")

  /** The BSP server can inform the client on the execution state of any task in the build tool. The
    * execution of some tasks, such as compilation or tests, must always be reported by the server.
    *
    * The server may also send additional task notifications for actions not covered by the
    * protocol, such as resolution or packaging. BSP clients can then display this information to
    * their users at their discretion.
    *
    * When beginning a task, the server may send `build/taskStart`, intermediate updates may be sent
    * in `build/taskProgress`.
    *
    * If a `build/taskStart` notification has been sent, the server must send `build/taskFinish` on
    * completion of the same task.
    *
    * `build/taskStart`, `build/taskProgress` and `build/taskFinish` notifications for the same task
    * must use the same `taskId`.
    *
    * Tasks that are spawned by another task should reference the originating task's `taskId` in
    * their own `taskId`'s `parent` field. Tasks spawned directly by a request should reference the
    * request's `originId` parent.
    */
  object taskStart extends Endpoint[TaskStartParams, Unit]("build/taskStart")

  /** After a `taskStart` and before `taskFinish` for a `taskId`, the server may send any number of
    * progress notifications.
    */
  object taskProgress extends Endpoint[TaskProgressParams, Unit]("build/taskProgress")

  /** A `build/taskFinish` notification must always be sent after a `build/taskStart` with the same
    * `taskId` was sent.
    */
  object taskFinish extends Endpoint[TaskFinishParams, Unit]("build/taskFinish")

  /** Like the language server protocol, the initialize request is sent as the first request from
    * the client to the server. If the server receives a request or notification before the
    * initialize request it should act as follows:
    *
    * * For a request the response should be an error with code: -32002. The message can be picked
    * by the server. * Notifications should be dropped, except for the exit notification. This will
    * allow the exit of a server without an initialize request.
    *
    * Until the server has responded to the initialize request with an InitializeBuildResult, the
    * client must not send any additional requests or notifications to the server.
    */
  object initialize
      extends Endpoint[InitializeBuildParams, InitializeBuildResult]("build/initialize")

  /** Like the language server protocol, the initialized notification is sent from the client to the
    * server after the client received the result of the initialize request but before the client is
    * sending any other request or notification to the server. The server can use the initialized
    * notification for example to initialize intensive computation such as dependency resolution or
    * compilation. The initialized notification may only be sent once.
    */
  object initialized extends Endpoint[Unit, Unit]("build/initialized")

  /** Like the language server protocol, the shutdown build request is sent from the client to the
    * server. It asks the server to shut down, but to not exit (otherwise the response might not be
    * delivered correctly to the client). There is a separate exit notification that asks the server
    * to exit.
    */
  object shutdown extends Endpoint[Unit, Unit]("build/shutdown")

  /** Like the language server protocol, a notification to ask the server to exit its process. The
    * server should exit with success code 0 if the shutdown request has been received before;
    * otherwise with error code 1.
    */
  object exit extends Endpoint[Unit, Unit]("build/exit")
}
object BuildTarget extends BuildTarget
trait BuildTarget {

  /** The build target changed notification is sent from the server to the client to signal a change
    * in a build target. The server communicates during the initialize handshake whether this method
    * is supported or not.
    */
  object didChange extends Endpoint[DidChangeBuildTarget, Unit]("buildTarget/didChange")

  /** The build target sources request is sent from the client to the server to query for the list
    * of text documents and directories that are belong to a build target. The sources response must
    * not include sources that are external to the workspace, see `buildTarget/dependencySources`.
    */
  object sources extends Endpoint[SourcesParams, SourcesResult]("buildTarget/sources")

  /** The inverse sources request is sent from the client to the server to query for the list of
    * build targets containing a text document. The server communicates during the initialize
    * handshake whether this method is supported or not. This request can be viewed as the inverse
    * of `buildTarget/sources`, except it only works for text documents and not directories.
    */
  object inverseSources
      extends Endpoint[InverseSourcesParams, InverseSourcesResult]("buildTarget/inverseSources")

  /** The build target dependency sources request is sent from the client to the server to query for
    * the sources of build target dependencies that are external to the workspace. The dependency
    * sources response must not include source files that belong to a build target within the
    * workspace, see `buildTarget/sources`.
    *
    * The server communicates during the initialize handshake whether this method is supported or
    * not. This method can for example be used by a language server on `textDocument/definition` to
    * "Go to definition" from project sources to dependency sources.
    */
  object dependencySources
      extends Endpoint[DependencySourcesParams, DependencySourcesResult](
        "buildTarget/dependencySources"
      )

  /** The build target dependency modules request is sent from the client to the server to query for
    * the libraries of build target dependencies that are external to the workspace including meta
    * information about library and their sources. It's an extended version of
    * `buildTarget/sources`.
    */
  object dependencyModules
      extends Endpoint[DependencyModulesParams, DependencyModulesResult](
        "buildTarget/dependencyModules"
      )

  /** The build target resources request is sent from the client to the server to query for the list
    * of resources of a given list of build targets.
    *
    * A resource is a data dependency required to be present in the runtime classpath when a build
    * target is run or executed. The server communicates during the initialize handshake whether
    * this method is supported or not.
    *
    * This request can be used by a client to highlight the resources in a project view, for
    * example.
    */
  object resources extends Endpoint[ResourcesParams, ResourcesResult]("buildTarget/resources")

  /** The build target output paths request is sent from the client to the server to query for the
    * list of output paths of a given list of build targets.
    *
    * An output path is a file or directory that contains output files such as build artifacts which
    * IDEs may decide to exclude from indexing. The server communicates during the initialize
    * handshake whether this method is supported or not.
    */
  object outputPaths
      extends Endpoint[OutputPathsParams, OutputPathsResult]("buildTarget/outputPaths")

  /** The compile build target request is sent from the client to the server to compile the given
    * list of build targets. The server communicates during the initialize handshake whether this
    * method is supported or not. This method can for example be used by a language server before
    * `textDocument/rename` to ensure that all workspace sources typecheck correctly and are
    * up-to-date.
    */
  object compile extends Endpoint[CompileParams, CompileResult]("buildTarget/compile")

  /** The run request is sent from the client to the server to run a build target. The server
    * communicates during the initialize handshake whether this method is supported or not.
    *
    * This request may trigger a compilation on the selected build targets. The server is free to
    * send any number of `build/task*`, `build/publishDiagnostics` and `build/logMessage`
    * notifications during compilation before completing the response.
    *
    * The client will get a `originId` field in `RunResult` if the `originId` field in the
    * `RunParams` is defined.
    *
    * Note that an empty run request is valid. Run will be executed in the target as specified in
    * the build tool.
    */
  object run extends Endpoint[RunParams, RunResult]("buildTarget/run")

  /** The test build target request is sent from the client to the server to test the given list of
    * build targets. The server communicates during the initialize handshake whether this method is
    * supported or not.
    */
  object test extends Endpoint[TestParams, TestResult]("buildTarget/test")

  /** The clean cache request is sent from the client to the server to reset any state associated
    * with a given build target. The state can live either in the build tool or in the file system.
    *
    * The build tool defines the exact semantics of the clean cache request:
    *
    *   1. Stateless build tools are free to ignore the request and respond with a successful
    *      response. 2. Stateful build tools must ensure that invoking compilation on a target that
    *      has been cleaned results in a full compilation.
    */
  object cleanCache extends Endpoint[CleanCacheParams, CleanCacheResult]("buildTarget/cleanCache")

  /** The build target cpp options request is sent from the client to the server to query for the
    * list of compiler options necessary to compile in a given list of targets.
    */
  object cppOptions extends Endpoint[CppOptionsParams, CppOptionsResult]("buildTarget/cppOptions")

  /** The build target javac options request is sent from the client to the server to query for the
    * list of compiler options necessary to compile in a given list of targets.
    */
  object javacOptions
      extends Endpoint[JavacOptionsParams, JavacOptionsResult]("buildTarget/javacOptions")

  /** The JVM test environment request is sent from the client to the server in order to gather
    * information required to launch a Java process. This is useful when the client wants to control
    * the Java process execution, for example to enable custom Java agents or launch a custom main
    * class during unit testing or debugging
    *
    * The data provided by this endpoint may change between compilations, so it should not be cached
    * in any form. The client should ask for it right before test execution, after all the targets
    * are compiled.
    */
  object jvmTestEnvironment
      extends Endpoint[JvmTestEnvironmentParams, JvmTestEnvironmentResult](
        "buildTarget/jvmTestEnvironment"
      )

  /** Similar to `buildTarget/jvmTestEnvironment`, but returns environment that should be used for
    * regular exection of main classes, not for testing
    */
  object jvmRunEnvironment
      extends Endpoint[JvmRunEnvironmentParams, JvmRunEnvironmentResult](
        "buildTarget/jvmRunEnvironment"
      )

  /** The Python Options Request is sent from the client to the server to query for the list of the
    * interpreter flags used to run a given list of targets.
    */
  object pythonOptions
      extends Endpoint[PythonOptionsParams, PythonOptionsResult]("buildTarget/pythonOptions")

  /** **Unstable** (may change in future versions) The Rust workspace request is sent from the
    * client to the server to query for the information about project's workspace for the given list
    * of build targets.
    *
    * The request is essential to connect and work with `intellij-rust` plugin.
    *
    * The request may take a long time, as it may require building a project to some extent (for
    * example with `cargo check` command).
    */
  object rustWorkspace
      extends Endpoint[RustWorkspaceParams, RustWorkspaceResult]("buildTarget/rustWorkspace")

  /** The build target scalac options request is sent from the client to the server to query for the
    * list of compiler options necessary to compile in a given list of targets.
    */
  object scalacOptions
      extends Endpoint[ScalacOptionsParams, ScalacOptionsResult]("buildTarget/scalacOptions")

  /** The Scala build target test classes request is sent from the client to the server to query for
    * the list of fully qualified names of test classes in a given list of targets.
    *
    * This method can for example be used by a client to:
    *
    *   - Show a list of the discovered classes that can be tested.
    *   - Attach a "Run test suite" button above the definition of a test suite via
    *     `textDocument/codeLens`.
    *
    * (To render the code lens, the language server needs to map the fully qualified names of the
    * test targets to the defining source file via `textDocument/definition`. Then, once users click
    * on the button, the language server can pass the fully qualified name of the test class as an
    * argument to the `buildTarget/test` request.)
    *
    * This request may trigger a compilation on the selected build targets. The server is free to
    * send any number of `build/task*`, `build/publishDiagnostics` and `build/logMessage`
    * notifications during compilation before completing the response.
    *
    * The client will get a `originId` field in `ScalaTestClassesResult` if the `originId` field in
    * the `ScalaTestClassesParams` is defined.
    */
  @deprecated("Use buildTarget/jvmTestEnvironment instead")
  object scalaTestClasses
      extends Endpoint[ScalaTestClassesParams, ScalaTestClassesResult](
        "buildTarget/scalaTestClasses"
      )

  /** The build target main classes request is sent from the client to the server to query for the
    * list of main classes that can be fed as arguments to `buildTarget/run`. This method can be
    * used for the same use cases than the [Scala Test Classes Request](#scala-test-classes-request)
    * enables. This request may trigger a compilation on the selected build targets. The server is
    * free to send any number of `build/taskStart`, `build/taskProgress`, `build/taskFinish`,
    * `build/publishDiagnostics` and `build/logMessage` notifications during compilation before
    * completing the response. The client will get a `originId` field in `ScalaMainClassesResult` if
    * the `originId` field in the `ScalaMainClassesParams` is defined.
    */
  @deprecated("Use buildTarget/jvmRunEnvironment instead")
  object scalaMainClasses
      extends Endpoint[ScalaMainClassesParams, ScalaMainClassesResult](
        "buildTarget/scalaMainClasses"
      )
}
object Workspace extends Workspace
trait Workspace {

  /** The workspace build targets request is sent from the client to the server to ask for the list
    * of all available build targets in the workspace.
    */
  object buildTargets extends Endpoint[Unit, WorkspaceBuildTargetsResult]("workspace/buildTargets")

  /** The `reload` request is sent from the client to instruct the build server to reload the build
    * configuration. This request should be supported by build tools that keep their state in
    * memory. If the `reload` request returns with an error, it's expected that other requests
    * respond with the previously known "good" state.
    */
  object reload extends Endpoint[Unit, Unit]("workspace/reload")

  /** **Unstable** (may change in future versions) The cargo features state request is sent from the
    * client to the server to query for the current state of the Cargo features. Provides also
    * mapping between Cargo packages and build target identifiers.
    */
  object cargoFeaturesState
      extends Endpoint[Unit, CargoFeaturesStateResult]("workspace/cargoFeaturesState")

  /** **Unstable** (may change in future versions) The enable cargo features request is sent from
    * the client to the server to set provided features collection as a new state for the specified
    * Cargo package.
    */
  object setCargoFeatures
      extends Endpoint[SetCargoFeaturesParams, SetCargoFeaturesResult]("workspace/setCargoFeatures")
}
object DebugSession extends DebugSession
trait DebugSession {

  /** The debug request is sent from the client to the server to debug build target(s). The server
    * launches a [Microsoft DAP](https://microsoft.github.io/debug-adapter-protocol/) server and
    * returns a connection URI for the client to interact with.
    */
  object start extends Endpoint[DebugSessionParams, DebugSessionAddress]("debugSession/start")
}
object $ extends $
trait $ {

  /** Like the language server protocol, a notification to ask the server to cancel a request.
    */
  object cancelRequest extends Endpoint[CancelRequestParams, Unit]("$/cancelRequest")
}
