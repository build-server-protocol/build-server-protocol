---
id: cargo
title: Cargo Extension
sidebar_label: Cargo
---

The following section contains Cargo-specific extensions to the build server
protocol.

## BSP version

`2.2.0`

## BSP Server remote interface

### CargoFeaturesState: request

**Unstable** (may change in future versions)

The cargo features state request is sent from the client to the server to
query for the current state of the Cargo features. Provides also mapping
between Cargo packages and build target identifiers.

- method: `workspace/cargoFeaturesState`
- result: `CargoFeaturesStateResult`

#### CargoFeaturesStateResult

**Unstable** (may change in future versions)

```ts
export interface CargoFeaturesStateResult {
  /** The list of Cargo packages with assigned to them target
   * identifiers and available features. */
  packagesFeatures: PackageFeatures[];
}
```

#### PackageFeatures

```ts
export interface PackageFeatures {
  /** The Cargo package identifier. */
  packageId: string;

  /** The list of build target identifiers assigned to the Cargo package. */
  targets: BuildTargetIdentifier[];

  /** The list of available features for the Cargo package. */
  availableFeatures: Map<Feature, Set<Feature>>;

  /** The list of enabled features for the Cargo package. */
  enabledFeatures: Set<Feature>;
}
```

### SetCargoFeatures: request

**Unstable** (may change in future versions)

The enable cargo features request is sent from the client to the server to
set provided features collection as a new state for
the specified Cargo package.

- method: `workspace/setCargoFeatures`
- params: `SetCargoFeaturesParams`
- result: `SetCargoFeaturesResult`

#### SetCargoFeaturesParams

**Unstable** (may change in future versions)

```ts
export interface SetCargoFeaturesParams {
  /** Package ID for which new features state will be set. */
  packageId: string;

  /** The list of features to be set as a new state. */
  features: Set<Feature>;
}
```

#### SetCargoFeaturesResult

**Unstable** (may change in future versions)

```ts
export interface SetCargoFeaturesResult {
  /** The status code of the operation. */
  statusCode: StatusCode;
}
```

## BuildTargetData kinds

### CargoBuildTarget

This structure is embedded in
the `data?: BuildTargetData` field, when
the `dataKind` field contains `"cargo"`.

#### CargoBuildTarget

`CargoBuildTarget` is a basic data structure that contains
cargo-specific metadata.

```ts
export interface CargoBuildTarget {
  edition: RustEdition;

  requiredFeatures: Set<Feature>;
}
```
