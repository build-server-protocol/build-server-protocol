package bsp.codegen

import software.amazon.smithy.model.shapes.ShapeId
import bsp.codegen.JsonRPCMethodType.Notification
import bsp.codegen.JsonRPCMethodType.Request
import bsp.codegen.Hint.Documentation
import cats.syntax.all._
import scala.collection.mutable.{Set => MSet}

object MarkdownRenderer {
  def render(tree: DocTree): String = {
    val state = MSet.empty[ShapeId]
    val renderer = new MarkdownRenderer(state)
    renderer.render(tree).get.mkString(System.lineSeparator())
  }
}

class MarkdownRenderer private (state: MSet[ShapeId]) {

  import dsl._

  def render(tree: DocTree): Lines = {
    lines(
      "## Common shapes",
      newline,
      tree.commonShapes.foldMap(renderNode),
      newline,
      tree.services.foldMap(renderNode)
    )
  }

  def renderNode(node: DocNode): Lines = node match {
    case OperationDocNode(operation, inputNode, outputNode) =>
      lines(
        s"### ${operation.name}: ${methodTpe(operation)}",
        newline,
        documentation(operation.hints),
        newline,
        s"- method: ${operation.jsonRPCMethod}",
        inputNode.foldMap(n => s"- params: ${n.shapeId.getName()}"),
        newline,
        inputNode.foldMap(tsBlock),
        newline,
        outputNode.foldMap(n => s"- result: ${n.shapeId.getName()}"),
        if (outputNode.isDefined) newline else empty,
        outputNode.foldMap(tsBlock),
        inputNode.foldMap(renderRest),
        outputNode.foldMap(renderRest),
        newline
      )
    case ServiceDocNode(shapeId, operations) => {
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
    }
    case ShapeDocNode(definition, members) =>
      if (state.contains(definition.shapeId)) empty
      else {
        state.add(definition.shapeId)
        lines(
          s"#### ${definition.shapeId.getName()}",
          newline,
          documentation(definition.hints),
          newline,
          tsBlock(definition),
          newline
        )
      }
  }

  def renderRest(doc: DocNode): Lines = doc match {
    case ShapeDocNode(definition, members) =>
      members.foldMap(renderNode)
    case _ => empty
  }

  def methodTpe(operation: Operation): String = operation.jsonRPCMethodType match {
    case Notification => "notification"
    case Request      => "request"
  }

  def documentation(hints: List[Hint]): Lines = Lines {
    hints.collect { case Documentation(string) => string.split(System.lineSeparator()).toList }
  }

  val tsRenderer = new TypescriptRenderer(None)

  def tsBlock(node: DocNode): Lines =
    node match {
      case ShapeDocNode(definition, _) => tsBlock(definition)
      case _                           => empty
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
