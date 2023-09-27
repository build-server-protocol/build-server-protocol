---
id: java
title: Java Extension
sidebar_label: Java
---

The following section contains Java-specific extensions to the build server
protocol.

## BSP version

`2.1.0`

## BSP Server remote interface

### BuildTargetJavacOptions: request

The build target javac options request is sent from the client to the server to
query for the list of compiler options necessary to compile in a given list of
targets.

- method: `buildTarget/javacOptions`
- params: `JavacOptionsParams`
- result: `JavacOptionsResult`

#### JavacOptionsParams

```ts
export interface JavacOptionsParams {
  targets: BuildTargetIdentifier[];
}
```

#### JavacOptionsResult

```ts
export interface JavacOptionsResult {
  items: JavacOptionsItem[];
}
```

#### JavacOptionsItem

```ts
export interface JavacOptionsItem {
  target: BuildTargetIdentifier;

  /** Additional arguments to the compiler.
   * For example, -deprecation. */
  options: string[];

  /** The dependency classpath for this target, must be
   * identical to what is passed as arguments to
   * the -classpath flag in the command line interface
   * of javac. */
  classpath: string[];

  /** The output directory for classfiles produced by this target */
  classDirectory: string;
}
```
