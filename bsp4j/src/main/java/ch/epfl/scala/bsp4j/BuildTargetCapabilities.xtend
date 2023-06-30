package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class BuildTargetCapabilities {
    Boolean canCompile
    Boolean canTest
    Boolean canRun
    Boolean canDebug

    new() {
    }
}
