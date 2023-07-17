package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface JavaBuildServer {
    @JsonRequest("buildTarget/javacOptions")
    CompletableFuture<JavacOptionsResult> buildTargetJavacOptions(JavacOptionsParams params);


}
