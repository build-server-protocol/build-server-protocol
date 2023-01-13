package bsp.codegen

import software.amazon.smithy.model.shapes.ShapeId

case class CodegenFile(shapeId: ShapeId, path: os.RelPath, contents: String)
