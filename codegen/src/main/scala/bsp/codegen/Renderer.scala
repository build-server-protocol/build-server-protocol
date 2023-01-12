package bsp.codegen

import bsp.codegen.Def._
import bsp.codegen.Primitive._
import bsp.codegen.Type._
import cats.syntax.all._
import software.amazon.smithy.model.shapes.ShapeId

import dsl._

class Renderer(basepkg: String) {

  val baseRelPath = os.rel / basepkg.split('.')

  def render(definition: Def): Option[CodegenFile] = {
    definition match {
      case Structure(shapeId, fields)            => Some(renderStructure(shapeId, fields))
      case ClosedEnum(shapeId, enumType, values) => None
      case OpenEnum(shapeId, enumType, values)   => None
      case Service(shapeId, operations)          => None
    }
  }

  def renderStructure(shapeId: ShapeId, fields: List[Field]): CodegenFile = {
    val allLines = lines(
      renderPkg(shapeId),
      newline,
      "import org.eclipse.lsp4j.jsonrpc.validation.NonNull",
      "import org.eclipse.lsp4j.generator.JsonRpcData",
      renderImports(fields),
      newline,
      "@JsonRpcData",
      block(s"class ${shapeId.getName()}")(
        lines(fields.map(renderField)),
        newline, {
          val params = fields.map(renderField).mkString(", ")
          val assignments = fields.map(_.name).map(n => s"this.$n = $n")
          block(s"new($params)")(assignments)
        }
      )
    )

    val fileName = shapeId.getName() + ".xtends"
    CodegenFile(baseRelPath / fileName, allLines.render)
  }

  def renderPkg(shapeId: ShapeId): Lines = lines(
    s"package $basepkg"
  )

  def renderImports(fields: List[Field]): Lines =
    fields.map(_.tpe).foldMap(renderImport(_)).distinct.sorted

  def renderImport(tpe: Type): Lines = tpe match {
    case TRef(shapeId) => empty // assuming everything is generated in the same package
    case TMap(key, value) =>
      lines(s"import java.util.collection.Map") ++ renderImport(key) ++ renderImport(value)
    case TCollection(member) =>
      lines(s"import java.util.collection.List") ++ renderImport(member)
    case TUntaggedUnion(tpes) => tpes.foldMap(renderImport)
    case TPrimitive(prim)     => empty
  }

  def renderField(field: Field): String = {
    val decl = s"${renderType(field.tpe)} ${field.name}"
    if (field.required) {
      s"@NonNull $decl"
    } else decl
  }

  def renderType(tpe: Type): String = tpe match {
    case TRef(shapeId)        => shapeId.getName()
    case TPrimitive(prim)     => renderPrimitive(prim)
    case TMap(key, value)     => s"Map<${renderType(key)}, ${renderType(value)}>"
    case TCollection(member)  => s"List<${renderType(member)}>"
    case TUntaggedUnion(tpes) => renderType(tpes.head) // Todo what does bsp4j do ?
  }

  def renderPrimitive(prim: Primitive) = prim match {
    case PFloat    => "Float"
    case PDouble   => "Double"
    case PUnit     => "void"
    case PString   => "String"
    case PInt      => "Integer"
    case PDocument => "Object"
    case PBool     => "Boolean"
    case PLong     => "Long"
  }

}
