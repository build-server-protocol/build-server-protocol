package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface PythonBuildServer {
    @JsonRequest("buildTarget/pythonOptions")
    CompletableFuture<PythonOptionsResult> buildTargetPythonOptions(PythonOptionsParams params);


}
