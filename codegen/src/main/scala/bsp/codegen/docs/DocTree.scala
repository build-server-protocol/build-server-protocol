package bsp.codegen.docs

import bsp.codegen.ir.{Def, Operation}
import software.amazon.smithy.model.shapes.ShapeId

case class DocTree(
    commonShapeIds: List[ShapeId],
    services: Map[ShapeId, ServiceDocNode],
    operations: Map[ShapeId, OperationDocNode],
    structures: Map[ShapeId, StructureDocNode]
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

sealed trait StructureDocMember {
  def shapeId: ShapeId
}

object StructureDocMember {
  case class Field(shapeId: ShapeId) extends StructureDocMember
  case class AssociatedDataKind(shapeId: ShapeId, dataKind: String) extends StructureDocMember
}

case class StructureDocNode(definition: Def, members: List[StructureDocMember]) extends DocNode {
  def shapeId: ShapeId = definition.shapeId
}
