---
id: rust
title: Rust Extension
sidebar_label: Rust
---

The following section contains Rust-specific extensions to the build server
protocol.

## BSP version

`2.2.0`

## BSP Server remote interface

### RustWorkspace: request

**Unstable** (may change in future versions)

The Rust workspace request is sent from the client to the server to query for
the information about project's workspace for the given list of build targets.

The request is essential to connect and work with `intellij-rust` plugin.

The request may take a long time, as it may require building a project to some extent
(for example with `cargo check` command).

- method: `buildTarget/rustWorkspace`
- params: `RustWorkspaceParams`
- result: `RustWorkspaceResult`

#### RustWorkspaceParams

**Unstable** (may change in future versions)

```ts
export interface RustWorkspaceParams {
  /** A sequence of build targets for workspace resolution. */
  targets: BuildTargetIdentifier[];
}
```

#### RustWorkspaceResult

**Unstable** (may change in future versions)

```ts
export interface RustWorkspaceResult {
  /** Packages of given targets. */
  packages: RustPackage[];

  /** Dependencies in `cargo metadata` as listed in the package `Cargo.toml`,
   * without package resolution or any additional data. */
  rawDependencies: Map<string, RustRawDependency[]>;

  /** Resolved dependencies of the build. Handles renamed dependencies.
   * Correspond to dependencies from resolved dependency graph from `cargo metadata` that shows
   * the actual dependencies that are being used in the build. */
  dependencies: Map<string, RustDependency[]>;

  /** A sequence of build targets taken into consideration during build process. */
  resolvedTargets: BuildTargetIdentifier[];
}
```

#### RustPackage

A `crate` is the smallest amount of code that the Rust compiler considers at a time.
It can come in one of two forms: a binary crate or a library crate.
`Binary crates` are programs you can compile to an executable that you can run,
such as a command-line program or a server.
Each must have a function called main that defines what happens when the executable runs.
`Library crates` don’t have a main function, and they don’t compile to an executable.
Instead, they define functionality intended to be shared with multiple projects.

A `package` is a bundle of one or more crates that provides a set of functionality.
It contains a Cargo.toml file that describes how to build those crates.
A package can contain many binary crates, but at most only one library crate.
However, it must contain at least one crate, whether that’s a library or binary crate.

```ts
export interface RustPackage {
  /** The package’s unique identifier */
  id: string;

  /** The package's root path. */
  rootUrl: URI;

  /** The name of the package. */
  name: string;

  /** The version of the package. */
  version: string;

  /** Defines a reason a package is in a project. */
  origin: RustPackageOrigin;

  /** Code edition of the package. */
  edition: RustEdition;

  /** The source ID of the dependency, for example:
   * "registry+https://github.com/rust-lang/crates.io-index".
   * `null` for the root package and path dependencies. */
  source?: string;

  /** Corresponds to source files which can be compiled into a crate from this package.
   * Contains only resolved targets without conflicts. */
  resolvedTargets: RustTarget[];

  /** Same as `resolvedTargets`, but contains all targets from this package.
   * `targets` should be the subset of `allTargets`. */
  allTargets: RustTarget[];

  /** Set of features defined for the package (including optional dependencies).
   * Each feature maps to an array of features or dependencies it enables.
   * The entry named "default" defines which features are enabled by default. */
  features: Map<Feature, Set<Feature>>;

  /** Array of features enabled on this package. */
  enabledFeatures: Set<Feature>;

  /** Conditional compilation flags that can be set based on certain conditions.
   * They can be used to enable or disable certain sections of code during the build process.
   * `cfgs` in Rust can take one of two forms: "cfg1" or "cfg2=\"string\"".
   * The `cfg` is split by '=' delimiter and the first half becomes key and
   * the second is aggregated to the value in `RustCfgOptions`.
   * For "cfg1" the value is empty. */
  cfgOptions?: Map<string, string[]>;

  /** Environment variables for the package. */
  env?: Map<string, string>;

  /** An absolute path which is used as a value of `OUT_DIR` environmental
   * variable when compiling current package. */
  outDirUrl?: URI;

  /** File path to compiled output of a procedural macro crate.
   * Procedural macros are macros that generate code at compile time.
   * Contains files with file extensions: `.dll`, `.so` or `.dylib`. */
  procMacroArtifact?: URI;
}
```

#### RustPackageOrigin

```ts
export type RustPackageOrigin = string;

export namespace RustPackageOrigin {
  /** External dependency of [WORKSPACE] or other [DEPENDENCY] package. */
  export const Dependency = "dependency";

  /** The package comes from the standard library. */
  export const Stdlib = "stdlib";

  /** External dependency of [STDLIB] or other [STDLIB_DEPENDENCY] package. */
  export const StdlibDependency = "stdlib-dependency";

  /** The package is a part of our workspace. */
  export const Workspace = "workspace";
}
```

#### RustEdition

The Rust edition.

```ts
export type RustEdition = string;

export namespace RustEdition {
  export const E2015 = "2015";

  export const E2018 = "2018";

  export const E2021 = "2021";
}
```

#### RustTarget

`RustTarget` contains data of the target as defined in Cargo metadata.

```ts
export interface RustTarget {
  /** The name of the target. */
  name: string;

  /** Path to the root module of the crate. */
  crateRootUrl: URI;

  /** A target's kind. */
  kind: RustTargetKind;

  /** Type of output that is produced by a crate during the build process.
   * The crate type determines how the source code is compiled. */
  crateTypes?: RustCrateType[];

  /** The Rust edition of the target. */
  edition: RustEdition;

  /** Whether or not this target has doc tests enabled, and
   * the target is compatible with doc testing. */
  doctest: boolean;

  /** A sequence of required features. */
  requiredFeatures?: Set<Feature>;
}
```

#### RustTargetKind

```ts
export enum RustTargetKind {
  /** For lib targets. */
  Lib = 1,

  /** For binaries. */
  Bin = 2,

  /** For integration tests. */
  Test = 3,

  /** For examples. */
  Example = 4,

  /** For benchmarks. */
  Bench = 5,

  /** For build scripts. */
  CustomBuild = 6,

  /** For unknown targets. */
  Unknown = 7,
}
```

#### RustCrateType

Crate types (`lib`, `rlib`, `dylib`, `cdylib`, `staticlib`) are listed for
`lib` and `example` target kinds. For other target kinds `bin` crate type is listed.

```ts
export enum RustCrateType {
  Bin = 1,

  Lib = 2,

  Rlib = 3,

  Dylib = 4,

  Cdylib = 5,

  Staticlib = 6,

  ProcMacro = 7,

  Unknown = 8,
}
```

#### Feature

```ts
export type Feature = string;
```

#### RustRawDependency

```ts
export interface RustRawDependency {
  /** The name of the dependency. */
  name: string;

  /** Name to which this dependency is renamed when declared in Cargo.toml.
   * This field allows to specify an alternative name for a dependency to use in a code,
   * regardless of how it’s published (helpful for example if multiple dependencies
   * have conflicting names). */
  rename?: string;

  /** The dependency kind. */
  kind?: RustDepKind;

  /** The target platform for the dependency. */
  target?: string;

  /** Indicates whether this is an optional dependency. */
  optional: boolean;

  /** Indicates whether default features are enabled. */
  usesDefaultFeatures: boolean;

  /** A sequence of enabled features. */
  features: Set<Feature>;
}
```

#### RustDepKind

```ts
export type RustDepKind = string;

export namespace RustDepKind {
  /** For [build-dependencies]. */
  export const Build = "build";

  /** For [dev-dependencies]. */
  export const Dev = "dev";

  /** For [dependencies]. */
  export const Normal = "normal";

  /** For old Cargo versions prior to `1.41.0`. */
  export const Unclassified = "unclassified";
}
```

#### RustDependency

```ts
export interface RustDependency {
  /** The Package ID of the dependency. */
  pkg: string;

  /** The name of the dependency's library target.
   * If this is a renamed dependency, this is the new name. */
  name?: string;

  /** Array of dependency kinds. */
  depKinds?: RustDepKindInfo[];
}
```

#### RustDepKindInfo

```ts
export interface RustDepKindInfo {
  /** The dependency kind. */
  kind: RustDepKind;

  /** The target platform for the dependency. */
  target?: string;
}
```
