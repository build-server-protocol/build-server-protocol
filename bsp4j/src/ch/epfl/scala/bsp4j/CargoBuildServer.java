package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface CargoBuildServer {
  @JsonRequest("workspace/cargoFeaturesState")
  CompletableFuture<CargoFeaturesStateResult> cargoFeaturesState();

  @JsonRequest("workspace/setCargoFeatures")
  CompletableFuture<SetCargoFeaturesResult> setCargoFeatures(SetCargoFeaturesParams params);
}
