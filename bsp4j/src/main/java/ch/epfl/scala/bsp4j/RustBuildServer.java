package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface RustBuildServer {
    /**
     * **Unstable** (may change in future versions)
     * The Rust workspace request is sent from the client to the server to query for
     * the information about project's workspace for the given list of build targets.
     * 
     * The request is essential to connect and work with `intellij-rust` plugin.
     * 
     * The request may take a long time, as it may require building a project to some extent
     * (for example with `cargo check` command).
     */
    @JsonRequest("buildTarget/rustWorkspace")
    CompletableFuture<RustWorkspaceResult> rustWorkspace(RustWorkspaceParams params);


}
