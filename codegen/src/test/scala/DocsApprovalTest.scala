import org.approvaltests.Approvals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters.IterableHasAsJava
import scala.jdk.StreamConverters.StreamHasToScala

// ApprovalTests lib requires JUnit.
// Running JUnit tests in ScalaTest is not trivial: it requires a custom runner.
// There's https://github.com/scalatest/scalatestplus-junit, but it doesn't support
// parameterized tests, which we need here.
class DocsApprovalTest {
  @Test
  def docsAreApproved(): Unit = {
    val docs = bsp.codegen.docs.Codegen.docs("bsp")
    Approvals.verify(docs)
  }
}

@RunWith(classOf[Parameterized])
class ExtensionApprovalTest(extension: String) {
  @Test
  def extensionIsApproved(): Unit = {
    val docs = bsp.codegen.docs.Codegen.docs(s"bsp.$extension")
    Approvals.verify(docs, Approvals.NAMES.withParameters(extension))
  }
}

object ExtensionApprovalTest {
  val extensions: List[String] = {
    // Get the list of all filenames (without file extensions) in the
    // spec/src/main/resources/META-INF/smithy/bsp/extensions directory.
    val path = Paths.get("spec/src/main/resources/META-INF/smithy/bsp/extensions")
    val filenames = Files
      .list(path)
      .toScala(List)
      .map(_.getFileName.toString.split("\\.").head)

    if (filenames.isEmpty) {
      throw new RuntimeException("No extensions found, make sre the path is correct.")
    }

    filenames
  }

  @Parameters(name = "{0}")
  def data(): java.util.Collection[String] = {
    extensions.asJavaCollection
  }
}
