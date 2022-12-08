---
id: python
title: Python Extension
sidebar_label: Python
---

The following section contains Python-specific extensions to the build server
protocol.

## Python Build Target

`PythonBuildTarget` is a basic data structure that contains Python-specific
metadata, specifically the interpreter reference and the Python version.
This metadata is embedded in the `data: Option[Json]` field of the `BuildTarget` definition when
the `dataKind` field contains "python".

```ts
export interface PythonBuildTarget {
  /** The Python version this target is supposed to use.
   * For example: 3.9 */
  version?: String;

  /** URI representing the path to the Python interpreter.
   * For example: file:///usr/bin/python */
  interpreter?: Uri;
}
```

## Python Options Request

The Python Options Request is sent from the client to the server to
query for the list of the interpreter flags used to run a given list of
targets.

- method: `buildTarget/pythonOptions`
- params: `PythonOptionsParams`

```ts
export interface PythonOptionsParams {
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `PythonOptionsResult`, defined as follows

```ts
export interface PythonOptionsResult {
  items: List[PythonOptionsItem];
}

export interface PythonOptionsItem {
  target: BuildTargetIdentifier;

  /** Attributes added to the interpreter command
   * For example: -E */
  linkopts: String[];
}
```
