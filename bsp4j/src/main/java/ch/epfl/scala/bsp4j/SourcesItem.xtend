package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class SourcesItem {
    @NonNull
    BuildTargetIdentifier target
    @NonNull
    List<SourceItem> sources
    List<String> roots

    new(@NonNull BuildTargetIdentifier target, @NonNull List<SourceItem> sources){
        this.target = target
        this.sources = sources
    }
}
