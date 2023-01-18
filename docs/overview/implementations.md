---
id: implementations
title: Implementations
---

## Build Servers

| Build tool | Link                                                       | Implementation language | Notes                                                         |
| ---------- | ---------------------------------------------------------- | ----------------------- | ------------------------------------------------------------- |
| Bazel      | [bazel-bsp](https://github.com/JetBrains/bazel-bsp)        | Java                    | Supports Java, Scala, and Kotlin.                             |
| Bloop      | [scalacenter/bloop](https://github.com/scalacenter/bloop/) | Scala                   | Supports sbt, Gradle, Maven and Mill.                         |
| Mill       | [mill](https://github.com/lihaoyi/mill/)                   | Scala                   | Built-in since mill 0.9.3, before as contrib plugin.          |
| sbt        | [sbt](https://www.scala-sbt.org/)                          | Scala                   | [Since 1.4.0](https://github.com/sbt/sbt/releases/tag/v1.4.0) |

## Build Clients

| Supporting Tool | Link                                                                    | Implementation language |
| --------------- | ----------------------------------------------------------------------- | ----------------------- |
| IntelliJ Scala  | [Jetbrains/intellij-scala](https://github.com/Jetbrains/intellij-scala) | Scala                   |
| Metals          | [scalameta/metals](https://github.com/scalameta/metals)                 | Scala                   |
