---
id: jvm
title: JVM Extension
sidebar_label: JVM
---

The following section contains JVM-specific extensions to the build server
protocol.

## JVM Build Target

`JvmBuildTarget` is a basic data structure that contains jvm-specific
metadata, specifically JDK reference. This metadata is embedded in
the `data: Option[Json]` field of the `BuildTarget` definition, when
the `dataKind` field contains "jvm".

```ts
export interface JvmBuildTarget {
  /** Uri representing absolute path to jdk
   * For example: file:///usr/lib/jvm/java-8-openjdk-amd64 */
  javaHome?: Uri;

  /** The java version this target is supposed to use.
   * For example: 1.8 */
  javaVersion?: String;
}
```

## Test Environment Request

The JVM test environment request is sent from the client to the server in order to
gather information required to launch a Java process. This is useful when the
client wants to control the Java process execution, for example to enable custom
Java agents or launch a custom main class during unit testing or debugging

The data provided by this endpoint may change between compilations, so it should
not be cached in any form. The client should ask for it right before test execution,
after all the targets are compiled.

- method: `buildTarget/jvmTestEnvironment`
- params: `JvmTestEnvironmentParams`

```ts
export interface JvmTestEnvironmentParams(
    targets: BuildTargetIdentifier[],
    originId?: String
)
```

Response:

- result: `JvmTestEnvironmentResult`, defined as follows
- error: JSON-RPC code and message set in case an exception happens during the
  request.

```ts
export interface JvmMainClass {
  class: String;
  arguments: String[];
}

export interface JvmEnvironmentItem {
  target: BuildTargetIdentifier;
  classpath: Uri[];
  jvmOptions: String[];
  workingDirectory: String;
  environmentVariables: Map<String, String>;
  mainClasses: JvmMainClass[];
}

export interface JvmTestEnvironmentResult {
  items: JvmEnvironmentItem[];
}
```

## Run Environment Request

Similar to `buildTarget/jvmTestEnvironment`, but returns environment
that should be used for regular exection of main classes, not for testing

- method: `buildTarget/jvmRunEnvironment`
- params: `JvmRunEnvironmentParams`

```ts
export interface JvmRunEnvironmentParams(
    targets: BuildTargetIdentifier[],
    originId?: String
)
```

Response:

- result: `JvmRunEnvironmentResult`, defined as follows
- error: JSON-RPC code and message set in case an exception happens during the
  request.

```ts
export interface JvmMainClass {
  class: String;
  arguments: String[];
}

export interface JvmEnvironmentItem {
  target: BuildTargetIdentifier;
  classpath: Uri[];
  jvmOptions: String[];
  workingDirectory: String;
  environmentVariables: Map<String, String>;
  mainClasses: JvmMainClass[];
}

export interface JvmRunEnvironmentResult {
  items: JvmEnvironmentItem[];
}
```
