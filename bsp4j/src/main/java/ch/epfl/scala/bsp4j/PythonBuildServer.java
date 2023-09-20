package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface PythonBuildServer {
    /**
     * The Python Options Request is sent from the client to the server to
     * query for the list of the interpreter flags used to run a given list of
     * targets.
     */
    @JsonRequest("buildTarget/pythonOptions")
    CompletableFuture<PythonOptionsResult> buildTargetPythonOptions(PythonOptionsParams params);


}
