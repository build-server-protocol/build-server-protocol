package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface BuildServer {
  @JsonRequest("build/initialize")
  CompletableFuture<InitializeBuildResult> buildInitialize(InitializeBuildParams params);

  @JsonNotification("build/initialized")
  void onBuildInitialized();

  @JsonRequest("build/shutdown")
  CompletableFuture<Object> buildShutdown();

  @JsonNotification("build/exit")
  void onBuildExit();

  @JsonRequest("workspace/buildTargets")
  CompletableFuture<WorkspaceBuildTargetsResult> workspaceBuildTargets();

  @JsonRequest("workspace/reload")
  CompletableFuture<Object> workspaceReload();

  @JsonRequest("buildTarget/sources")
  CompletableFuture<SourcesResult> buildTargetSources(SourcesParams params);

  @JsonRequest("buildTarget/inverseSources")
  CompletableFuture<InverseSourcesResult> buildTargetInverseSources(InverseSourcesParams params);

  @JsonRequest("buildTarget/dependencySources")
  CompletableFuture<DependencySourcesResult> buildTargetDependencySources(
      DependencySourcesParams params);

  @JsonRequest("buildTarget/dependencyModules")
  CompletableFuture<DependencyModulesResult> buildTargetDependencyModules(
      DependencyModulesParams params);

  @JsonRequest("buildTarget/resources")
  CompletableFuture<ResourcesResult> buildTargetResources(ResourcesParams params);

  @JsonRequest("buildTarget/outputPaths")
  CompletableFuture<OutputPathsResult> buildTargetOutputPaths(OutputPathsParams params);

  @JsonRequest("buildTarget/compile")
  CompletableFuture<CompileResult> buildTargetCompile(CompileParams params);

  @JsonRequest("buildTarget/run")
  CompletableFuture<RunResult> buildTargetRun(RunParams params);

  @JsonRequest("buildTarget/test")
  CompletableFuture<TestResult> buildTargetTest(TestParams params);

  @JsonRequest("debugSession/start")
  CompletableFuture<DebugSessionAddress> debugSessionStart(DebugSessionParams params);

  @JsonRequest("buildTarget/cleanCache")
  CompletableFuture<CleanCacheResult> buildTargetCleanCache(CleanCacheParams params);

  @JsonNotification("run/readStdin")
  void onRunReadStdin(ReadParams params);
}
