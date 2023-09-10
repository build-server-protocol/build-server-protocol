$version: "2"

namespace bsp.rust

use bsp#BuildTargetData
use bsp#BuildTargetIdentifiers
use bsp#URI
use traits#enumKind
use traits#dataKind
use traits#jsonRPC
use traits#jsonRequest
use traits#set

@jsonRPC
service RustBuildServer {
    operations: [
        RustWorkspace
        RustToolchain
    ]
}

/// The Rust workspace request is sent from the client to the server to query for
/// the information about project's workspace for the given list of build targets.
///
/// The request is essential to connect and work with `intellij-rust` plugin.
///
/// The request may take a long time, as it may require building a project to some extent
/// (for example with `cargo check` command).
@jsonRequest("buildTarget/rustWorkspace")
operation RustWorkspace {
    input: RustWorkspaceParams
    output: RustWorkspaceResult
}

structure RustWorkspaceParams {
    /// A sequence of build targets for workspace resolution.
    @required
    targets: BuildTargetIdentifiers
}

structure RustWorkspaceResult {
    /// Packages of given targets.
    @required
    packages: RustPackages
    /// Dependencies as listed in the package `Cargo.toml`,
    /// without package resolution or any additional data.
    @required
    rawDependencies: RustRawDependencies
    /// Resolved dependencies of the package. Handles renamed dependencies.
    @required
    dependencies: RustDependencies
    /// A sequence of build targets taken into consideration during build process.
    @required
    resolvedTargets: BuildTargetIdentifiers
}

list RustPackages {
    member: RustPackage
}

/// A `crate` is the smallest amount of code that the Rust compiler considers at a time.
/// It can come in one of two forms: a binary crate or a library crate.
/// `Binary crates` are programs you can compile to an executable that you can run,
/// such as a command-line program or a server.
/// Each must have a function called main that defines what happens when the executable runs.
/// `Library crates` don’t have a main function, and they don’t compile to an executable.
/// Instead, they define functionality intended to be shared with multiple projects.
///
/// A `package` is a bundle of one or more crates that provides a set of functionality.
/// It contains a Cargo.toml file that describes how to build those crates.
/// A package can contain many binary crates, but at most only one library crate.
/// However, it must contain at least one crate, whether that’s a library or binary crate.
structure RustPackage {
    /// The package’s unique identifier
    @required
    id: String
    /// The package's root path.
    @required
    rootUrl: URI
    /// The name of the package.
    @required
    name: String
    /// The version of the package.
    @required
    version: String
    /// Defines a reason a package is in a project.
    @required
    origin: RustPackageOrigin
    /// Code edition of the package.
    @required
    edition: RustEdition
    /// The source ID of the dependency, `null` for the root package and path dependencies.
    source: String
    /// Corresponds to source files which can be compiled into a crate from this package.
    /// Contains only resolved targets without conflicts.
    @required
    resolvedTargets: RustTargets
    /// Same as `targets`, but contains all targets from this package.
    /// `targets` should be the subset of `allTargets`.
    @required
    allTargets: RustTargets
    /// Set of features defined for the package.
    /// Each feature maps to an array of features or dependencies it enables.
    /// The entry named "default" defines which features are enabled by default.
    @required
    features: RustFeatures
    /// Array of features enabled on this package.
    @required
    enabledFeatures: RustPackageEnabledFeatures
    /// Conditional compilation flags that can be set based on certain conditions.
    /// They can be used to enable or disable certain sections of code during the build process.
    /// `cfgs` in Rust can take one of two forms: "cfg1" or "cfg2=\"string\"".
    /// The `cfg` is split by '=' delimiter and the first half becomes key and
    /// the second is aggregated to the value in `RustCfgOptions`.
    /// For "cfg1" the value is empty.
    cfgOptions: RustCfgOptions
    /// Environment variables for the package.
    env: RustEnvironmentVariables
    /// An absolute path which is used as a value of `OUT_DIR` environmental
    /// variable when compiling current package.
    outDirUrl: URI
    /// File path to compiled output of a procedural macro crate.
    /// Procedural macros are macros that generate code at compile time.
    /// Contains files with file extensions: `.dll`, `.so` or `.dylib`.
    procMacroArtifact: String
}

list RustPackageEnabledFeatures {
    member: String
}

list RustTargets {
    member: RustBuildTarget
}

/// This structure is embedded in the `data?: BuildTargetData` field, when the
/// `dataKind` field contains "rust".
@dataKind(kind: "rust", extends: [BuildTargetData])
structure RustBuildTarget {
    /// The name of the target.
    @required
    name: String
    /// Path to the root module of the crate.
    @required
    crateRootUrl: URI
    /// A target's kind.
    @required
    kind: RustTargetKind
    /// Type of output that is produced by a crate during the build process.
    /// The crate type determines how the source code is compiled.
    crateTypes: RustCrateTypes
    /// The Rust edition of the target.
    @required
    edition: RustEdition
    /// Whether or not this target has doc tests enabled, and
    /// the target is compatible with doc testing.
    @required
    doctest: Boolean
    /// A sequence of required features.
    requiredFeatures: RustPackageRequiredFeatures
}

list RustCrateTypes {
    member: RustCrateType
}

/// Crate types (`lib`, `rlib`, `dylib`, `cdylib`, `staticlib`) are listed for
/// `lib` and `example` target kinds. For other target kinds `bin` crate type is listed.
@enumKind("closed")
intEnum RustCrateType {
    BIN = 1
    LIB = 2
    RLIB = 3
    DYLIB = 4
    CDYLIB = 5
    STATICLIB = 6
    PROC_MACRO = 7
    UNKNOWN = 8
}

@enumKind("closed")
intEnum RustTargetKind {
    /// For lib targets.
    LIB = 1
    /// For binaries.
    BIN = 2
    /// For integration tests.
    TEST = 3
    /// For examples.
    EXAMPLE = 4
    /// For benchmarks.
    BENCH = 5
    /// For build scripts.
    CUSTOMBUILD = 6
    /// For unknown targets.
    UNKNOWN = 7
}

list RustPackageRequiredFeatures {
    member: String
}

@set
list RustFeatures {
    member: RustFeature
}

// TODO to be deleted once "Add cargo extension" PR is merged that defines this enum
string Feature

structure RustFeature {
    /// Name of the feature.
    @required
    name: Feature
    /// Feature's dependencies.
    @required
    dependencies: RustFeatureDependencies
}

list RustFeatureDependencies {
    member: Feature
}

map RustCfgOptions {
    key: String
    value: RustCfgValues
}

list RustCfgValues {
    member: String
}

map RustEnvironmentVariables {
    key: String
    value: String
}

map RustRawDependencies {
    /// Package id
    key: String
    value: RustRawDependenciesInfo
}

list RustRawDependenciesInfo {
    member: RustRawDependency
}

structure RustRawDependency {
    /// The name of the dependency.
    @required
    name: String
    /// Name to which this dependency is renamed when declared in Cargo.toml.
    rename: String
    /// The dependency kind.
    kind: RustDepKind
    /// The target platform for the dependency.
    target: String
    /// Indicates whether this is an optional dependency.
    @required
    optional: Boolean
    /// Indicates whether default features are enabled.
    @required
    usesDefaultFeatures: Boolean
    /// A sequence of enabled features.
    @required
    features: RustRawDependencyFeatures
}

list RustRawDependencyFeatures {
    member: String
}

map RustDependencies {
    /// Package id
    key: String
    value: RustDependenciesInfo
}

list RustDependenciesInfo {
    member: RustDependency
}

structure RustDependency {
    /// The Package ID of the dependency.
    @required
    pkg: String
    /// The name of the dependency's library target.
    /// If this is a renamed dependency, this is the new name.
    name: String
    /// Array of dependency kinds.
    depKinds: RustDepKindsInfo
}

list RustDepKindsInfo {
    member: RustDepKindInfo
}

structure RustDepKindInfo {
    /// The dependency kind.
    @required
    kind: RustDepKind
    /// The target platform for the dependency.
    target: String
}

@enumKind("open")
enum RustDepKind {
    /// For old Cargo versions prior to `1.41.0`.
    UNCLASSIFIED = "unclassified"
    /// For [dependencies].
    NORMAL = "normal"
    /// For [dev-dependencies].
    DEV = "dev"
    /// For [build-dependencies].
    BUILD = "build"
}

// TODO to be deleted once "Add cargo extension" PR is merged that defines this enum
@enumKind("open")
enum RustEdition {
    E2015 = "2015"
    E2018 = "2018"
    E2021 = "2021"
}

@enumKind("open")
enum RustPackageOrigin {
    /// The package comes from the standard library.
    STDLIB = "stdlib"
    /// The package is a part of our workspace.
    WORKSPACE = "workspace"
    /// External dependency of [WORKSPACE] or other [DEPENDENCY] package.
    DEPENDENCY = "dependency"
    /// External dependency of [STDLIB] or other [STDLIB_DEPENDENCY] package.
    STDLIB_DEPENDENCY = "stdlib-dependency"
}

/// The Rust toolchain request is sent from the client to the server to query for
/// the information about project's toolchain for the given list of build targets.
///
/// The request is essential to connect and work with `intellij-rust` plugin.
@jsonRequest("buildTarget/rustToolchain")
operation RustToolchain {
    input: RustToolchainParams
    output: RustToolchainResult
}

structure RustToolchainParams {
    /// A sequence of build targets for toolchain resolution.
    @required
    targets: BuildTargetIdentifiers
}

structure RustToolchainResult {
    /// A sequence of Rust toolchains.
    @required
    toolchains: RustToolchainItems
}

list RustToolchainItems {
    member: RustToolchainItem
}

structure RustToolchainItem {
    /// Additional information about Rust toolchain.
    /// Obtained from `rustc`.
    rustStdLib: RustcInfo
    /// Path to Cargo executable.
    @required
    cargoBinPath: URI
    /// Location of the source code of procedural macros in the Rust toolchain.
    @required
    procMacroSrvPath: URI
}

structure RustcInfo {
    /// Root directory where the Rust compiler looks for standard libraries and other
    /// essential components when building Rust projects.
    @required
    sysrootPath: URI
    /// Source code for the Rust standard library.
    @required
    srcSysrootPath: URI
    /// `rustc` SemVer (Semantic Versioning) version.
    @required
    version: String
    /// Target architecture and operating system of the Rust compiler.
    /// Used by [`intellij-rust`] for checking if given toolchain is supported.
    @required
    host: String
}
