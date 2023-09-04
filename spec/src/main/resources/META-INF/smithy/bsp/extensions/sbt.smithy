$version: "2"

namespace bsp.sbt

use bsp#BuildTargetData
use bsp#BuildTargetIdentifier
use bsp#BuildTargetIdentifiers
use bsp.scala#ScalaBuildTarget
use traits#dataKind

/// `SbtBuildTarget` is a basic data structure that contains sbt-specific metadata
/// for providing editor support for sbt build files.
///
/// For example, say we have a project in `/foo/bar` defining projects `A` and `B`
/// and two meta builds `M1` (defined in `/foo/bar/project`) and `M2` (defined in
/// `/foo/bar/project/project`).
///
/// The sbt build target for `M1` will have `A` and `B` as the defined targets and
/// `M2` as the parent. Similarly, the sbt build target for `M2` will have `M1` as
/// the defined target and no parent.
///
/// Clients can use this information to reconstruct the tree of sbt meta builds. The
/// `parent` information can be defined from `children` but it's provided by the
/// server to simplify the data processing on the client side.
@dataKind(kind: "sbt", extends: [BuildTargetData])
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

list SbtAutoImports {
    member: String
}
