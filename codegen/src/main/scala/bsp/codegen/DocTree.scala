package bsp.codegen

import software.amazon.smithy.model.shapes.ShapeId

case class DocTree(
    commonShapes: List[ShapeId],
    services: List[ShapeId],
    docNodes: Map[ShapeId, DocNode]
)

sealed trait DocNode {
  def shapeId: ShapeId
}

case class ServiceDocNode(
    shapeId: ShapeId,
    operations: List[ShapeId]
) extends DocNode

case class OperationDocNode(
    operation: Operation,
    inputNode: Option[ShapeId],
    outputNode: Option[ShapeId]
) extends DocNode {
  def shapeId = operation.shapeId
}
case class ShapeDocNode(definition: Def, members: List[ShapeId]) extends DocNode {
  def shapeId: ShapeId = definition.shapeId
}
