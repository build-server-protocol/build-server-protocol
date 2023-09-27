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

    @JsonNotification("build/taskStart")
    void onBuildTaskStart(TaskStartParams params);

    @JsonNotification("build/taskProgress")
    void onBuildTaskProgress(TaskProgressParams params);

    @JsonNotification("build/taskFinish")
    void onBuildTaskFinish(TaskFinishParams params);

    @JsonNotification("run/printStdout")
    void onRunPrintStdout(PrintParams params);

    @JsonNotification("run/printStderr")
    void onRunPrintStderr(PrintParams params);


}
