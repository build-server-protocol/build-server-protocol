package bsp.codegen.docs

import bsp.codegen.common.ir.Def
import bsp.codegen.common.ir.EnumType
import bsp.codegen.common.ir.EnumValue
import bsp.codegen.common.ir.Operation
import bsp.codegen.common.ir.PolymorphicDataKind
import bsp.codegen.common.ir.SmithyToIr
import bsp.codegen.common.ir.Type
import bsp.codegen.common.util.kebabToScreamingSnakeCase
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.traits.TagsTrait
import traits.DocsPriorityTrait

data class DocTree(
    val commonShapeIds: List<ShapeId>,
    val services: List<ServiceDocNode>,
    val operations: Map<ShapeId, OperationDocNode>,
    val structures: Map<ShapeId, StructureDocNode>,
    val dataKindInhabitants: Map<ShapeId, List<PolymorphicDataKind>>
)

sealed interface DocNode {
  val shapeId: ShapeId
}

data class ServiceDocNode(override val shapeId: ShapeId, val operations: List<ShapeId>) : DocNode

data class OperationDocNode(val operation: Operation) : DocNode {
  override val shapeId = operation.shapeId
}

data class StructureDocNode(val definition: Def) : DocNode {
  override val shapeId: ShapeId = definition.shapeId
}

fun SmithyToIr.docTree(namespace: String): DocTree {
  val shapes = model.shapes().filter { it.id.namespace == namespace }
  val commonTag = "basic"
  val commonShapes =
      shapes
          .filter { shape ->
            shape.getTrait(TagsTrait::class.java).map { it.tags.contains(commonTag) }.orElse(false)
          }
          .toList()
          .sortedByDescending {
            it.getTrait(DocsPriorityTrait::class.java).map { t -> t.priority }.orElse(0)
          }

  val definitions = this.definitions(namespace)

  val operations =
      definitions
          .flatMap { def ->
            when (def) {
              is Def.Service -> def.operations.map { OperationDocNode(it) }
              else -> emptyList()
            }
          }
          .associateBy { it.shapeId }

  val services =
      definitions.filterIsInstance<Def.Service>().map { def ->
        ServiceDocNode(def.shapeId, def.operations.map { it.shapeId })
      }

  val structures = definitions.flatMap { toStructureNodes(it) }.associateBy { it.shapeId }

  val commonShapeIds = commonShapes.map { it.id }

  val dataKindInhabitants =
      allDataKindAnnotated
          .mapValues { (_, inhabitants) -> inhabitants.filter { it.shape.namespace == namespace } }
          .filterValues { it.isNotEmpty() }

  return DocTree(commonShapeIds, services, operations, structures, dataKindInhabitants)
}

private fun toStructureNodes(definition: Def): List<StructureDocNode> {
  return when (definition) {
    is Def.Structure -> listOf(StructureDocNode(definition))
    is Def.OpenEnum<*> -> listOf(StructureDocNode(definition))
    is Def.ClosedEnum<*> -> listOf(StructureDocNode(definition))
    is Def.Alias -> listOf(StructureDocNode(definition))
    is Def.DataKinds -> {
      val data = Def.Alias(definition.shapeId, Type.Json, emptyList())
      val values =
          definition.kinds.map { polyData ->
            val snakeCased = polyData.kind.kebabToScreamingSnakeCase()
            EnumValue(snakeCased, polyData.kind, polyData.hints)
          }
      val openEnum =
          Def.OpenEnum(definition.kindsEnumId, EnumType.StringEnum, values, definition.hints)
      listOf(StructureDocNode(data), StructureDocNode(openEnum))
    }
    else -> emptyList()
  }
}
