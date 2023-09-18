package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface CppBuildServer {
    /**
     * The build target cpp options request is sent from the client to the server to
     * query for the list of compiler options necessary to compile in a given list of
     * targets.
     */
    @JsonRequest("buildTarget/cppOptions")
    CompletableFuture<CppOptionsResult> buildTargetCppOptions(CppOptionsParams params);


}
