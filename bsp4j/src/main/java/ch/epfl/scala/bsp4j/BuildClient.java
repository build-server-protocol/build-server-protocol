package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface BuildClient {

    @JsonNotification("build/showMessage")
    void onShowMessage(ShowMessageParams params);

    @JsonNotification("build/logMessage")
    void onLogMessage(LogMessageParams params);

    @JsonNotification("build/publishDiagnostics")
    void onPublishDiagnostics(PublishDiagnosticsParams params);

    @JsonNotification("buildTarget/didChange")
    void onBuildTargetChanged(DidChangeBuildTarget params);

    @JsonRequest("build/registerFileWatcher")
    CompletableFuture<RegisterFileWatcherResult> registerFileWatcher(RegisterFileWatcherParams params);

    @JsonRequest("build/cancelFileWatcher")
    CompletableFuture<CancelFileWatcherResult> cancelFileWatcher(CancelFileWatcherParams params);

    @JsonNotification("buildTarget/compileReport")
    void onCompileReport(CompileReport params);

    default void onConnectWithServer(BuildServer server) {

    }

}
