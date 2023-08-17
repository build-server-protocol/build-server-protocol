$version: "2"

namespace bsp.cargo

use bsp#BuildTargetIdentifiers
use bsp#StatusCode
use jsonrpc#jsonRPC
use jsonrpc#jsonRequest

@jsonRPC
service CargoBuildServer {
    operations: [
        CargoFeaturesState
        SetCargoFeatures
    ]
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

structure PackageFeatures {
    /// The Cargo package identifier.
    @required
    packageId: String
    /// The list of build target identifiers assigned to the Cargo package.
    @required
    targets: BuildTargetIdentifiers
    /// The list of available features for the Cargo package.
    @required
    availableFeatures: Features
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