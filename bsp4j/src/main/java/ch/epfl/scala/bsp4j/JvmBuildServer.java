package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface JvmBuildServer {
    @JsonRequest("buildTarget/jvmRunEnvironment")
    CompletableFuture<JvmRunEnvironmentResult> jvmRunEnvironment(JvmRunEnvironmentParams params);

    @JsonRequest("buildTarget/jvmTestEnvironment")
    CompletableFuture<JvmTestEnvironmentResult> jvmTestEnvironment(JvmTestEnvironmentParams params);
}
