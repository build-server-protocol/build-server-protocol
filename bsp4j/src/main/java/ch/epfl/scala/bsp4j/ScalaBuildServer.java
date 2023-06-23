package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface ScalaBuildServer {

  @JsonRequest("buildTarget/scalacOptions")
  CompletableFuture<ScalacOptionsResult> scalacOptions(ScalacOptionsParams params);

  @JsonRequest("buildTarget/scalaTestClasses")
  CompletableFuture<ScalaTestClassesResult> scalaTestClasses(ScalaTestClassesParams params);

  @JsonRequest("buildTarget/scalaMainClasses")
  CompletableFuture<ScalaMainClassesResult> scalaMainClasses(ScalaMainClassesParams params);

}
