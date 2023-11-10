package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface JvmBuildServer {
  /**
   * The JVM test environment request is sent from the client to the server in order to gather
   * information required to launch a Java process. This is useful when the client wants to control
   * the Java process execution, for example to enable custom Java agents or launch a custom main
   * class during unit testing or debugging
   *
   * <p>The data provided by this endpoint may change between compilations, so it should not be
   * cached in any form. The client should ask for it right before test execution, after all the
   * targets are compiled.
   */
  @JsonRequest("buildTarget/jvmTestEnvironment")
  CompletableFuture<JvmTestEnvironmentResult> buildTargetJvmTestEnvironment(
      JvmTestEnvironmentParams params);

  /**
   * Similar to `buildTarget/jvmTestEnvironment`, but returns environment that should be used for
   * regular exection of main classes, not for testing
   */
  @JsonRequest("buildTarget/jvmRunEnvironment")
  CompletableFuture<JvmRunEnvironmentResult> buildTargetJvmRunEnvironment(
      JvmRunEnvironmentParams params);
}
