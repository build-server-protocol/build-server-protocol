---
id: faq
title: Frequently Asked Questions
---

**Q**: What's the relationship between BSP and LSP?

**A**: They are complementary protocols. While LSP specifies endpoints for
communication between an **editor acting as client** and language server, BSP
specifies endpoints between a **language server acting as client** and build
server. For example, in order to respond to a `textDocument/definition` request
from an editor client, a language server could query a build tool via BSP for
the classpath of a module.

**Q**: What's the relationship between implementations of BSP and
implementations of LSP like
[dragos/dragos-vscode-scala](https://github.com/dragos/dragos-vscode-scala),
[Dotty IDE](https://marketplace.visualstudio.com/items?itemName=lampepfl.dotty)
or [Metals](https://github.com/scalameta/metals)?

**A**: Currently, those language servers each implement custom integrations for
each supported build tool to extract build metadata. Those language servers
could instead implement a BSP client to extract build metadata from any build
tools that implement BSP, sharing a single BSP server implementation. Likewise,
a new build tool could implement a BSP server and support a wide range of
language servers out-of-the-box.

**Q**: Should non-Scala participants in the protocol generate data types from
`bsp.proto` or is it preferable to use pre-generated artifacts in maven (or
other repos)?

**A**: BSP uses JSON on the wire like LSP, it is not necessary to use
`bsp.proto`. The `bsp.proto` schema is provided as a language-agnostic reference
schema for the shape of BSP data structures, similarly to how LSP messages are
defined using TypeScript interfaces. Like with LSP, it is left to BSP
participant to figure out how to produce JSON payloads with BSP data structures.
