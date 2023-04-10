$version: "2"

namespace bsp

list SbtAutoImports {
    member: String
}

structure SbtBuildTarget {
    @required
    sbtVersion: String
    @required
    autoImports: SbtAutoImports
    @required
    scalaBuildTarget: ScalaBuildTarget
    parent: BuildTargetIdentifier
    @required
    children: BuildTargetIdentifiers
}
