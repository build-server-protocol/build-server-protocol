package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface ScalaBuildServer {
    @JsonRequest("buildTarget/scalacOptions")
    CompletableFuture<ScalacOptionsResult> buildTargetScalacOptions(ScalacOptionsParams params);

    @Deprecated
    @JsonRequest("buildTarget/scalaTestClasses")
    CompletableFuture<ScalaTestClassesResult> buildTargetScalaTestClasses(ScalaTestClassesParams params);

    @Deprecated
    @JsonRequest("buildTarget/scalaMainClasses")
    CompletableFuture<ScalaMainClassesResult> buildTargetScalaMainClasses(ScalaMainClassesParams params);


}
