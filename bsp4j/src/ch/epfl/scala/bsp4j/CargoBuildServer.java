package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface CargoBuildServer {
    @JsonRequest("workspace/cargoFeaturesState")
    CompletableFuture<CargoFeaturesStateResult> cargoFeaturesState();

    @JsonRequest("workspace/setCargoFeatures")
    CompletableFuture<SetCargoFeaturesResult> setCargoFeatures(SetCargoFeaturesParams params);


}
