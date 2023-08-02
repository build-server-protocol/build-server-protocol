package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface RustBuildServer {
    @JsonRequest("buildTarget/rustWorkspace")
    CompletableFuture<RustWorkspaceResult> rustWorkspace(RustWorkspaceParams params);

    @JsonRequest("buildTarget/rustToolchain")
    CompletableFuture<RustToolchainResult> rustToolchain(RustToolchainParams params);


}
