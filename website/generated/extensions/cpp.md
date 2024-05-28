---
id: cpp
title: C++ Extension
sidebar_label: cpp
---

The following section contains C++-specific extensions to the build server
protocol.

## BSP version

`2.2.0`

## BSP Server remote interface

### BuildTargetCppOptions: request

The build target cpp options request is sent from the client to the server to
query for the list of compiler options necessary to compile in a given list of
targets.

- method: `buildTarget/cppOptions`
- params: `CppOptionsParams`
- result: `CppOptionsResult`

#### CppOptionsParams

```ts
export interface CppOptionsParams {
  /** The targets for which the options are requested. */
  targets: BuildTargetIdentifier[];
}
```

#### CppOptionsResult

```ts
export interface CppOptionsResult {
  /** The list of options for each target. */
  items: CppOptionsItem[];
}
```

#### CppOptionsItem

```ts
export interface CppOptionsItem {
  /** The target identifier for which the options are requested. */
  target: BuildTargetIdentifier;

  /** Attributes added in the given order to COPTS
   * before compiling the target.
   * For example: -Iexternal/gtest/include */
  copts: string[];

  /** Attributes prepended with -D
   * and added to the compile command line
   * For example: BOOST_FALLTHROUGH */
  defines: string[];

  /** Attributes added to the linker command
   * For example: -pthread */
  linkopts: string[];

  /** Create a shared library.
   * The presence of this flag means that linking occurs with the -shared flag */
  linkshared?: boolean;
}
```

## BuildTargetData kinds

### CppBuildTarget

This structure is embedded in
the `data?: BuildTargetData` field, when
the `dataKind` field contains `"cpp"`.

#### CppBuildTarget

`CppBuildTarget` is a basic data structure that contains c++-specific
metadata, specifically compiler reference.

```ts
export interface CppBuildTarget {
  /** The c++ version this target is supposed to use.
   * For example: C++11 */
  version?: string;

  /** The type of compiler this target is supposed to use.
   * For example: gcc */
  compiler?: string;

  /** Uri representating path to the c compiler.
   * For example: file:///usr/bin/gcc */
  cCompiler?: URI;

  /** Uri representating path to the c++ compiler.
   * For example: file:///usr/bin/g++ */
  cppCompiler?: URI;
}
```
