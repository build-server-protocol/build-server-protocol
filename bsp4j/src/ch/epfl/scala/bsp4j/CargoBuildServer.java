package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface CargoBuildServer {
  /**
   * **Unstable** (may change in future versions) The cargo features state request is sent from the
   * client to the server to query for the current state of the Cargo features. Provides also
   * mapping between Cargo packages and build target identifiers.
   */
  @JsonRequest("workspace/cargoFeaturesState")
  CompletableFuture<CargoFeaturesStateResult> cargoFeaturesState();

  /**
   * **Unstable** (may change in future versions) The enable cargo features request is sent from the
   * client to the server to set provided features collection as a new state for the specified Cargo
   * package.
   */
  @JsonRequest("workspace/setCargoFeatures")
  CompletableFuture<SetCargoFeaturesResult> setCargoFeatures(SetCargoFeaturesParams params);
}
