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

    val services = tree.services.values.toList.foldMap(renderServiceNode)
    lines(
      commonShapes,
      newline,
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
        renderStructureNodeMembers(node.members)
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

  def renderStructureNodeMembers(members: List[StructureDocMember]): Lines = {
    val fields = members.filter {
      case _: StructureDocMember.Field => true
      case _                           => false
    }

    val associatedDataKinds = members.filter {
      case _: StructureDocMember.AssociatedDataKind => true
      case _                                        => false
    }

    val renderedAdks = if (associatedDataKinds.nonEmpty) {
      lines(
        s"- associated data kinds:",
        newline,
        associatedDataKinds.map(_.shapeId).foldMap(renderStructureNode),
        s"- other structures:",
        newline
      )
    } else {
      empty
    }

    lines(
      renderedAdks,
      fields.map(_.shapeId).foldMap(renderStructureNode),
      newline
    )
  }

  def renderOperationNode(node: OperationDocNode): Lines = {
    node match {
      case OperationDocNode(operation, input, output) =>
        lines(
          s"### ${operation.name}: ${methodTpe(operation)}",
          newline,
          documentation(operation.hints),
          newline,
          s"- method: `${operation.jsonRPCMethod}`",
          input.foldMap(n => s"- params: `${n.getName()}`"),
          output.foldMap(n => s"- result: `${n.getName()}`"),
          input.foldMap(renderStructureNode),
          output.foldMap(renderStructureNode),
          newline
        )
    }

  }

  def renderServiceNode(node: ServiceDocNode): Lines = {
    node match {
      case ServiceDocNode(shapeId, operations) =>
        val header = if (shapeId.getName().toLowerCase().contains("server")) {
          s"## BSP Server remote interface"
        } else {
          s"## BSP Client remote interface"
        }
        lines(
          header,
          operations
            .map(tree.operations)
            .foldMap(renderOperationNode)
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
