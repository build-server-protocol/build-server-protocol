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
    enabledFeatures: RustPackageEnabledFeatures
    cfgOptions: RustCfgOptions
    @required
    env: RustEnvironmentVariables
    outDirUrl: String
    procMacroArtifact: RustProcMacroArtifact
}

list RustPackageEnabledFeatures {
    feature: String
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
    kind: RustTargetKind
    edition: String
    doctest: Boolean
    requiredFeatures: RustPackageRequiredFeatures
}

enum RustTargetKind {
    BIN
    TEST
    EXAMPLE
    BENCH
    CUSTOMBUILD
    UNKNOWN
}

list RustPackageRequiredFeatures {
    feature: String
}

list RustFeatures {
    member: RustFeature
}

structure RustFeature {
    @required
    name: String
    deps: RustFeatureDependencies
}

list RustFeatureDependencies {
    dependence: String
}

structure RustCfgOptions {
    keyValueOptions: KeyValueOptions
    nameOptions: RustCfgOptionsNames
}

list RustCfgOptionsNames {
    name: String
}

map KeyValueOptions {
    key: String
    value: Values
}

list Values {
    value: String
}

map RustEnvironmentVariables {
    key: String
    value: String
}

structure RustProcMacroArtifact {
    path: URI
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
    usesDefaultFeatures: Boolean
    @required
    features: RustRawDependencyFeatures
}

list RustRawDependencyFeatures {
    feature: String
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
    kind: DepKindEnum
    target: String
}

enum DepKindEnum {
    UNCLASSIFIED
    STDLIB
    NORMAL
    DEV
    BUILD
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
    cargoBinPath: URI
    @required
    procMacroSrvPath: URI
}

structure RustcInfo {
    @required
    sysrootPath: URI
    @required
    srcSysrootPath: URI
    @required
    version: String
    @required
    host: String
}
