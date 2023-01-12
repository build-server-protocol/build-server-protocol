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
