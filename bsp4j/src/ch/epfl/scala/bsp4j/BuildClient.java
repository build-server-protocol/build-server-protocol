package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface BuildClient {
    /**
     * The show message notification is sent from a server to a client to ask the client to display a particular message in the user interface.
     * 
     * A build/showMessage notification is similar to LSP's window/showMessage, except for a few additions like id and originId.
     */
    @JsonNotification("build/showMessage")
    void onBuildShowMessage(ShowMessageParams params);

    /**
     * The log message notification is sent from a server to a client to ask the client to log a particular message in its console.
     * 
     * A build/logMessage notification is similar to LSP's window/logMessage, except for a few additions like id and originId.
     */
    @JsonNotification("build/logMessage")
    void onBuildLogMessage(LogMessageParams params);

    /**
     * The Diagnostics notification are sent from the server to the client to signal results of validation runs.
     * 
     * When reset is true, the client must clean all previous diagnostics associated with the same textDocument and
     * buildTarget and set instead the diagnostics in the request. This is the same behaviour as PublishDiagnosticsParams
     * in the LSP. When reset is false, the diagnostics are added to the last active diagnostics, allowing build tools to
     * stream diagnostics to the client.
     * 
     * It is the server's responsibility to manage the lifetime of the diagnostics by using the appropriate value in the reset field.
     * Clients generate new diagnostics by calling any BSP endpoint that triggers a buildTarget/compile, such as buildTarget/compile, buildTarget/test and buildTarget/run.
     * 
     * If the computed set of diagnostic is empty, the server must push an empty array with reset set to true, in order to clear previous diagnostics.
     * 
     * The optional originId field in the definition of PublishDiagnosticsParams can be used by clients to know which request originated the notification.
     * This field will be defined if the client defined it in the original request that triggered this notification.
     */
    @JsonNotification("build/publishDiagnostics")
    void onBuildPublishDiagnostics(PublishDiagnosticsParams params);

    /**
     * The build target changed notification is sent from the server to the client to
     * signal a change in a build target. The server communicates during the initialize
     * handshake whether this method is supported or not.
     */
    @JsonNotification("buildTarget/didChange")
    void onBuildTargetDidChange(DidChangeBuildTarget params);

    /**
     * The BSP server can inform the client on the execution state of any task in the
     * build tool. The execution of some tasks, such as compilation or tests, must
     * always be reported by the server.
     * 
     * The server may also send additional task notifications for actions not covered
     * by the protocol, such as resolution or packaging. BSP clients can then display
     * this information to their users at their discretion.
     * 
     * When beginning a task, the server may send `build/taskStart`, intermediate
     * updates may be sent in `build/taskProgress`.
     * 
     * If a `build/taskStart` notification has been sent, the server must send
     * `build/taskFinish` on completion of the same task.
     * 
     * `build/taskStart`, `build/taskProgress` and `build/taskFinish` notifications for
     * the same task must use the same `taskId`.
     * 
     * Tasks that are spawned by another task should reference the originating task's
     * `taskId` in their own `taskId`'s `parent` field. Tasks spawned directly by a
     * request should reference the request's `originId` parent.
     */
    @JsonNotification("build/taskStart")
    void onBuildTaskStart(TaskStartParams params);

    /**
     * After a `taskStart` and before `taskFinish` for a `taskId`, the server may send
     * any number of progress notifications.
     */
    @JsonNotification("build/taskProgress")
    void onBuildTaskProgress(TaskProgressParams params);

    /**
     * A `build/taskFinish` notification must always be sent after a `build/taskStart`
     * with the same `taskId` was sent.
     */
    @JsonNotification("build/taskFinish")
    void onBuildTaskFinish(TaskFinishParams params);

    /**
     * **Unstable** (may change in future versions)
     * Notification sent from the server to the client when the target being run or tested
     * prints something to stdout.
     */
    @JsonNotification("run/printStdout")
    void onRunPrintStdout(PrintParams params);

    /**
     * **Unstable** (may change in future versions)
     * Notification sent from the server to the client when the target being run or tested
     * prints something to stderr.
     */
    @JsonNotification("run/printStderr")
    void onRunPrintStderr(PrintParams params);


}
