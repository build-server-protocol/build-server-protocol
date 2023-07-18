$version: "2"

namespace bsp.maven

use bsp#DependencyModuleData
use bsp#URI
use jsonrpc#dataKind

/// `MavenDependencyModule` is a basic data structure that contains maven-like
/// metadata. This metadata is embedded in the `data: Option[Json]` field of the `DependencyModule` definition, when the `dataKind` field contains "maven".
@dataKind(kind: "maven", extends: [DependencyModuleData])
structure MavenDependencyModule {
    @required
    organization: String
    @required
    name: String
    @required
    version: String
    /// List of module's artifacts with different classifiers.
    /// For example: [
    ///   {uri = "../scala-library-2.13.5.jar"},
    ///   {uri = "../scala-library-2.13.5-sources.jar", classifier = "sources"}
    /// ]
    @required
    artifacts: MavenDependencyModuleArtifacts
    scope: String
}

structure MavenDependencyModuleArtifact {
    /// Path to jar
    @required
    uri: URI
    /// Empty or `sources`|`docs`
    classifier: String
}

list MavenDependencyModuleArtifacts {
    member: MavenDependencyModuleArtifact
}
