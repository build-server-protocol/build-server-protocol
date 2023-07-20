---
id: jvm
title: JVM Extension
sidebar_label: JVM
---

The following section contains JVM-specific extensions to the build server
protocol.

## BSP version

`2.1.0`

## BSP Server remote interface

### BuildTargetJvmTestEnvironment: request

The JVM test environment request is sent from the client to the server in order to
gather information required to launch a Java process. This is useful when the
client wants to control the Java process execution, for example to enable custom
Java agents or launch a custom main class during unit testing or debugging

The data provided by this endpoint may change between compilations, so it should
not be cached in any form. The client should ask for it right before test execution,
after all the targets are compiled.

- method: `buildTarget/jvmTestEnvironment`
- params: `JvmTestEnvironmentParams`
- result: `JvmTestEnvironmentResult`

#### JvmTestEnvironmentParams

```ts
export interface JvmTestEnvironmentParams {
  targets: BuildTargetIdentifier[];

  originId?: Identifier;
}
```

#### JvmTestEnvironmentResult

```ts
export interface JvmTestEnvironmentResult {
  items: JvmEnvironmentItem[];
}
```

#### JvmEnvironmentItem

```ts
export interface JvmEnvironmentItem {
  target: BuildTargetIdentifier;

  classpath: string[];

  jvmOptions: string[];

  workingDirectory: string;

  environmentVariables: Map<string, string>;

  mainClasses?: JvmMainClass[];
}
```

#### JvmMainClass

```ts
export interface JvmMainClass {
  className: string;

  arguments: string[];
}
```

### BuildTargetJvmRunEnvironment: request

Similar to `buildTarget/jvmTestEnvironment`, but returns environment
that should be used for regular exection of main classes, not for testing

- method: `buildTarget/jvmRunEnvironment`
- params: `JvmRunEnvironmentParams`
- result: `JvmRunEnvironmentResult`

#### JvmRunEnvironmentParams

```ts
export interface JvmRunEnvironmentParams {
  targets: BuildTargetIdentifier[];

  originId?: Identifier;
}
```

#### JvmRunEnvironmentResult

```ts
export interface JvmRunEnvironmentResult {
  items: JvmEnvironmentItem[];
}
```

## BuildTargetData kinds

### JvmBuildTarget

This structure is embedded in
the `data?: BuildTargetData` field, when
the `dataKind` field contains `"jvm"`.

#### JvmBuildTarget

`JvmBuildTarget` is a basic data structure that contains jvm-specific
metadata, specifically JDK reference.

```ts
export interface JvmBuildTarget {
  /** Uri representing absolute path to jdk
   * For example: file:///usr/lib/jvm/java-8-openjdk-amd64 */
  javaHome?: URI;

  /** The java version this target is supposed to use.
   * For example: 1.8 */
  javaVersion?: string;
}
```
