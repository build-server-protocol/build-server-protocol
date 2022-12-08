---
id: java
title: Java Extension
sidebar_label: Java
---

The following section contains Java-specific extensions to the build server
protocol.

## Javac Options Request

The build target scalac options request is sent from the client to the server to
query for the list of compiler options necessary to compile in a given list of
targets.

- method: `buildTarget/javacOptions`
- params: `JavacOptionsParams`

```ts
export interface JavacOptionsParams {
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `JavacOptionsResult`, defined as follows

```ts
export interface JavacOptionsResult {
  items: List[JavacOptionsItem];
}

export interface JavacOptionsItem {
  target: BuildTargetIdentifier;

  /** Additional arguments to the compiler.
   * For example, -deprecation. */
  options: List[String];

  /** The dependency classpath for this target, must be
   * identical to what is passed as arguments to
   * the -classpath flag in the command line interface
   * of javac. */
  classpath: List[Uri];

  /** The output directory for classfiles produced by this target */
  classDirectory: Uri;
}
```
