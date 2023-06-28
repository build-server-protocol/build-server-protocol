package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class JvmTestEnvironmentParams {
    @NonNull
    List<BuildTargetIdentifier> targets
    String originId

    new(@NonNull List<BuildTargetIdentifier> targets){
        this.targets = targets
    }
}
