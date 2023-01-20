package bsp.codegen

import software.amazon.smithy.model.shapes.ShapeId

case class DocTree(
    commonShapes: List[DocNode],
    services: List[DocNode]
)

sealed trait DocNode {
  def shapeId: ShapeId
}

case class ServiceDocNode(
    shapeId: ShapeId,
    operations: List[DocNode]
) extends DocNode

case class OperationDocNode(
    operation: Operation,
    inputNode: Option[DocNode],
    outputNode: Option[DocNode]
) extends DocNode {
  def shapeId = operation.shapeId
}
case class ShapeDocNode(definition: Def, members: List[DocNode]) extends DocNode {
  def shapeId: ShapeId = definition.shapeId
}
