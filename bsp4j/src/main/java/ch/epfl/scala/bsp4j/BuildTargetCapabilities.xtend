package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class BuildTargetCapabilities {
    @NonNull
    Boolean canCompile
    @NonNull
    Boolean canTest
    @NonNull
    Boolean canRun
    @NonNull
    Boolean canDebug

    new(@NonNull Boolean canCompile, @NonNull Boolean canTest, @NonNull Boolean canRun, @NonNull Boolean canDebug){
        this.canCompile = canCompile
        this.canTest = canTest
        this.canRun = canRun
        this.canDebug = canDebug
    }
}
