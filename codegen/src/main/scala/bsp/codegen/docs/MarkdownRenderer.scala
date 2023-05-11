package bsp.codegen.docs

import bsp.codegen._
import bsp.codegen.ir.Hint.Documentation
import bsp.codegen.ir.JsonRPCMethodType.{Notification, Request}
import bsp.codegen.ir.{Def, Hint, Operation}
import cats.syntax.all._
import software.amazon.smithy.model.shapes.ShapeId

import scala.collection.mutable.{Set => MSet}

object MarkdownRenderer {
  def render(tree: DocTree): String = {
    val visited = MSet.empty[ShapeId]
    val renderer = new MarkdownRenderer(tree, visited)
    renderer.render.get.mkString(System.lineSeparator())
  }
}

class MarkdownRenderer private (tree: DocTree, visited: MSet[ShapeId]) {

  import dsl._

  val tsRenderer = new TypescriptRenderer(None)

  def render: Lines = {
    val commonShapes =
      if (tree.commonShapes.isEmpty) empty
      else
        lines(
          "## Common shapes",
          newline,
          tree.commonShapes.foldMap(renderNode),
          newline
        )

    val services = tree.services.foldMap(renderNode)
    commonShapes ++ services
  }

  def renderNode(id: ShapeId): Lines = {
    // We don't want to generate definitions for built-in primitives.
    if (id.getNamespace == "smithy.api") {
      return empty
    }

    tree.docNodes(id) match {
      case OperationDocNode(operation, inputNode, outputNode) =>
        lines(
          s"### ${operation.name}: ${methodTpe(operation)}",
          newline,
          documentation(operation.hints),
          newline,
          s"- method: `${operation.jsonRPCMethod}`",
          inputNode.foldMap(n => s"- params: `${n.getName()}`"),
          outputNode.foldMap(n => s"- result: `${n.getName()}`"),
          inputNode.foldMap(renderNode),
          outputNode.foldMap(renderNode),
          newline
        )
      case ServiceDocNode(shapeId, operations) =>
        if (shapeId.getName().toLowerCase().contains("server")) {
          lines(
            s"## BSP Server remote interface",
            newline,
            operations.foldMap(renderNode)
          )
        } else {
          lines(
            s"## BSP Client remote interface",
            newline,
            operations.foldMap(renderNode)
          )
        }
      case ShapeDocNode(definition, members) =>
        if (visited.contains(definition.shapeId)) empty
        else
          {
            visited.add(definition.shapeId)
            lines(
              s"#### ${definition.shapeId.getName()}",
              newline,
              documentation(definition.hints),
              newline,
              tsBlock(definition),
              newline
            )
          } ++ lines(
            members.foldMap(renderNode)
          )
    }
  }

  def methodTpe(operation: Operation): String = operation.jsonRPCMethodType match {
    case Notification => "notification"
    case Request      => "request"
  }

  def documentation(hints: List[Hint]): Lines = Lines {
    hints.collect { case Documentation(string) => string.split(System.lineSeparator()).toList }
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
