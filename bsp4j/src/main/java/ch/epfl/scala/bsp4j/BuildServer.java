package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface BuildServer {

    @JsonRequest("build/initialize")
    CompletableFuture<InitializeBuildResult> initialize(InitializeBuildParams params);

    @JsonNotification("build/initialized")
    void onInitialized();

    @JsonRequest("build/shutdown")
    CompletableFuture<Object> shutdown();

    @JsonNotification("build/exit")
    void onExit();

    @JsonRequest("workspace/buildTargets")
    CompletableFuture<WorkspaceBuildTargetsResult> listWorkspaceBuildTargets();

    @JsonNotification("build/didChangeWatchedFiles")
    void onWatchedFileChanged(DidChangeWatchedFiles params);

    @JsonRequest("buildTarget/textDocuments")
    CompletableFuture<BuildTargetTextDocumentsResult> listBuildTargetTextDocuments(BuildTargetTextDocumentsParams params);

    @JsonRequest("textDocuments/buildTargets")
    CompletableFuture<TextDocumentBuildTargetsResult> listTextDocumentBuildTargets(TextDocumentBuildTargetsParams params);

    @JsonRequest("buildTarget/dependencySources")
    CompletableFuture<DependencySourcesResult> listBuildTargetDependencySources(DependencySourcesParams params);

    @JsonRequest("buildTarget/resources")
    CompletableFuture<ResourcesResult> listBuildTargetResources(ResourcesParams params);

    @JsonRequest("buildTarget/compile")
    CompletableFuture<CompileResult> compileBuildTarget(CompileParams params);

    @JsonRequest("buildTarget/test")
    CompletableFuture<TestResult> testBuildTarget(TestParams params);

    @JsonRequest("buildTarget/run")
    CompletableFuture<RunResult> runBuildTarget(RunParams params);

    default void onConnectWithClient(BuildClient server) {

    }
}

