package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface CppBuildServer {
    @JsonRequest("buildTarget/cppOptions")
    CompletableFuture<CppOptionsResult> buildTargetCppOptions(CppOptionsParams params);
}
