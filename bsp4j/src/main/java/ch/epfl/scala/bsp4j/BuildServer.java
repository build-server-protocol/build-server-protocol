package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface BuildServer {

    @JsonRequest("build/initialize")
    CompletableFuture<InitializeBuildResult> initialize(InitializeBuildParams params);

    @JsonNotification("build/initialized")
    void initialized();

    @JsonRequest("build/shutdown")
    CompletableFuture<Object> shutdown();

    @JsonNotification("build/exit")
    void exit();

    @JsonRequest("workspace/buildTargets")
    CompletableFuture<WorkspaceBuildTargetsResult> workspaceBuildTargets();

    @JsonNotification("build/didChangeWatchedFiles")
    void didChangeWatchedFiles(DidChangeWatchedFiles params);

    @JsonRequest("buildTarget/textDocuments")
    CompletableFuture<BuildTargetTextDocumentsResult> buildTargetTextDocuments(BuildTargetTextDocumentsParams params);

    @JsonRequest("textDocuments/buildTargets")
    CompletableFuture<TextDocumentBuildTargetsResult> textDocumentBuildTargets(TextDocumentBuildTargetsParams params);

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

    default void connect(BuildClient server) {

    }
}

