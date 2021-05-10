package tests

import ch.epfl.scala.bsp.testkit.gen.Bsp4jGenerators
import ch.epfl.scala.bsp.testkit.gen.bsp4jArbitrary._
import ch.epfl.scala.{bsp4j, bsp => bsp4s}
import com.google.gson.{Gson, GsonBuilder}
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Assertion, Assertions, FunSuite}

class SerializationPropertySuite extends FunSuite with GeneratorDrivenPropertyChecks {

  implicit val gson: Gson = new GsonBuilder().setPrettyPrinting().create()

  def assertSerializationRoundtrip[T4j,T4s](bsp4jValue: T4j)(implicit encoder: Encoder[T4s], decoder: Decoder[T4s]): Assertion = {
    val bsp4jJson = gson.toJson(bsp4jValue)
    val bsp4sValueDecoded = decode[T4s](bsp4jJson) match {
      case Left(problem) =>
        if (problem.getCause == null) Assertions.fail(problem.getMessage)
        else Assertions.fail(problem.getMessage, problem.getCause)
      case Right(value) => value
    }
    val bsp4sJson = bsp4sValueDecoded.asJson.toString()
    val bsp4jValueDecoded = gson.fromJson[T4j](bsp4sJson, bsp4jValue.getClass)

    assert(bsp4jValue == bsp4jValueDecoded)
  }

  test("BspConnectionDetails") {
    forAll { bspConnectionDetails: bsp4j.BspConnectionDetails =>
      assertSerializationRoundtrip[bsp4j.BspConnectionDetails, bsp4s.BspConnectionDetails](bspConnectionDetails)
    }
  }
  test("BuildClientCapabilities") {
    forAll { buildClientCapabilities: bsp4j.BuildClientCapabilities  =>
      assertSerializationRoundtrip[bsp4j.BuildClientCapabilities, bsp4s.BuildClientCapabilities](buildClientCapabilities)
    }
  }
  test("BuildServerCapabilities") {
    forAll { a: bsp4j.BuildServerCapabilities =>
      assertSerializationRoundtrip[bsp4j.BuildServerCapabilities,bsp4s.BuildServerCapabilities](a)
    }
  }
  test("BuildTarget") {
    forAll { a: bsp4j.BuildTarget =>
      assertSerializationRoundtrip[bsp4j.BuildTarget, bsp4s.BuildTarget](a)
    }
  }
  test("BuildTarget with Scala") {
    forAll(Bsp4jGenerators.genBuildTargetWithScala) { a: bsp4j.BuildTarget =>
      assertSerializationRoundtrip[bsp4j.BuildTarget, bsp4s.BuildTarget](a)
    }
  }
  test("BuildTargetCapabilities") {
    forAll { a: bsp4j.BuildTargetCapabilities =>
      assertSerializationRoundtrip[bsp4j.BuildTargetCapabilities, bsp4s.BuildTargetCapabilities](a)
    }
  }
  test("BuildTargetEvent") {
    forAll { a: bsp4j.BuildTargetEvent =>
      assertSerializationRoundtrip[bsp4j.BuildTargetEvent, bsp4s.BuildTargetEvent](a)
    }
  }
  test("BuildTargetEventKind") {
    forAll { a: bsp4j.BuildTargetEventKind =>
      assertSerializationRoundtrip[bsp4j.BuildTargetEventKind, bsp4s.BuildTargetEventKind](a)
    }
  }
  test("BuildTargetIdentifier") {
    forAll { a: bsp4j.BuildTargetIdentifier =>
      assertSerializationRoundtrip[bsp4j.BuildTargetIdentifier, bsp4s.BuildTargetIdentifier](a)
    }
  }
  test("CleanCacheParams") {
    forAll { a: bsp4j.CleanCacheParams =>
      assertSerializationRoundtrip[bsp4j.CleanCacheParams, bsp4s.CleanCacheParams](a)
    }
  }
  test("CleanCacheResult") {
    forAll { a: bsp4j.CleanCacheResult =>
      assertSerializationRoundtrip[bsp4j.CleanCacheResult, bsp4s.CleanCacheResult](a)
    }
  }
  test("CompileParams") {
    forAll { a: bsp4j.CompileParams =>
      assertSerializationRoundtrip[bsp4j.CompileParams, bsp4s.CompileParams](a)
    }
  }
  test("CompileProvider") {
    forAll { a: bsp4j.CompileProvider =>
      assertSerializationRoundtrip[bsp4j.CompileProvider, bsp4s.CompileProvider](a)
    }
  }
  test("CompileReport") {
    forAll { a: bsp4j.CompileReport =>
      assertSerializationRoundtrip[bsp4j.CompileReport, bsp4s.CompileReport](a)
    }
  }
  test("CompileResult") {
    forAll { a: bsp4j.CompileResult =>
      assertSerializationRoundtrip[bsp4j.CompileResult, bsp4s.CompileResult](a)
    }
  }
  test("CompileTask") {
    forAll { a: bsp4j.CompileTask =>
      assertSerializationRoundtrip[bsp4j.CompileTask, bsp4s.CompileTask](a)
    }
  }
  test("DependencySourcesItem") {
    forAll { a: bsp4j.DependencySourcesItem =>
      assertSerializationRoundtrip[bsp4j.DependencySourcesItem, bsp4s.DependencySourcesItem](a)
    }
  }
  test("DependencySourcesParams") {
    forAll { a: bsp4j.DependencySourcesItem =>
      assertSerializationRoundtrip[bsp4j.DependencySourcesItem, bsp4s.DependencySourcesItem](a)
    }
  }
  test("DependencySourcesResult") {
    forAll { a: bsp4j.DependencySourcesResult =>
      assertSerializationRoundtrip[bsp4j.DependencySourcesResult, bsp4s.DependencySourcesResult](a)
    }
  }
  test("Diagnostic") {
    forAll { a: bsp4j.Diagnostic =>
      assertSerializationRoundtrip[bsp4j.Diagnostic, bsp4s.Diagnostic](a)
    }
  }
  test("DiagnosticRelatedInformation") {
    forAll { a: bsp4j.DiagnosticRelatedInformation =>
      assertSerializationRoundtrip[bsp4j.DiagnosticRelatedInformation, bsp4s.DiagnosticRelatedInformation](a)
    }
  }
  test("DiagnosticSeverity") {
    forAll { a: bsp4j.DiagnosticSeverity =>
      assertSerializationRoundtrip[bsp4j.DiagnosticSeverity, bsp4s.DiagnosticSeverity](a)
    }
  }
  test("DidChangeBuildTarget") {
    forAll { a: bsp4j.DidChangeBuildTarget =>
      assertSerializationRoundtrip[bsp4j.DidChangeBuildTarget, bsp4s.DidChangeBuildTarget](a)
    }
  }
  test("InitializeBuildParams") {
    forAll { a: bsp4j.InitializeBuildParams =>
      assertSerializationRoundtrip[bsp4j.InitializeBuildParams, bsp4s.InitializeBuildParams](a)
    }
  }
  test("InitializeBuildResult") {
    forAll { a: bsp4j.InitializeBuildResult =>
      assertSerializationRoundtrip[bsp4j.InitializeBuildResult, bsp4s.InitializeBuildResult](a)
    }
  }
  test("InverseSourcesParams") {
    forAll { a: bsp4j.InverseSourcesParams =>
      assertSerializationRoundtrip[bsp4j.InverseSourcesParams, bsp4s.InverseSourcesParams](a)
    }
  }
  test("InverseSourcesResult") {
    forAll { a: bsp4j.InverseSourcesResult =>
      assertSerializationRoundtrip[bsp4j.InverseSourcesResult, bsp4s.InverseSourcesResult](a)
    }
  }
  test("Location") {
    forAll { a: bsp4j.Location =>
      assertSerializationRoundtrip[bsp4j.Location, bsp4s.Location](a)
    }
  }
  test("LogMessageParams") {
    forAll { a: bsp4j.LogMessageParams =>
      assertSerializationRoundtrip[bsp4j.LogMessageParams, bsp4s.LogMessageParams](a)
    }
  }
  test("MessageType") {
    forAll { a: bsp4j.MessageType =>
      assertSerializationRoundtrip[bsp4j.MessageType, bsp4s.MessageType](a)
    }
  }
  test("Position") {
    forAll { a: bsp4j.Position =>
      assertSerializationRoundtrip[bsp4j.Position, bsp4s.Position](a)
    }
  }
  test("PublishDiagnosticsParams") {
    forAll { a: bsp4j.PublishDiagnosticsParams =>
      assertSerializationRoundtrip[bsp4j.PublishDiagnosticsParams, bsp4s.PublishDiagnosticsParams](a)
    }
  }
  test("Range") {
    forAll { a: bsp4j.Range =>
      assertSerializationRoundtrip[bsp4j.Range, bsp4s.Range](a)
    }
  }
  test("ResourcesItem") {
    forAll { a: bsp4j.ResourcesItem =>
      assertSerializationRoundtrip[bsp4j.ResourcesItem, bsp4s.ResourcesItem](a)
    }
  }
  test("ResourcesParams") {
    forAll { a: bsp4j.ResourcesParams =>
      assertSerializationRoundtrip[bsp4j.ResourcesParams, bsp4s.ResourcesParams](a)
    }
  }
  test("ResourcesResult") {
    forAll { a: bsp4j.ResourcesResult =>
      assertSerializationRoundtrip[bsp4j.ResourcesResult, bsp4s.ResourcesResult](a)
    }
  }
  test("RunParams") {
    forAll { a: bsp4j.RunParams =>
      assertSerializationRoundtrip[bsp4j.RunParams, bsp4s.RunParams](a)
    }
  }
  test("RunProvider") {
    forAll { a: bsp4j.RunProvider =>
      assertSerializationRoundtrip[bsp4j.RunProvider, bsp4s.RunProvider](a)
    }
  }
  test("RunResult") {
    forAll { a: bsp4j.RunResult =>
      assertSerializationRoundtrip[bsp4j.RunResult, bsp4s.RunResult](a)
    }
  }
  test("JvmBuildTarget") {
    forAll { a: bsp4j.JvmBuildTarget =>
      assertSerializationRoundtrip[bsp4j.JvmBuildTarget, bsp4s.JvmBuildTarget](a)
    }
  }
  test("SbtBuildTarget") {
    forAll { a: bsp4j.SbtBuildTarget =>
      assertSerializationRoundtrip[bsp4j.SbtBuildTarget, bsp4s.SbtBuildTarget](a)
    }
  }
  test("ScalaBuildTarget") {
    forAll { a: bsp4j.ScalaBuildTarget =>
      assertSerializationRoundtrip[bsp4j.ScalaBuildTarget, bsp4s.ScalaBuildTarget](a)
    }
  }
  test("ScalacOptionsItem") {
    forAll { a: bsp4j.ScalacOptionsItem =>
      assertSerializationRoundtrip[bsp4j.ScalacOptionsItem, bsp4s.ScalacOptionsItem](a)
    }
  }
  test("ScalacOptionsParams") {
    forAll { a: bsp4j.ScalacOptionsParams =>
      assertSerializationRoundtrip[bsp4j.ScalacOptionsParams, bsp4s.ScalacOptionsParams](a)
    }
  }
  test("ScalacOptionsResult") {
    forAll { a: bsp4j.ScalacOptionsResult =>
      assertSerializationRoundtrip[bsp4j.ScalacOptionsResult, bsp4s.ScalacOptionsResult](a)
    }
  }
  test("ScalaMainClass") {
    forAll { a: bsp4j.ScalaMainClass =>
      assertSerializationRoundtrip[bsp4j.ScalaMainClass, bsp4s.ScalaMainClass](a)
    }
  }
  test("ScalaMainClassesItem") {
    forAll { a: bsp4j.ScalaMainClassesItem =>
      assertSerializationRoundtrip[bsp4j.ScalaMainClassesItem, bsp4s.ScalaMainClassesItem](a)
    }
  }
  test("ScalaMainClassesParams") {
    forAll { a: bsp4j.ScalaMainClassesParams =>
      assertSerializationRoundtrip[bsp4j.ScalaMainClassesParams, bsp4s.ScalaMainClassesParams](a)
    }
  }
  test("ScalaMainClassesResult") {
    forAll { a: bsp4j.ScalaMainClassesResult =>
      assertSerializationRoundtrip[bsp4j.ScalaMainClassesResult, bsp4s.ScalaMainClassesResult](a)
    }
  }
  test("ScalaPlatform") {
    forAll { a: bsp4j.ScalaPlatform =>
      assertSerializationRoundtrip[bsp4j.ScalaPlatform, bsp4s.ScalaPlatform](a)
    }
  }
  test("ScalaTestClassesItem") {
    forAll { a: bsp4j.ScalaTestClassesItem =>
      assertSerializationRoundtrip[bsp4j.ScalaTestClassesItem, bsp4s.ScalaTestClassesItem](a)
    }
  }
  test("ScalaTestClassesParams") {
    forAll { a: bsp4j.ScalaTestClassesParams =>
      assertSerializationRoundtrip[bsp4j.ScalaTestClassesParams, bsp4s.ScalaTestClassesParams](a)
    }
  }
  test("ScalaTestClassesResult") {
    forAll { a: bsp4j.ScalaTestClassesResult =>
      assertSerializationRoundtrip[bsp4j.ScalaTestClassesResult, bsp4s.ScalaTestClassesResult](a)
    }
  }
  test("ScalaTestParams") {
    forAll { a: bsp4j.ScalaTestParams =>
      assertSerializationRoundtrip[bsp4j.ScalaTestParams, bsp4s.ScalaTestParams](a)
    }
  }
  test("ShowMessageParams") {
    forAll { a: bsp4j.ShowMessageParams =>
      assertSerializationRoundtrip[bsp4j.ShowMessageParams, bsp4s.ShowMessageParams](a)
    }
  }
  test("SourceItem") {
    forAll { a: bsp4j.SourceItem =>
      assertSerializationRoundtrip[bsp4j.SourceItem, bsp4s.SourceItem](a)
    }
  }
  test("SourcesItem") {
    forAll { a: bsp4j.SourcesItem =>
      assertSerializationRoundtrip[bsp4j.SourcesItem, bsp4s.SourcesItem](a)
    }
  }
  test("SourcesParams") {
    forAll { a: bsp4j.SourcesParams =>
      assertSerializationRoundtrip[bsp4j.SourcesParams, bsp4s.SourcesParams](a)
    }
  }
  test("SourcesResult") {
    forAll { a: bsp4j.SourcesResult =>
      assertSerializationRoundtrip[bsp4j.SourcesResult, bsp4s.SourcesResult](a)
    }
  }
  test("StatusCode") {
    forAll { a: bsp4j.StatusCode =>
      assertSerializationRoundtrip[bsp4j.StatusCode, bsp4s.StatusCode](a)
    }
  }
  test("TaskFinishParams") {
    forAll { a: bsp4j.TaskFinishParams =>
      assertSerializationRoundtrip[bsp4j.TaskFinishParams, bsp4s.TaskFinishParams](a)
    }
  }
  test("TaskId") {
    forAll { a: bsp4j.TaskId =>
      assertSerializationRoundtrip[bsp4j.TaskId, bsp4s.TaskId](a)
    }
  }
  test("TaskProgressParams") {
    forAll { a: bsp4j.TaskProgressParams =>
      assertSerializationRoundtrip[bsp4j.TaskProgressParams, bsp4s.TaskProgressParams](a)
    }
  }
  test("TaskStartParams") {
    forAll { a: bsp4j.TaskStartParams =>
      assertSerializationRoundtrip[bsp4j.TaskStartParams, bsp4s.TaskStartParams](a)
    }
  }
  test("TestFinish") {
    forAll { a: bsp4j.TestFinish =>
      assertSerializationRoundtrip[bsp4j.TestFinish, bsp4s.TestFinish](a)
    }
  }
  test("TestParams") {
    forAll { a: bsp4j.TestParams =>
      assertSerializationRoundtrip[bsp4j.TestParams, bsp4s.TestParams](a)
    }
  }
  test("TestProvider") {
    forAll { a: bsp4j.TestProvider =>
      assertSerializationRoundtrip[bsp4j.TestProvider, bsp4s.TestProvider](a)
    }
  }
  test("TestReport") {
    forAll { a: bsp4j.TestReport =>
      assertSerializationRoundtrip[bsp4j.TestReport, bsp4s.TestReport](a)
    }
  }
  test("TestResult") {
    forAll { a: bsp4j.TestResult =>
      assertSerializationRoundtrip[bsp4j.TestResult, bsp4s.TestResult](a)
    }
  }
  test("TestStart") {
    forAll { a: bsp4j.TestStart =>
      assertSerializationRoundtrip[bsp4j.TestStart, bsp4s.TestStart](a)
    }
  }
  test("TestStatus") {
    forAll { a: bsp4j.TestStatus =>
      assertSerializationRoundtrip[bsp4j.TestStatus, bsp4s.TestStatus](a)
    }
  }
  test("TestTask") {
    forAll { a: bsp4j.TestTask =>
      assertSerializationRoundtrip[bsp4j.TestTask, bsp4s.TestTask](a)
    }
  }
  test("TextDocumentIdentifier") {
    forAll { a: bsp4j.TextDocumentIdentifier =>
      assertSerializationRoundtrip[bsp4j.TextDocumentIdentifier, bsp4s.TextDocumentIdentifier](a)
    }
  }
  test("WorkspaceBuildTargetsResult") {
    forAll { a: bsp4j.WorkspaceBuildTargetsResult =>
      assertSerializationRoundtrip[bsp4j.WorkspaceBuildTargetsResult, bsp4s.WorkspaceBuildTargetsResult](a)
    }
  }
  test("JvmTestEnvironmentParams") {
    forAll { a: bsp4j.JvmTestEnvironmentParams =>
      assertSerializationRoundtrip[bsp4j.JvmTestEnvironmentParams, bsp4s.JvmTestEnvironmentParams](a)
    }
  }
  test("JvmTestEnvironmentResult") {
    forAll { a: bsp4j.JvmTestEnvironmentResult =>
      assertSerializationRoundtrip[bsp4j.JvmTestEnvironmentResult, bsp4s.JvmTestEnvironmentResult](a)
    }
  }
  test("JvmRunEnvironmentParams") {
    forAll { a: bsp4j.JvmRunEnvironmentParams =>
      assertSerializationRoundtrip[bsp4j.JvmRunEnvironmentParams, bsp4s.JvmRunEnvironmentParams](a)
    }
  }
  test("JvmRunEnvironmentResult") {
    forAll { a: bsp4j.JvmRunEnvironmentResult =>
      assertSerializationRoundtrip[bsp4j.JvmRunEnvironmentResult, bsp4s.JvmRunEnvironmentResult](a)
    }
  }
  test("JvmEnvironmentItem") {
    forAll { a: bsp4j.JvmEnvironmentItem =>
      assertSerializationRoundtrip[bsp4j.JvmEnvironmentItem, bsp4s.JvmEnvironmentItem](a)
    }
  }
  test("JavacOptionsItem") {
    forAll { a: bsp4j.JavacOptionsItem =>
      assertSerializationRoundtrip[bsp4j.JavacOptionsItem, bsp4s.JavacOptionsItem](a)
    }
  }
  test("JavacOptionsParams") {
    forAll { a: bsp4j.JavacOptionsParams =>
      assertSerializationRoundtrip[bsp4j.JavacOptionsParams, bsp4s.JavacOptionsParams](a)
    }
  }
  test("JavacOptionsResult") {
    forAll { a: bsp4j.JavacOptionsResult =>
      assertSerializationRoundtrip[bsp4j.JavacOptionsResult, bsp4s.JavacOptionsResult](a)
    }
  }
  test("CppBuildTarget") {
    forAll { a: bsp4j.CppBuildTarget =>
      assertSerializationRoundtrip[bsp4j.CppBuildTarget, bsp4s.CppBuildTarget](a)
    }
  }
  test("CppOptionsItem") {
    forAll { a: bsp4j.CppOptionsItem =>
      assertSerializationRoundtrip[bsp4j.CppOptionsItem, bsp4s.CppOptionsItem](a)
    }
  }
  test("CppOptionsParams") {
    forAll { a: bsp4j.CppOptionsParams =>
      assertSerializationRoundtrip[bsp4j.CppOptionsParams, bsp4s.CppOptionsParams](a)
    }
  }
  test("CppOptionsResult") {
    forAll { a: bsp4j.CppOptionsResult =>
      assertSerializationRoundtrip[bsp4j.CppOptionsResult, bsp4s.CppOptionsResult](a)
    }
  }
}
