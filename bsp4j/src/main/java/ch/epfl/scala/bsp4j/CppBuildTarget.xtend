package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class CppBuildTarget {
    String version
    String compiler
    String cCompiler
    String cppCompiler

    new(){
    }
}
