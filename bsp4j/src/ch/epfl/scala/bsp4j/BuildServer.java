package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface BuildServer {
  /**
   * Like the language server protocol, the initialize request is sent as the first request from the
   * client to the server. If the server receives a request or notification before the initialize
   * request it should act as follows:
   *
   * <p>* For a request the response should be an error with code: -32002. The message can be picked
   * by the server. * Notifications should be dropped, except for the exit notification. This will
   * allow the exit of a server without an initialize request.
   *
   * <p>Until the server has responded to the initialize request with an InitializeBuildResult, the
   * client must not send any additional requests or notifications to the server.
   */
  @JsonRequest("build/initialize")
  CompletableFuture<InitializeBuildResult> buildInitialize(InitializeBuildParams params);

  /**
   * Like the language server protocol, the initialized notification is sent from the client to the
   * server after the client received the result of the initialize request but before the client is
   * sending any other request or notification to the server. The server can use the initialized
   * notification for example to initialize intensive computation such as dependency resolution or
   * compilation. The initialized notification may only be sent once.
   */
  @JsonNotification("build/initialized")
  void onBuildInitialized();

  /**
   * Like the language server protocol, the shutdown build request is sent from the client to the
   * server. It asks the server to shut down, but to not exit (otherwise the response might not be
   * delivered correctly to the client). There is a separate exit notification that asks the server
   * to exit.
   */
  @JsonRequest("build/shutdown")
  CompletableFuture<Object> buildShutdown();

  /**
   * Like the language server protocol, a notification to ask the server to exit its process. The
   * server should exit with success code 0 if the shutdown request has been received before;
   * otherwise with error code 1.
   */
  @JsonNotification("build/exit")
  void onBuildExit();

  /**
   * The workspace build targets request is sent from the client to the server to ask for the list
   * of all available build targets in the workspace.
   */
  @JsonRequest("workspace/buildTargets")
  CompletableFuture<WorkspaceBuildTargetsResult> workspaceBuildTargets();

  /**
   * The `reload` request is sent from the client to instruct the build server to reload the build
   * configuration. This request should be supported by build tools that keep their state in memory.
   * If the `reload` request returns with an error, it's expected that other requests respond with
   * the previously known "good" state.
   */
  @JsonRequest("workspace/reload")
  CompletableFuture<Object> workspaceReload();

  /**
   * The build target sources request is sent from the client to the server to query for the list of
   * text documents and directories that are belong to a build target. The sources response must not
   * include sources that are external to the workspace, see `buildTarget/dependencySources`.
   */
  @JsonRequest("buildTarget/sources")
  CompletableFuture<SourcesResult> buildTargetSources(SourcesParams params);

  /**
   * The inverse sources request is sent from the client to the server to query for the list of
   * build targets containing a text document. The server communicates during the initialize
   * handshake whether this method is supported or not. This request can be viewed as the inverse of
   * `buildTarget/sources`, except it only works for text documents and not directories.
   */
  @JsonRequest("buildTarget/inverseSources")
  CompletableFuture<InverseSourcesResult> buildTargetInverseSources(InverseSourcesParams params);

  /**
   * The wrapped sources request is sent from the client to the server to queryfor the list of build
   * targets containing wrapped sources. Wrapped sources are script sources that are wrapped by the
   * build tool with some top and bottom wrappers.
   * The server communicates during the initialize handshake whether this method is supported or not.
   */
  @JsonRequest("buildTarget/wrappedSources")
  CompletableFuture<WrappedSourcesResult> buildTargetWrappedSources(WrappedSourcesParams params);

  /**
   * The build target dependency sources request is sent from the client to the server to query for
   * the sources of build target dependencies that are external to the workspace. The dependency
   * sources response must not include source files that belong to a build target within the
   * workspace, see `buildTarget/sources`.
   *
   * <p>The server communicates during the initialize handshake whether this method is supported or
   * not. This method can for example be used by a language server on `textDocument/definition` to
   * "Go to definition" from project sources to dependency sources.
   */
  @JsonRequest("buildTarget/dependencySources")
  CompletableFuture<DependencySourcesResult> buildTargetDependencySources(
      DependencySourcesParams params);

  /**
   * The build target dependency modules request is sent from the client to the server to query for
   * the libraries of build target dependencies that are external to the workspace including meta
   * information about library and their sources. It's an extended version of `buildTarget/sources`.
   */
  @JsonRequest("buildTarget/dependencyModules")
  CompletableFuture<DependencyModulesResult> buildTargetDependencyModules(
      DependencyModulesParams params);

  /**
   * The build target resources request is sent from the client to the server to query for the list
   * of resources of a given list of build targets.
   *
   * <p>A resource is a data dependency required to be present in the runtime classpath when a build
   * target is run or executed. The server communicates during the initialize handshake whether this
   * method is supported or not.
   *
   * <p>This request can be used by a client to highlight the resources in a project view, for
   * example.
   */
  @JsonRequest("buildTarget/resources")
  CompletableFuture<ResourcesResult> buildTargetResources(ResourcesParams params);

  /**
   * The build target output paths request is sent from the client to the server to query for the
   * list of output paths of a given list of build targets.
   *
   * <p>An output path is a file or directory that contains output files such as build artifacts
   * which IDEs may decide to exclude from indexing. The server communicates during the initialize
   * handshake whether this method is supported or not.
   */
  @JsonRequest("buildTarget/outputPaths")
  CompletableFuture<OutputPathsResult> buildTargetOutputPaths(OutputPathsParams params);

  /**
   * The compile build target request is sent from the client to the server to compile the given
   * list of build targets. The server communicates during the initialize handshake whether this
   * method is supported or not. This method can for example be used by a language server before
   * `textDocument/rename` to ensure that all workspace sources typecheck correctly and are
   * up-to-date.
   */
  @JsonRequest("buildTarget/compile")
  CompletableFuture<CompileResult> buildTargetCompile(CompileParams params);

  /**
   * The run request is sent from the client to the server to run a build target. The server
   * communicates during the initialize handshake whether this method is supported or not.
   *
   * <p>Note that a run request containing only the target id is valid. If no further parameters are
   * provided, the server should use the default ones.
   *
   * <p>Implementation notes:
   *
   * <p>This request may trigger a compilation on the selected build targets. The server is free to
   * send any number of `build/task*`, `build/publishDiagnostics` and `build/logMessage`
   * notifications during compilation before completing the response.
   *
   * <p>The client will get a `originId` field in `RunResult` if and only if the `originId` field in
   * the `RunParams` is defined.
   *
   * <p>Cancelling this request must kill the running process.
   *
   * <p>If the BSP server wishes to forward the stdout and stderr streams of the running process to
   * the client, it can do so by sending `run/printStdout` and `run/printStderr` notifications.
   *
   * <p>If the client wishes to send input to the running process, it can do so by sending
   * `run/readStdin` notifications to the server.
   */
  @JsonRequest("buildTarget/run")
  CompletableFuture<RunResult> buildTargetRun(RunParams params);

  /**
   * The test build target request is sent from the client to the server to test the given list of
   * build targets. The server communicates during the initialize handshake whether this method is
   * supported or not.
   *
   * <p>The "Implementation notes" section of the `buildTarget/run` request applies to this request
   * as well.
   */
  @JsonRequest("buildTarget/test")
  CompletableFuture<TestResult> buildTargetTest(TestParams params);

  /**
   * The debug request is sent from the client to the server to debug build target(s). The server
   * launches a [Microsoft DAP](https://microsoft.github.io/debug-adapter-protocol/) server and
   * returns a connection URI for the client to interact with.
   */
  @JsonRequest("debugSession/start")
  CompletableFuture<DebugSessionAddress> debugSessionStart(DebugSessionParams params);

  /**
   * The clean cache request is sent from the client to the server to reset any state associated
   * with a given build target. The state can live either in the build tool or in the file system.
   *
   * <p>The build tool defines the exact semantics of the clean cache request:
   *
   * <p>1. Stateless build tools are free to ignore the request and respond with a successful
   * response. 2. Stateful build tools must ensure that invoking compilation on a target that has
   * been cleaned results in a full compilation.
   */
  @JsonRequest("buildTarget/cleanCache")
  CompletableFuture<CleanCacheResult> buildTargetCleanCache(CleanCacheParams params);

  /**
   * *Unstable** (may change in future versions) Notification sent from the client to the server
   * when the user wants to send input to the stdin of the running target.
   */
  @JsonNotification("run/readStdin")
  void onRunReadStdin(ReadParams params);
}
