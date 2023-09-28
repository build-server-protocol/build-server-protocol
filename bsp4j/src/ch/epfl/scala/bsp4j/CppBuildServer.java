package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface CppBuildServer {
  @JsonRequest("buildTarget/cppOptions")
  CompletableFuture<CppOptionsResult> buildTargetCppOptions(CppOptionsParams params);
}
