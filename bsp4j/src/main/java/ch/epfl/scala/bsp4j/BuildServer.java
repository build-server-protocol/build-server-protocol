package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

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
    CompletableFuture<DependencySourcesResult> buildTargetDependencySources(DependencySourcesParams params);

    @JsonRequest("buildTarget/resources")
    CompletableFuture<ResourcesResult> buildTargetResources(ResourcesParams params);

    @JsonRequest("buildTarget/compile")
    CompletableFuture<CompileResult> buildTargetCompile(CompileParams params);

    @JsonRequest("buildTarget/test")
    CompletableFuture<TestResult> buildTargetTest(TestParams params);

    @JsonRequest("buildTarget/run")
    CompletableFuture<RunResult> buildTargetRun(RunParams params);

    @JsonRequest("buildTarget/cleanCache")
    CompletableFuture<CleanCacheResult> buildTargetCleanCache(CleanCacheParams params);

    default void onConnectWithClient(BuildClient server) {

    }
}
