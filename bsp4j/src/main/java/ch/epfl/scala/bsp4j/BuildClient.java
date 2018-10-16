package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface BuildClient {

    @JsonNotification("build/showMessage")
    void showMessage(ShowMessageParams params);

    @JsonNotification("build/logMessage")
    void logMessage(LogMessageParams params);

    @JsonNotification("build/publishDiagnostics")
    void publishDiagnostics(PublishDiagnosticsParams params);

    @JsonNotification("buildTarget/didChange")
    void didChangeBuildTarget(DidChangeBuildTarget params);

    @JsonRequest("build/registerFileWatcher")
    CompletableFuture<RegisterFileWatcherResult> registerFileWatcher(RegisterFileWatcherParams params);

    @JsonRequest("build/cancelFileWatcher")
    CompletableFuture<CancelFileWatcherResult> cancelFileWatcher(CancelFileWatcherParams params);

    @JsonNotification("buildTarget/compileReport")
    void compileReport(CompileReport params);

    default void connect(BuildServer server) {

    }

}
