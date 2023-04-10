$version: "2"

namespace bsp

structure MavenDependencyModuleArtifact {
    @required
    uri: String
    classifier: String
}

list MavenDependencyModuleArtifacts {
    member: MavenDependencyModuleArtifact
}

structure MavenDependencyModule {
    @required
    organization: String
    @required
    name: String
    @required
    version: String
    @required
    artifacts: MavenDependencyModuleArtifacts
    scope: String
}

