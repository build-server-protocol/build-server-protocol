$version: "2"

namespace bsp.cargo

use bsp#BuildTargetData
use bsp#BuildTargetIdentifiers
use bsp#StatusCode
use bsp.rust#FeaturesDependencyGraph
use bsp.rust#Features
use bsp.rust#RustEdition
use traits#dataKind
use traits#jsonRPC
use traits#jsonRequest

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
    edition: RustEdition
    @required
    requiredFeatures: Features
}

/// The cargo features state request is sent from the client to the server to
/// query for the current state of the Cargo features. Provides also mapping
/// between Cargo packages and build target identifiers.
@unstable
@jsonRequest("workspace/cargoFeaturesState")
operation CargoFeaturesState {
    output: CargoFeaturesStateResult
}

@unstable
structure CargoFeaturesStateResult {
    /// The list of Cargo packages with assigned to them target
    /// identifiers and available features.
    @required
    packagesFeatures: PackagesFeatures
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
    availableFeatures: FeaturesDependencyGraph
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
@unstable
@jsonRequest("workspace/setCargoFeatures")
operation SetCargoFeatures {
    input: SetCargoFeaturesParams
    output: SetCargoFeaturesResult
}

@unstable
structure SetCargoFeaturesParams {
    /// Package ID for which new features state will be set.
    @required
    packageId: String
    /// The list of features to be set as a new state.
    @required
    features: Features
}

@unstable
structure SetCargoFeaturesResult {
    /// The status code of the operation.
    @required
    statusCode: StatusCode
}
