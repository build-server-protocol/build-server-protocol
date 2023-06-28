package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class DependencyModulesResult {
    @NonNull
    List<DependencyModulesItem> items

    new(@NonNull List<DependencyModulesItem> items) {
        this.items = items
    }
}
