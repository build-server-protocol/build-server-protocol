package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface RustBuildServer {
    @JsonRequest("buildTarget/rustOptions")
    CompletableFuture<RustOptionsResult> buildTargetRustOptions(RustOptionsParams params);

    @JsonRequest("buildTarget/rustMetadata")
    CompletableFuture<RustMetadataResult> rustMetadata(RustMetadataParams params);
}
