package tests

import java.net.URI

import ch.epfl.scala.{bsp4j, bsp => bsp4s}
import com.google.gson.{Gson, GsonBuilder, JsonElement}
import org.scalatest.funsuite.AnyFunSuite
import com.github.plokhotnyuk.jsoniter_scala.core.writeToString
import com.github.plokhotnyuk.jsoniter_scala.core.writeToArray
import com.github.plokhotnyuk.jsoniter_scala.core.readFromString
import com.github.plokhotnyuk.jsoniter_scala.core.WriterConfig

import scala.collection.JavaConverters._
import jsonrpc4s.RawJson

class SerializationSuite extends AnyFunSuite {

  val gson: Gson = new GsonBuilder().create()

  test("TaskFinishParams") {
    val buildTarget = new URI("anUri")

    val target = bsp4s.BuildTargetIdentifier(bsp4s.Uri(buildTarget))
    val id = bsp4s.TaskId("task", Some(List("p1", "p2", "p3")))
    val report = bsp4s.CompileReport(target, Some("origin"), 13, 12, Some(77), None)
    val bsp4sValue = bsp4s.TaskFinishParams(
      id,
      Some("asd"),
      Some(12345),
      Some("message"),
      bsp4s.StatusCode.Ok,
      Some(bsp4s.TaskFinishDataKind.CompileReport),
      Option(RawJson(writeToArray(report)))
    )

    val bsp4sJson = writeToString(bsp4sValue)
    val bsp4jValue = gson.fromJson(bsp4sJson, classOf[bsp4j.TaskFinishParams])
    val bsp4jJson = gson.toJson(bsp4jValue)
    val bsp4sValueDecoded = readFromString[bsp4s.TaskFinishParams](bsp4sJson)

    val bsp4jValueData =
      gson.fromJson(bsp4jValue.getData.asInstanceOf[JsonElement], classOf[bsp4j.CompileReport])

    assert(bsp4jValue.getTaskId.getId == bsp4sValue.taskId.id)
    assert(bsp4jValue.getOriginId == bsp4sValue.originId.get)
    assert(bsp4jValue.getTaskId.getParents.asScala == bsp4sValue.taskId.parents.get)
    assert(bsp4jValue.getEventTime == bsp4sValue.eventTime.get)
    assert(bsp4jValue.getDataKind == bsp4sValue.dataKind.get)
    assert(bsp4jValue.getStatus.getValue == bsp4sValue.status.value)

    assert(bsp4jValueData.getOriginId == report.originId.get)
    assert(bsp4jValueData.getTarget.getUri == report.target.uri.value)
    assert(bsp4jValueData.getErrors == report.errors)
    assert(bsp4jValueData.getWarnings == report.warnings)
    assert(bsp4jValueData.getTime == report.time.get)

    assert(bsp4sValueDecoded == bsp4sValue)
  }

  test("PublishDiagnosticsParams") {
    val buildTarget = new URI("build.target")
    val textDocument = new URI("text.document")

    val range1 = bsp4s.Range(bsp4s.Position(1, 1), bsp4s.Position(1, 12))
    val location = bsp4s.Location(bsp4s.Uri(new URI("other")), range1)

    val relatedInformation = bsp4s.DiagnosticRelatedInformation(location, "message")
    val data = bsp4s.ScalaDiagnostic(
      actions = Some(
        bsp4s.ScalaAction(
          title = "something syntax",
          description = None,
          edit = Some(
            bsp4s.ScalaWorkspaceEdit(
              changes = bsp4s.ScalaTextEdit(range1, "something") :: Nil
            )
          )
        ) :: Nil
      )
    )
    val dataJson = RawJson(writeToArray(data, WriterConfig))

    val diagnostic1 =
      bsp4s.Diagnostic(
        range1,
        Some(bsp4s.DiagnosticSeverity.Error),
        None,
        None,
        None,
        "message",
        None,
        Some(List(relatedInformation)),
        dataKind = Some("scala"),
        data = Some(dataJson)
      )

    val bsp4sValue = bsp4s.PublishDiagnosticsParams(
      bsp4s.TextDocumentIdentifier(bsp4s.Uri(textDocument)),
      bsp4s.BuildTargetIdentifier(bsp4s.Uri(buildTarget)),
      Some("origin"),
      List(diagnostic1),
      reset = false
    )

    // val bsp4sJson = bsp4sValue.asJson.toString()
    val bsp4sJson = writeToString(bsp4sValue, WriterConfig)
    val bsp4jValue = gson.fromJson(bsp4sJson, classOf[bsp4j.PublishDiagnosticsParams])
    val bsp4jJson = gson.toJson(bsp4jValue)
    // val bsp4sValueDecoded = decode[bsp4s.PublishDiagnosticsParams](bsp4jJson).right.get
    val bsp4sValueDecoded = readFromString[bsp4s.PublishDiagnosticsParams](bsp4jJson)

    assert(bsp4jValue.getBuildTarget.getUri == bsp4sValue.buildTarget.uri.value)
    assert(bsp4jValue.getOriginId == bsp4sValue.originId.get)
    assert(bsp4jValue.getReset == bsp4sValue.reset)
    assert(bsp4jValue.getTextDocument.getUri == bsp4sValue.textDocument.uri.value)
    val bsp4jDiagnostic1 = bsp4jValue.getDiagnostics.get(0)
    assert(bsp4jDiagnostic1.getMessage == bsp4sValue.diagnostics.head.message)
    assert(
      bsp4jDiagnostic1.getSeverity.getValue == bsp4sValue.diagnostics.head.severity.get.value
    )
    assert(bsp4sValueDecoded.diagnostics.head.data == diagnostic1.data)
    assert(bsp4sValueDecoded == bsp4sValue)
  }

  test("ScalaTestClassesItem - backward compatible framework") {
    val legacyJson =
      """
        |{
        |  "target": {"uri": ""},
        |  "classes": []
        |}""".stripMargin

    val bsp4jValue = gson.fromJson(legacyJson, classOf[bsp4j.ScalaTestClassesItem])
    val bsp4sValue = readFromString[bsp4s.ScalaTestClassesItem](legacyJson)

    assert(bsp4jValue.getTarget().getUri == bsp4sValue.target.uri.value)
    assert(bsp4jValue.getClasses().asScala.toList == bsp4sValue.classes)

    assert(bsp4jValue.getFramework() == null)
    assert(bsp4sValue.framework == None)
  }
}
