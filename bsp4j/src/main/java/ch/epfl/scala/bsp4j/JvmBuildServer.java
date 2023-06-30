package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface JvmBuildServer {
    @JsonRequest("buildTarget/jvmTestEnvironment")
    CompletableFuture<JvmTestEnvironmentResult> buildTargetJvmTestEnvironment(JvmTestEnvironmentParams params);

    @JsonRequest("buildTarget/jvmRunEnvironment")
    CompletableFuture<JvmRunEnvironmentResult> buildTargetJvmRunEnvironment(JvmRunEnvironmentParams params);


}
