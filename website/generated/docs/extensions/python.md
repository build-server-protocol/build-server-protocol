---
id: python
title: Python Extension
sidebar_label: Python
---

The following section contains Python-specific extensions to the build server
protocol.

## BSP version

`2.1.0`

## BSP Server remote interface

### BuildTargetPythonOptions: request

The Python Options Request is sent from the client to the server to
query for the list of the interpreter flags used to run a given list of
targets.

- method: `buildTarget/pythonOptions`
- params: `PythonOptionsParams`
- result: `PythonOptionsResult`

#### PythonOptionsParams

```ts
export interface PythonOptionsParams {
  targets: BuildTargetIdentifier[];
}
```

#### PythonOptionsResult

```ts
export interface PythonOptionsResult {
  items: PythonOptionsItem[];
}
```

#### PythonOptionsItem

```ts
export interface PythonOptionsItem {
  target: BuildTargetIdentifier;

  /** Attributes added to the interpreter command
   * For example: -E */
  interpreterOptions: string[];
}
```

## BuildTargetData kinds

### PythonBuildTarget

This structure is embedded in
the `data?: BuildTargetData` field, when
the `dataKind` field contains `"python"`.

#### PythonBuildTarget

`PythonBuildTarget` is a basic data structure that contains Python-specific
metadata, specifically the interpreter reference and the Python version.

```ts
export interface PythonBuildTarget {
  version?: string;

  interpreter?: URI;
}
```
