package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class CompileReport {
    @NonNull
    BuildTargetIdentifier target
    String originId
    @NonNull
    Integer errors
    @NonNull
    Integer warnings
    Long time
    Boolean noOp

    new(@NonNull BuildTargetIdentifier target, @NonNull Integer errors, @NonNull Integer warnings) {
        this.target = target
        this.errors = errors
        this.warnings = warnings
    }
}
