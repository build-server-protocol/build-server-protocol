$version: "2"

namespace bsp.rust

use bsp#BuildTargetData
use bsp#BuildTargetIdentifiers
use jsonrpc#data
use jsonrpc#dataKind
use jsonrpc#jsonRPC
use jsonrpc#jsonRequest

// TODO: check correctness. Refer to original Rust extension implementation in .xtend/java/scala.

@jsonRPC
service RustBuildServer {
    operations: [
        RustWorkspace
        RustToolchain
    ]
}

@jsonRequest("buildTarget/rustWorkspace")
operation RustWorkspace {
    input: RustWorkspaceParams
    output: RustWorkspaceResult
}

structure RustWorkspaceParams {
    @required
    targets: BuildTargetIdentifiers
}

structure RustWorkspaceResult {
    @required
    packages: RustPackages
    @required
    rawDependencies: RustRawDependencies
    @required
    dependencies: RustDependencies
}

list RustPackages {
    member: RustPackage
}

structure RustPackage {
    @required
    id: String
    version: String
    origin: String
    edition: String
    source: String
    @required
    targets: RustTargets
    @required
    allTargets: RustTargets
    @required
    features: RustFeatures
    @required
    enabledFeatures: Strings
    cfgOptions: RustCfgOptions
    @required
    env: RustEnvDatas
    outDirUrl: String
    procMacroArtifact: RustProcMacroArtifact
}

list RustTargets {
    member: RustTarget
}

@dataKind(kind: "rust", extends: BuildTargetData)
structure RustTarget {
    @required
    name: String
    @required
    crateRootUrl: String
    @required
    packageRootUrl: String
    @required
    kind: String
    edition: String
    doctest: Boolean
    requiredFeatures: Strings
}

list RustFeatures {
    member: RustFeature
}

structure RustFeature {
    @required
    name: String
    deps: Strings
}

list Strings {
    member: String
}

structure RustCfgOptions {
    keyValueOptions: KeyValueOptions
    nameOptions: Strings
}

map KeyValueOptions {
    key: String
    value: Strings
}

map RustEnvDatas {
    key: String
    value: String
}

structure RustProcMacroArtifact {
    path: String
    // TODO: we don't need hash. It is calculated by IntelliJ-Rust
}

map RustRawDependencies {
    key: String // PackageId
    value: RustRawDependency
}

structure RustRawDependency {
    @required
    name: String
    rename: String
    kind: String
    target: String
    optional: Boolean
    uses_default_features: Boolean
    @required
    features: Strings
}

map RustDependencies {
    key: String // Source
    value: RustDependency
}

structure RustDependency {
    @required
    target: String
    name: String
    depKinds: DepKinds
}

list DepKinds {
    member: DepKind
}

structure DepKind {
    @required
    kind: String
    target: String
}

@jsonRequest("buildTarget/rustToolchain")
operation RustToolchain {
    input: RustToolchainParams
    output: RustToolchainResult
}

structure RustToolchainParams {
    @required
    targets: BuildTargetIdentifiers
}

structure RustToolchainResult {
    @required
    items: RustToolchainsItems
}

list RustToolchainsItems {
    member: RustToolchainsItem
}

structure RustToolchainsItem {
    rustStdLib: RustcInfo
    @required
    cargoBinPath: String
    @required
    procMacroSrvPath: String
}

structure RustcInfo {
    @required
    sysroot: String
    @required
    srcSysroot: String
    @required
    version: String
    @required
    host: String
}
