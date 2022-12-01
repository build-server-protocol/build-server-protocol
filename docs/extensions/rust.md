---
id: rust
title: Rust Extension
sidebar_label: Rust
---

The following section contains Rust-specific extensions to the build server
protocol.

## Rust Build Target

`RustBuildTarget` is a basic data structure that contains Rust-specific
metadata, specifically the compiler reference and the Rust edition.
This metadata is embedded in the `data: Option[Json]` field of the `BuildTarget` definition when
the `dataKind` field contains "rust".

```ts
export interface RustBuildTarget {
  /** The Rust edition this target is supposed to use. 
    * For example: 2021 */
  edition?: String;

  /** URI representing the path to the Rust compiler. 
    * For example: file:///usr/bin/cargo */
  compiler?: Uri;  
}
```

## Rust Options Request

The Rust Options Request is sent from the client to the server to
query for the list of the compiler flags used to run a given list of
targets.

- method: `buildTarget/rustOptions`
- params: `RustOptionsParams`

```ts
export interface RustOptionsParams {
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `RustOptionsResult`, defined as follows

```ts
export interface RustOptionsResult {
  items: List[RustOptionsItem];
}

export interface RustOptionsItem {
  target: BuildTargetIdentifier;
    
  /** Attributes added to the compiler command
    * For example: -q */
  compilerOptions: String[];
}
```
