package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class WorkspaceBuildTargetsResult {
    @NonNull
    List<BuildTarget> targets

    new(@NonNull List<BuildTarget> targets){
        this.targets = targets
    }
}
