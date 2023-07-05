package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class SbtBuildTarget {
    @NonNull
    String sbtVersion
    @NonNull
    List<String> autoImports
    @NonNull
    ScalaBuildTarget scalaBuildTarget
    BuildTargetIdentifier parent
    @NonNull
    List<BuildTargetIdentifier> children

    new(@NonNull String sbtVersion, @NonNull List<String> autoImports, @NonNull ScalaBuildTarget scalaBuildTarget, @NonNull List<BuildTargetIdentifier> children){
        this.sbtVersion = sbtVersion
        this.autoImports = autoImports
        this.scalaBuildTarget = scalaBuildTarget
        this.children = children
    }
}
