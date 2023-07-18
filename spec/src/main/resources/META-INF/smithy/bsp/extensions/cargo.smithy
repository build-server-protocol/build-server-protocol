$version: "2"

namespace bsp.cargo

use bsp#BuildTargetIdentifiers
use jsonrpc#jsonRPC
use jsonrpc#jsonRequest

@jsonRPC
service CargoBuildServer {
    operations: [
        CargoFeaturesState
        EnableCargoFeatures
        DisableCargoFeatures
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
    packagesFeatures: PackageFeatures
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
/// enable features for the specified Cargo package.
@jsonRequest("workspace/enableCargoFeatures")
operation EnableCargoFeatures {
    input: EnableCargoFeaturesParams
}

structure EnableCargoFeaturesParams {
    /// Package ID to enable features for.
    @required
    packageId: String
    /// The list of features to enable.
    @required
    features: Features
}

/// The disable cargo features request is sent from the client to the server to
/// disable features for the specified Cargo package.
@jsonRequest("workspace/disableCargoFeatures")
operation DisableCargoFeatures {
    input: DisableCargoFeaturesParams
}


structure DisableCargoFeaturesParams {
    /// Package ID to disable features for.
    @required
    packageId: String
    /// The list of features to disable.
    @required
    features: Features
}
