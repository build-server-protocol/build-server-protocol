package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface ScalaBuildServer {

    @JsonRequest("buildTarget/scalacOptions")
    CompletableFuture<ScalacOptionsResult> buildTargetScalacOptions(ScalacOptionsParams params);

    @JsonRequest("buildTarget/scalaTestClasses")
    CompletableFuture<ScalaTestClassesResult> buildTargetScalaTestClasses(ScalaTestClassesParams params);

    @JsonRequest("buildTarget/scalaMainClasses")
    CompletableFuture<ScalaMainClassesResult> buildTargetScalaMainClasses(ScalaMainClassesParams params);

    @JsonRequest("buildTarget/executionEnvironment")
    CompletableFuture<ExecutionEnvironmentResult> executionEnvironment(ExecutionEnvironmentParams params);
}
