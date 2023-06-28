package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class PythonBuildTarget {
    String version
    String interpreter

    new() {
    }
}
