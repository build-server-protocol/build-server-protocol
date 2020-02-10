---
id: jvm
title: JVM Extension
sidebar_label: JVM
---

The following section contains JVM-specific extensions to the build server
protocol.


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
export interface JvmEnvironmentItem{
    target: BuildTargetIdentifier;
    classpath: String[];
    jvmOptions: String[];
}

export interface JvmTestEnvironmentResult{
    items: JvmEnvironmentItem[];
    workingDirectory: String;
    environmentVariables: Map<String, String>;
}
```
