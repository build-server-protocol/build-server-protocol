package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class BuildServerCapabilities {
    CompileProvider compileProvider
    TestProvider testProvider
    RunProvider runProvider
    DebugProvider debugProvider
    Boolean inverseSourcesProvider
    Boolean dependencySourcesProvider
    Boolean dependencyModulesProvider
    Boolean resourcesProvider
    Boolean outputPathsProvider
    Boolean buildTargetChangedProvider
    Boolean jvmRunEnvironmentProvider
    Boolean jvmTestEnvironmentProvider
    Boolean canReload

    new() {
    }
}
