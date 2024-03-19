package bsp.codegen.bsp4s

import bsp.codegen.Lines
import bsp.codegen.dsl.{block, empty, lines, newline, paren}
import bsp.codegen.ir.Def._
import bsp.codegen.ir.EnumType.{IntEnum, StringEnum}
import bsp.codegen.ir.Primitive._
import bsp.codegen.ir.Type._
import bsp.codegen.ir._
import bsp.codegen.ir.Hint.{Deprecated, Documentation, Unstable}
import cats.implicits.toFoldableOps
import org.jetbrains.bsp.generators.{CodegenFile, Loader}
import software.amazon.smithy.model.shapes.ShapeId

import java.nio.file.Path

class ScalaRenderer(basepkg: String, definitions: List[Def], version: String) {
  import bsp.codegen.Settings.scala

  val baseRelPath: Path = Path.of(basepkg.replace(".", "/"))

  def render(): List[CodegenFile] = {
    List(renderEndpoints(), renderDefinitions(), copyCustomCodec())
  }

  private def copyCustomCodec(): CodegenFile = {
    val contents = Loader.INSTANCE.readResource("CustomCodec.scala")
    val path = Path.of("org", "jetbrains", "bsp", "util", "CustomCodec.scala")
    new CodegenFile(path, contents)
  }

  def renderDefinitions(): CodegenFile = {
    val filePath = baseRelPath.resolve("Bsp.scala")

    val renderedDefinitions = lines(definitions.map {
      case Alias(_, _, _)                       => Lines.empty
      case Structure(shapeId, fields, hints, _) => renderStructure(shapeId, fields, hints)
      case ClosedEnum(shapeId, enumType, values, hints) =>
        renderClosedEnum(shapeId, enumType, values, hints)
      case OpenEnum(shapeId, enumType, values, hints) =>
        renderOpenEnum(shapeId, enumType, values, hints)
      case Service(_, _, _) => Lines.empty
    })

    val contents = lines(
      s"package $basepkg",
      newline,
      "import org.jetbrains.bsp.util.CustomCodec",
      newline,
      "import java.net.{URI, URISyntaxException}",
      newline,
      "import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec",
      "import com.github.plokhotnyuk.jsoniter_scala.macros.named",
      "import com.github.plokhotnyuk.jsoniter_scala.core.JsonCodec",
      "import com.github.plokhotnyuk.jsoniter_scala.core.JsonWriter",
      "import com.github.plokhotnyuk.jsoniter_scala.core.JsonReader",
      "import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker",
      "import jsonrpc4s.RawJson",
      newline,
      block("object Bsp4s") {
        s"""val ProtocolVersion: String = "$version""""
      },
      newline,
      // Special handling for URI
      """
        |final case class Uri private[Uri] (val value: String) {
        |  def toPath: java.nio.file.Path =
        |    java.nio.file.Paths.get(new java.net.URI(value))
        |}
        |
        |object Uri {
        |  // This is the only valid way to create a URI
        |  def apply(u: URI): Uri = Uri(u.toString)
        |
        |  implicit val uriCodec: JsonValueCodec[Uri] = new JsonValueCodec[Uri] {
        |    def nullValue: Uri = null
        |    def encodeValue(id: Uri, out: JsonWriter): Unit = out.writeVal(id.value)
        |    def decodeValue(in: JsonReader, default: Uri): Uri = {
        |      val defaultStr = if (default == null) null else default.value
        |      val str = in.readString(defaultStr)
        |      try Uri(URI.create(str).toString)
        |      catch {
        |        case _: IllegalArgumentException | _: URISyntaxException =>
        |          in.decodeError(s"String $str is not a valid URI!")
        |      }
        |    }
        |  }
        |}""".stripMargin,
      newline,
      renderedDefinitions
    )

    new CodegenFile(filePath, contents.render)
  }

  def renderEndpoints(): CodegenFile = {
    val endpointsPath = baseRelPath.resolve("endpoints").resolve("Endpoints.scala")

    val operations = definitions.collect { case Service(shapeId, operations, _) =>
      operations
    }.flatten

    // In bsp4s there's no split between client and server endpoints

    val renderedOperations = renderOperations(operations)

    val contents = lines(
      s"package $basepkg",
      "package endpoints",
      newline,
      "import jsonrpc4s.Endpoint",
      "import jsonrpc4s.Endpoint.unitCodec",
      renderedOperations
    )

    new CodegenFile(endpointsPath, contents.render)

  }

  def renderStructure(shapeId: ShapeId, fields: List[Field], hints: List[Hint]): Lines = {
    val docsLines = renderDocs(hints)
    lines(
      docsLines,
      paren(s"final case class ${shapeId.getName()}")(
        fields.foldMap(renderScalaField)
      ),
      newline,
      block(s"object ${shapeId.getName()}")(
        lines(
          s"implicit val codec: JsonValueCodec[${shapeId.getName()}] = JsonCodecMaker.makeWithRequiredCollectionFields",
          if (fields.exists(_.tpe.isInstanceOf[TUntaggedUnion]))
            s"implicit val codecForEither: JsonValueCodec[Either[String, Int]] = CustomCodec.forEitherStringInt"
          else Lines.empty
        )
      ),
      newline
    )
  }

  def renderClosedEnum[A](
      shapeId: ShapeId,
      enumType: EnumType[A],
      values: List[EnumValue[A]],
      hints: List[Hint]
  ): Lines = {
    val valueType = enumValueType(enumType)
    val enumName = shapeId.getName()
    val docsLines = renderDocs(hints)
    lines(
      docsLines,
      s"sealed abstract class $enumName(val value: $valueType)",
      block(s"object $enumName")(
        values.map(renderEnumValueDef(enumType, shapeId)),
        newline,
        block(
          s"implicit val codec: JsonValueCodec[${shapeId.getName}] = new JsonValueCodec[${shapeId.getName}]"
        )(
          s"def nullValue: $enumName = null",
          s"def encodeValue(msg: $enumName, out: JsonWriter): Unit = out.writeVal(msg.value)",
          block(s"def decodeValue(in: JsonReader, default: $enumName): $enumName = ")(
            block("in.readInt() match ")(
              values.map { ev =>
                s"case ${ev.value} => ${toUpperCamelCase(ev.name)}"
              },
              s"""case n => in.decodeError(s"Unknown message type id for $$n")"""
            )
          )
        )
      )
    )
  }

  def renderOpenEnum[A](
      shapeId: ShapeId,
      enumType: EnumType[A],
      values: List[EnumValue[A]],
      hints: List[Hint]
  ): Lines = {
    val tpe = shapeId.getName()
    val docsLines = renderDocs(hints)
    lines(
      docsLines,
      block(s"object $tpe") {
        values.map(renderStaticValue(enumType))
      },
      newline
    )
  }

  def renderDocs(hints: List[Hint]): Lines = {
    val isUnstable = hints.contains(Unstable)
    val unstableNote = if (isUnstable) {
      List("**Unstable** (may change in future versions)")
    } else {
      List.empty
    }
    val docs = unstableNote ++
      hints.collect { case Documentation(string) =>
        string.split(System.lineSeparator()).toList
      }.flatten
    docs match {
      case Nil => empty
      case _ =>
        lines(
          "/**",
          docs.map(line => s" * $line"),
          " */"
        )
    }
  }

  def renderOperations(operations: List[Operation]): Lines = {
    val groupedByTargetName = operations.groupBy(_.jsonRPCMethod.split("/")(0))
    lines(
      groupedByTargetName.map { case (targetName, operations) =>
        val targetNameCapitalized = targetName.capitalize
        lines(
          s"object $targetNameCapitalized extends $targetNameCapitalized",
          block(s"trait $targetNameCapitalized")(
            operations.foldMap(renderOperation)
          )
        )
      }.toList,
      newline
    )
  }

  def renderOperation(operation: Operation): Lines = {
    val name = operation.jsonRPCMethod.split("/")(1)
    val output = operation.outputType match {
      case TPrimitive(Primitive.PUnit, _) => "Unit"
      case other                          => renderType(other)
    }
    val input = operation.inputType match {
      case TPrimitive(Primitive.PUnit, _) => "Unit"
      case other                          => renderType(other)
    }

    val maybeDeprecated = operation.hints.collectFirst { case Hint.Deprecated(message) =>
      if (message.isEmpty) "@deprecated" else s"""@deprecated("$message")"""
    }
    val docsLines = renderDocs(operation.hints)

    lines(
      docsLines,
      maybeDeprecated,
      s"""object $name extends Endpoint[$input, $output]("${operation.jsonRPCMethod}")"""
    )
  }

  def enumValueType[A](enumType: EnumType[A]): String = enumType match {
    case IntEnum    => "Int"
    case StringEnum => "String"
  }

  def toUpperCamelCase(str: String): String = {
    str.toLowerCase.split("_").map(_.capitalize).mkString("")
  }

  def renderStaticValue[A](enumType: EnumType[A]): EnumValue[A] => String = {
    enumType match {
      case IntEnum =>
        (ev: EnumValue[Int]) => s"""val ${toUpperCamelCase(ev.name)} = ${ev.value}"""
      case StringEnum =>
        (ev: EnumValue[String]) => s"""val ${toUpperCamelCase(ev.name)} = "${ev.value}""""
    }
  }

  def renderEnumValueDef[A](enumType: EnumType[A], shapeId: ShapeId): EnumValue[A] => String = {
    enumType match {
      case IntEnum =>
        (ev: EnumValue[Int]) =>
          s"case object ${toUpperCamelCase(ev.name)} extends ${shapeId.getName}(${ev.value})"
      case StringEnum =>
        (ev: EnumValue[String]) =>
          s"""case object ${toUpperCamelCase(ev.name)} extends ${shapeId.getName}("${ev.value}")"""
    }
  }

  def renderScalaField(field: Field): Lines = {
    val maybeRename =
      field.jsonRename.map(name => lines(s"""@named("$name")""")).getOrElse(empty)
    lines(
      maybeRename,
      renderFieldRaw(field) + ","
    )
  }

  def renderFieldRaw(field: Field): String = {
    val name =
      if (field.name == "type")
        "`type`"
      else field.name

    val tpe = if (field.required) renderType(field.tpe) else s"Option[${renderType(field.tpe)}]"

    s"$name: $tpe"
  }

  def renderType(tpe: Type): String = tpe match {
    case TRef(shapeId)             => shapeId.getName()
    case TPrimitive(prim, shapeId) => renderPrimitive(prim, shapeId)
    case TMap(key, value)          => s"Map[${renderType(key)}, ${renderType(value)}]"
    case TCollection(member)       => s"List[${renderType(member)}]"
    case TSet(member)              => s"Set[${renderType(member)}]"
    case TUntaggedUnion(tpes)      => renderUntaggedUnion(tpes)
  }

  private def renderUntaggedUnion(types: List[Type]): String = {
    val primitiveTypes = types.collect { case TPrimitive(prim, _) => prim }
    if (types.size != 2 || !(primitiveTypes == List(Primitive.PString, Primitive.PInt)))
      throw new Exception("Only unions with String and Int are supported (order matters)")

    s"Either[${types.map(renderType).mkString(", ")}]"
  }

  def renderPrimitive(prim: Primitive, shapeId: ShapeId): String = {
    // Special handling for URI
    if (shapeId == ShapeId.fromParts("bsp", "URI")) {
      "Uri"
    } else
      prim match {
        case PFloat     => "Float"
        case PDouble    => "Double"
        case PUnit      => "Unit"
        case PString    => "String"
        case PInt       => "Int"
        case PDocument  => "RawJson"
        case PBool      => "Boolean"
        case PLong      => "Long"
        case PTimestamp => "Long"
      }
  }

}
