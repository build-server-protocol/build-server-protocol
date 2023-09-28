package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface JvmBuildServer {
  @JsonRequest("buildTarget/jvmTestEnvironment")
  CompletableFuture<JvmTestEnvironmentResult> buildTargetJvmTestEnvironment(
      JvmTestEnvironmentParams params);

  @JsonRequest("buildTarget/jvmRunEnvironment")
  CompletableFuture<JvmRunEnvironmentResult> buildTargetJvmRunEnvironment(
      JvmRunEnvironmentParams params);
}
