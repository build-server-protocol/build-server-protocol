package bsp.codegen.docs

import bsp.codegen._
import bsp.codegen.ir.Hint.{Documentation, Deprecated}
import bsp.codegen.ir.JsonRPCMethodType.{Notification, Request}
import bsp.codegen.ir.{Def, Hint, Operation, PolymorphicDataKind}
import cats.syntax.all._
import software.amazon.smithy.model.shapes.ShapeId

import scala.collection.mutable.{Set => MSet}

object MarkdownRenderer {
  def render(tree: DocTree): String = {
    val visited = MSet.empty[ShapeId]
    val version = ProtocolVersionLoader.version()
    val renderer = new MarkdownRenderer(tree, visited, version)
    val rendered = renderer.render.render

    val notRenderedIds = visited.diff(tree.structures.keys.toSet)
    notRenderedIds.foreach { id =>
      println(s"warning: ${id.getName} was not rendered")
    }

    rendered
  }
}

class MarkdownRenderer private (tree: DocTree, visited: MSet[ShapeId], protocolVersion: String) {
  import bsp.codegen.Settings.typescript
  import dsl._

  val tsRenderer = new TypescriptRenderer

  def render: Lines = {
    val commonShapes =
      if (tree.commonShapeIds.isEmpty) empty
      else
        lines(
          "## Common shapes",
          newline,
          tree.commonShapeIds.foldMap(renderStructureNode),
          newline
        )

    // Server comes first because it's bigger.
    val services = tree.services.sortBy(_.operations.size).reverse.foldMap(renderServiceNode)

    val dataKinds = tree.dataKindInhabitants.toList.foldMap { case (extendableType, kinds) =>
      renderExtendableTypeNode(extendableType, kinds)
    }

    val versionRendered = lines(
      "## BSP version",
      s"`$protocolVersion`",
      newline
    )

    lines(
      versionRendered,
      commonShapes,
      services,
      dataKinds
    )
  }

  def renderStructureNode(id: ShapeId): Lines = {
    // We don't want to generate definitions for built-in primitives.
    if (id.getNamespace == "smithy.api") {
      return empty
    }

    val node = tree.structures(id)

    if (visited.contains(id)) empty
    else {
      visited.add(id)
      lines(
        renderStructureNodeDef(node.definition),
        node.definition.members
          .filter(_.getNamespace == id.getNamespace)
          .foldMap(renderStructureNode)
      )
    }
  }

  def renderStructureNodeDef(definition: Def): Lines = {
    lines(
      s"#### ${definition.shapeId.getName()}",
      newline,
      documentation(definition.hints),
      newline,
      tsBlock(definition),
      newline
    )
  }

  def renderExtendableTypeNode(extendableType: ShapeId, kinds: List[PolymorphicDataKind]): Lines = {
    val header = s"## ${extendableType.getName()} kinds"
    val kindNodes = kinds.foldMap(renderDataKindNode(extendableType))
    lines(
      header,
      newline,
      kindNodes
    )
  }

  def renderDataKindNode(extendableType: ShapeId)(kind: PolymorphicDataKind): Lines = {
    lines(
      s"### ${kind.shapeId.getName()}",
      "This structure is embedded in",
      s"the `data?: ${extendableType.getName()}` field, when",
      s"""the `dataKind` field contains `"${kind.kind}"`.""",
      newline,
      renderStructureNode(kind.shapeId)
    )
  }

  def renderOperationNode(node: OperationDocNode): Lines = {
    val OperationDocNode(operation, input, output) = node
    lines(
      s"### ${operation.name}: ${methodTpe(operation)}",
      newline,
      documentation(operation.hints),
      newline,
      s"- method: `${operation.jsonRPCMethod}`",
      input.foldMap(n => s"- params: `${n.getName()}`"),
      output.foldMap(n => s"- result: `${n.getName()}`"),
      newline,
      input.foldMap(renderStructureNode),
      output.foldMap(renderStructureNode)
    )
  }

  def renderServiceNode(node: ServiceDocNode): Lines = {
    val ServiceDocNode(shapeId, operations) = node
    val header = if (shapeId.getName().toLowerCase().contains("server")) {
      s"## BSP Server remote interface"
    } else {
      s"## BSP Client remote interface"
    }
    lines(
      header,
      newline,
      operations
        .map(tree.operations)
        .foldMap(renderOperationNode)
    )
  }

  def methodTpe(operation: Operation): String = operation.jsonRPCMethodType match {
    case Notification => "notification"
    case Request      => "request"
  }

  def documentation(hints: List[Hint]): Lines = Lines {
    val maybeDeprecated = hints.collectFirst { case Deprecated(message) =>
      val line = if (message.isEmpty) List("**Deprecated**") else List(s"**Deprecated**: $message")
      lines(
        line,
        newline
      )
    }

    val docs = hints.collect { case Documentation(string) =>
      string.split(System.lineSeparator()).toList
    }

    lines(
      maybeDeprecated,
      docs
    )
  }

  def tsBlock(definition: Def): Lines =
    tsRenderer
      .render(definition)
      .map { tsCode =>
        lines(
          "```ts",
          tsCode,
          "```"
        )
      }
      .getOrElse(empty)

}
