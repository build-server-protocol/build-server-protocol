package bsp.codegen.docs

import bsp.codegen.ir.{Def, Operation, PolymorphicDataKind}
import software.amazon.smithy.model.shapes.ShapeId

case class DocTree(
    commonShapeIds: List[ShapeId],
    services: List[ServiceDocNode],
    operations: Map[ShapeId, OperationDocNode],
    structures: Map[ShapeId, StructureDocNode],
    dataKindInhabitants: Map[ShapeId, List[PolymorphicDataKind]]
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
    input: Option[ShapeId],
    output: Option[ShapeId]
) extends DocNode {
  def shapeId = operation.shapeId
}

case class StructureDocNode(definition: Def) extends DocNode {
  def shapeId: ShapeId = definition.shapeId
}
