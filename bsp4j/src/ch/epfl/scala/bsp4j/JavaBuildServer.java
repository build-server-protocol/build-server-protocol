package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface JavaBuildServer {
  /**
   * The build target javac options request is sent from the client to the server to query for the
   * list of compiler options necessary to compile in a given list of targets.
   */
  @JsonRequest("buildTarget/javacOptions")
  CompletableFuture<JavacOptionsResult> buildTargetJavacOptions(JavacOptionsParams params);
}
