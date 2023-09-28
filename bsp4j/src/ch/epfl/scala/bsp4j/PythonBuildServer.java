package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface PythonBuildServer {
  @JsonRequest("buildTarget/pythonOptions")
  CompletableFuture<PythonOptionsResult> buildTargetPythonOptions(PythonOptionsParams params);
}
