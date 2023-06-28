package bsp.codegen.docs

import bsp.codegen._
import bsp.codegen.ir.Hint.Documentation
import bsp.codegen.ir.JsonRPCMethodType.{Notification, Request}
import bsp.codegen.ir.{Def, Hint, Operation, PolymorphicDataKind}
import cats.syntax.all._
import software.amazon.smithy.model.shapes.ShapeId

import scala.collection.mutable.{Set => MSet}

object MarkdownRenderer {
  def render(tree: DocTree): String = {
    val visited = MSet.empty[ShapeId]
    val renderer = new MarkdownRenderer(tree, visited)
    val rendered = renderer.render.get.mkString(System.lineSeparator())

    val notRenderedIds = visited.diff(tree.structures.keys.toSet)
    notRenderedIds.foreach { id =>
      println(s"warning: ${id.getName} was not rendered")
    }

    rendered
  }
}

class MarkdownRenderer private (tree: DocTree, visited: MSet[ShapeId]) {

  import dsl._

  val tsRenderer = new TypescriptRenderer(None)

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
    lines(
      commonShapes,
      services
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
