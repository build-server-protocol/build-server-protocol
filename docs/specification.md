---
id: specification
title: Build Server Protocol
sidebar_label: Specification
---

This document is the specification of the Build Server Protocol (BSP).

Edits to this specification can be made via a pull request against this markdown
document, see "edit" button at the bottom of this page on the website.

## Motivation

The goal of BSP is to reduce the effort required by tooling developers to
integrate between available IDEs and build tools. Currently, every IDE must
implement a custom integration for each supported build tool in order to extract
information such as source directory layouts or compiler options. Likewise, new
build tools are expected to integrate with all available IDEs. The growing
number of IDEs and build tools in the wider programming community means tooling
developers spend a lot of time working on these integrations.

The Build Server Protocol defines common functionality that both build tools
(servers) and IDEs (clients) understand. This common functionality enables
tooling developers to provide their end users the best developer experience
while supporting build tools and language servers with less effort and time.

## Background

The Build Server Protocol takes inspiration from the Language Server Protocol
(LSP). Unlike in the Language Server Protocol, the language server or IDE is
referred to as the “client” and a build tool such as sbt/Gradle/Bazel is
referred to as the “server”.

The best way to read this document is by considering it as a wishlist from the
perspective of an IDE developer.

The code listings in this document are written using TypeScript syntax. Every
data strucuture in this document has a direct translation to JSON and Protobuf.

## Relationship with LSP

BSP can be used together with LSP in the same architecture. The diagram below
illustrates an example how an LSP server can also act as a BSP client.

![](https://i.imgur.com/q4KEas9.png)

BSP can also be used without LSP. In the example above, IntelliJ acts as a BSP
client even if IntelliJ does not use LSP.

## Status

The Build Server Protocol is not an approved standard. Everything in this
document is subject to change and open for discussions, including core data
structures.

The creation of BSP clients and servers is under active development.

In the clients space, IntelliJ has been the first language server to implement
BSP. The integration is available in the nightly releases of the Scala plugin.
Other language servers, like [Dotty IDE](https://github.com/lampepfl/dotty) and
[scalameta/metals](https://github.com/scalameta/metals), are currently working
or planning to work on a BSP integrations.

On the server side,

- [Bloop](https://github.com/scalacenter/bloop) was the first
  server to implement BSP
- sbt added built-in support in [1.4.0](https://github.com/sbt/sbt/pull/5538),
- Mill ships with [built-in BSP support](https://mill-build.com/mill/Installation_IDE_Support.html#_build_server_protocol_bsp)
- Bazel support is provided by [bazel-bsp](https://github.com/JetBrains/bazel-bsp)

We're looking for third parties that implement BSP natively in other build tools
such as Gradle.

The Build Server Protocol has been designed to be language-agnostic. We're
looking for ways to collaborate with other programming language communities and
build tool authors.

The best way to share your thoughts on the Build Server Protocol or to get
involved in its development is to open an issue or pull request to this
repository. Any help on developing integrations will be much appreciated.

## Base protocol

The base protocol is identical to the language server base protocol. See
<https://microsoft.github.io/language-server-protocol/specification> for more
details.

Like the language server protocol, the build server protocol defines a set of
JSON-RPC request, response and notification messages which are exchanged using
the base protocol.

### Capabilities

Unlike the language server protocol, the build server protocol does not support
dynamic registration of capabilities. The motivation for this change is
simplicity. If a motivating example for dynamic registration comes up this
decision can be reconsidered. The server and client capabilities must be
communicated through the initialize request.

### Server lifetime

Like the language server protocol, the current protocol specification defines
that the lifetime of a build server is managed by the client (e.g. a language
server like Dotty IDE). It is up to the client to decide when to start
(process-wise) and when to shutdown a server.

```scala mdoc:passthrough
bsp.codegen.docs.Codegen.printDocs("bsp")
```
