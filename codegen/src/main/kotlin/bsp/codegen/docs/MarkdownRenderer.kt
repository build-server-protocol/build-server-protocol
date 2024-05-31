package bsp.codegen.docs

import bsp.codegen.common.Loader
import bsp.codegen.common.dsl.CodeBlock
import bsp.codegen.common.dsl.code
import bsp.codegen.common.dsl.emptyCode
import bsp.codegen.common.ir.Def
import bsp.codegen.common.ir.Hint
import bsp.codegen.common.ir.JsonRpcMethodType
import bsp.codegen.common.ir.Operation
import bsp.codegen.common.ir.PolymorphicDataKind
import bsp.codegen.common.ir.Type
import bsp.codegen.common.ir.referencedShapeIds
import software.amazon.smithy.model.shapes.ShapeId

class MarkdownRenderer(
    val tree: DocTree,
    val visited: MutableSet<ShapeId>,
    val protocolVersion: String
) {
  companion object {
    fun render(tree: DocTree): String {
      val visited = mutableSetOf<ShapeId>()
      val version = Loader.protocolVersion
      val renderer = MarkdownRenderer(tree, visited, version)
      val rendered = renderer.render().toString()

      val notRenderedIds = visited.minus(tree.structures.keys.toSet())
      notRenderedIds.forEach { println("warning: ${it.name} was not rendered") }

      return rendered
    }
  }

  val tsRenderer = TypescriptRenderer()

  fun render(): CodeBlock {
    val commonShapes =
        if (tree.commonShapeIds.isEmpty()) emptyCode
        else
            code {
              -"## Common shapes"
              newline()
              tree.commonShapeIds.forEach { include(renderStructureNode(it)) }
              newline()
            }

    // Server comes first because it's bigger.
    val services = code {
      tree.services
          .sortedBy { it.operations.size }
          .reversed()
          .forEach { include(renderServiceNode(it)) }
    }

    val dataKinds = code {
      tree.dataKindInhabitants.forEach { (extendableType, kinds) ->
        include(renderExtendableTypeNode(extendableType, kinds))
      }
    }

    val versionRendered = code {
      -"## BSP version"
      -"`$protocolVersion`"
      newline()
    }

    return code {
      include(versionRendered)
      include(commonShapes)
      include(services)
      include(dataKinds)
    }
  }

  fun renderStructureNode(id: ShapeId): CodeBlock {
    // We don't want to generate definitions for built-in primitives.
    if (id.namespace == "smithy.api") {
      return emptyCode
    }

    val node =
        tree.structures[id]
            ?: throw RuntimeException("Id not found: $id, structures: ${tree.structures}")

    if (visited.contains(id)) return emptyCode

    visited.add(id)
    return code {
      include(renderStructureNodeDef(node.definition))
      node.definition
          .referencedShapeIds()
          .filter { it.namespace == id.namespace }
          .forEach { include(renderStructureNode(it)) }
    }
  }

  fun renderStructureNodeDef(definition: Def): CodeBlock = code {
    -"#### ${definition.shapeId.name}"
    newline()
    include(documentation(definition.hints))
    newline()
    include(tsBlock(definition))
    newline()
  }

  fun renderExtendableTypeNode(
      extendableType: ShapeId,
      kinds: List<PolymorphicDataKind>
  ): CodeBlock = code {
    -"## ${extendableType.name} kinds"
    newline()
    kinds.forEach { include(renderDataKindNode(extendableType)(it)) }
  }

  fun renderDataKindNode(extendableType: ShapeId): (kind: PolymorphicDataKind) -> CodeBlock =
      { kind ->
        code {
          -"### ${kind.shape.name}"
          -"This structure is embedded in"
          -"the `data?: ${extendableType.name}` field, when"
          -"the `dataKind` field contains `\"${kind.kind}\"`."
          newline()
          include(renderStructureNode(kind.shape))
        }
      }

  fun renderOperationNode(node: OperationDocNode): CodeBlock = code {
    -"### ${node.operation.name}: ${methodTpe(node.operation)}"
    newline()
    include(documentation(node.operation.hints))
    newline()
    -"- method: `${node.operation.jsonRpcMethod}`"
    node.operation.inputType
        .takeIf { it != Type.Unit }
        ?.let { -"- params: `${tsRenderer.renderType(it)}`" }
    node.operation.outputType
        .takeIf { it != Type.Unit }
        ?.let { -"- result: `${tsRenderer.renderType(it)}`" }
    newline()
    (node.operation.inputType as? Type.Ref)?.let { include(renderStructureNode(it.shapeId)) }
    (node.operation.outputType as? Type.Ref)?.let { include(renderStructureNode(it.shapeId)) }
  }

  fun renderServiceNode(node: ServiceDocNode): CodeBlock = code {
    if (node.shapeId.name.lowercase().contains("server")) {
      -"## BSP Server remote interface"
    } else {
      -"## BSP Client remote interface"
    }
    newline()
    node.operations.forEach { include(renderOperationNode(tree.operations[it]!!)) }
  }

  fun methodTpe(operation: Operation): String =
      when (operation.jsonRpcMethodType) {
        JsonRpcMethodType.Request -> "request"
        JsonRpcMethodType.Notification -> "notification"
      }

  fun documentation(hints: List<Hint>): CodeBlock = code {
    val maybeDeprecated =
        hints
            .firstNotNullOfOrNull { it as? Hint.Deprecated }
            ?.let { deprecated ->
              if (deprecated.message.isNotEmpty()) {
                "**Deprecated**: ${deprecated.message}"
              } else {
                "**Deprecated**"
              }
            }

    val maybeUnstable =
        "**Unstable** (may change in future versions)\n"
            .takeIf { hints.any { it is Hint.Unstable } }

    val docs =
        hints.filterIsInstance<Hint.Documentation>().flatMap {
          it.string.split(System.lineSeparator())
        }

    -maybeDeprecated
    -maybeUnstable
    newline()
    docs.forEach { -it }
  }

  fun tsBlock(definition: Def): CodeBlock = code {
    tsRenderer.render(definition)?.let { tsCode ->
      -"```ts"
      include(tsCode)
      -"```"
    }
  }
}
