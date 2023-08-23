$version: "2"

namespace bsp.cargo

use bsp#BuildTargetData
use bsp#BuildTargetIdentifiers
use bsp#StatusCode
use jsonrpc#dataKind
use jsonrpc#enumKind
use jsonrpc#jsonRPC
use jsonrpc#jsonRequest

@jsonRPC
service CargoBuildServer {
    operations: [
        CargoFeaturesState
        SetCargoFeatures
    ]
}

/// `CargoBuildTarget` is a basic data structure that contains
/// cargo-specific metadata.
@dataKind(kind: "cargo", extends: [BuildTargetData])
structure CargoBuildTarget {
    @required
    edition: Edition
    @required
    required_features: Features
}

/// The Rust edition.
/// As of writing this comment rust editions 2024, 2027 and 2030 are not
/// actually a thing yet but are parsed nonetheless for future proofing.
@enumKind("open")
enum Edition {
    E2015 = "2015"
    E2018 = "2018"
    E2021 = "2021"
    _E2024 = "2024"
    _E2027 = "2027"
    _E2030 = "2030"
}

/// The cargo features state request is sent from the client to the server to
/// query for the current state of the Cargo features. Provides also mapping
/// between Cargo packages and build target identifiers.
@jsonRequest("workspace/cargoFeaturesState")
operation CargoFeaturesState {
    output: CargoFeaturesStateResult
}

structure CargoFeaturesStateResult {
    /// The list of Cargo packages with assigned to them target
    /// identifiers and available features.
    @required
    packagesFeatures: PackagesFeatures
}

list Features {
    member: String
}

/// The feature dependency graph is a mapping between
/// feature and the features it turns on
map FeatureDependencyGraph {
    key: String,
    value: Features
}

structure PackageFeatures {
    /// The Cargo package identifier.
    @required
    packageId: String
    /// The list of build target identifiers assigned to the Cargo package.
    @required
    targets: BuildTargetIdentifiers
    /// The list of available features for the Cargo package.
    @required
    availableFeatures: FeatureDependencyGraph
    /// The list of enabled features for the Cargo package.
    @required
    enabledFeatures: Features
}

list PackagesFeatures {
    member: PackageFeatures
}

/// The enable cargo features request is sent from the client to the server to
/// set provided features collection as a new state for
/// the specified Cargo package.
@jsonRequest("workspace/setCargoFeatures")
operation SetCargoFeatures {
    input: SetCargoFeaturesParams
    output: SetCargoFeaturesResult
}

structure SetCargoFeaturesParams {
    /// Package ID for which new features state will be set.
    @required
    packageId: String
    /// The list of features to be set as a new state.
    @required
    features: Features
}

structure  SetCargoFeaturesResult {
    /// The status code of the operation.
    @required
    statusCode: StatusCode
}