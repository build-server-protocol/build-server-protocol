package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface BuildClient {

    @JsonNotification("build/showMessage")
    void onBuildShowMessage(ShowMessageParams params);

    @JsonNotification("build/logMessage")
    void onBuildLogMessage(LogMessageParams params);

    @JsonNotification("build/publishDiagnostics")
    void onBuildPublishDiagnostics(PublishDiagnosticsParams params);

    @JsonNotification("buildTarget/didChange")
    void onBuildTargetDidChange(DidChangeBuildTarget params);

    @JsonRequest("build/registerFileWatcher")
    CompletableFuture<RegisterFileWatcherResult> buildRegisterFileWatcher(RegisterFileWatcherParams params);

    @JsonRequest("build/cancelFileWatcher")
    CompletableFuture<CancelFileWatcherResult> buildCancelFileWatcher(CancelFileWatcherParams params);

    @JsonNotification("buildTarget/compileReport")
    void onBuildTargetCompileReport(CompileReport params);

    @JsonNotification("buildTarget/testReport")
    void onBuildTargetTest(TestReport params);

    default void onConnectWithServer(BuildServer server) {

    }

}
