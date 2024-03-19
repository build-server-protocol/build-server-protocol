---
id: implementations
title: Implementations
---

## Build Servers

| Build tool | Link                                                                            | Implementation language | Notes                                                         |
| ---------- | ------------------------------------------------------------------------------- | ----------------------- | ------------------------------------------------------------- |
| Bazel      | [bazel-bsp](https://github.com/JetBrains/bazel-bsp)                             | Kotlin                  | Supports Java, Scala, and Kotlin.                             |
| Bloop      | [scalacenter/bloop](https://github.com/scalacenter/bloop/)                      | Scala                   | Supports sbt, Gradle, Maven and Mill.                         |
| Mill       | [mill](https://github.com/lihaoyi/mill/)                                        | Scala                   | Built-in since mill 0.9.3, before as contrib plugin.          |
| sbt        | [sbt](https://www.scala-sbt.org/)                                               | Scala                   | [Since 1.4.0](https://github.com/sbt/sbt/releases/tag/v1.4.0) |
| scala-cli  | [scala-cli](https://scala-cli.virtuslab.org/)                                   | Scala                   | Supports Scala and Java.                                      |
| Cargo      | [cargo-bsp](https://github.com/cargo-bsp/cargo-bsp)                             | Rust                    | (Work in progress) Supports Rust.                             |
| Gradle     | [build-server-for-gradle](https://github.com/microsoft/build-server-for-gradle) | Java                    | Supports Java.                                                |

## Build Clients

| Supporting Tool | Link                                                                    | Implementation language | Notes                                              |
| --------------- | ----------------------------------------------------------------------- | ----------------------- | -------------------------------------------------- |
| IntelliJ Scala  | [JetBrains/intellij-scala](https://github.com/JetBrains/intellij-scala) | Scala                   | Implementation focused on Scala, supports Java.    |
| IntelliJ-BSP    | [JetBrains/intellij-bsp](https://github.com/JetBrains/intellij-bsp)     | Kotlin                  | New implementation with broader language support.  |
| Metals          | [scalameta/metals](https://github.com/scalameta/metals)                 | Scala                   | LSP language server.                               |
| scala-cli       | [scala-cli](https://scala-cli.virtuslab.org/)                           | Scala                   | Act as BSP client towards Bloop.                   |
| Gradle for Java | [Microsoft/vscode-gradle](https://github.com/microsoft/vscode-gradle)   | Java and TypeScript     | Act as BSP client towards Build Server for Gradle. |
