import bsp.codegen.ExtensionLoader
import org.approvaltests.Approvals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

import scala.collection.JavaConverters.seqAsJavaListConverter

// ApprovalTests lib requires JUnit.
// Running JUnit tests in ScalaTest is not trivial: it requires a custom runner.
// There's https://github.com/scalatest/scalatestplus-junit, but it doesn't support
// parameterized tests, which we need here.
@RunWith(classOf[Parameterized])
class DocsApprovalTest(namespace: String) {
  @Test
  def docsAreApproved(): Unit = {
    val docs = bsp.codegen.docs.Codegen.docs(namespace)
    Approvals.verify(docs, CustomApprovalNamer.optionsFromName(namespace))
  }
}

object DocsApprovalTest {
  @Parameters(name = "{0}")
  def data(): java.util.Collection[String] = {
    ExtensionLoader.namespaces().asJava
  }
}
