package tests

import java.nio.file.Files
import java.util.{Collections, Optional}

import ch.epfl.scala.bsp.testkit.client.TestClient
import ch.epfl.scala.bsp.testkit.mock.MockServer
import ch.epfl.scala.bsp.testkit.mock.MockServer.LocalMockServer
import ch.epfl.scala.bsp4j._
import com.google.common.collect.Lists
import org.scalatest.FunSuite

import scala.jdk.CollectionConverters._

class MockClientSuite extends FunSuite {

  private val testDirectory = Files.createTempDirectory("bsp.MockClientSuite")
  private val initializeBuildParams =
    new InitializeBuildParams(
      "Mock-Client",
      "0.0",
      "2.0",
      testDirectory.toUri.toString,
      new BuildClientCapabilities(Collections.singletonList("scala"))
    )

  val targetId2 = new BuildTargetIdentifier(testDirectory.resolve("target2").toString)
  val targetId3 = new BuildTargetIdentifier(testDirectory.resolve("target3").toString)

  val target2 = new BuildTarget(
    targetId2,
    List(BuildTargetTag.TEST).asJava,
    Collections.emptyList(),
    Collections.emptyList(),
    new BuildTargetCapabilities(true, true, false)
  )
  val target3 = new BuildTarget(
    targetId3,
    List(BuildTargetTag.APPLICATION).asJava,
    Collections.emptyList(),
    Collections.emptyList(),
    new BuildTargetCapabilities(true, false, true)
  )

  private val client = TestClient(
    () => {
      val LocalMockServer(running, in, out) = MockServer.startMockServer(testDirectory.toFile)
      (out, in, () => running.cancel(true))
    },
    initializeBuildParams
  )

  test("Initial imports") {
    client.testResolveProject()
  }

  test("Test server capabilities") {
    client.testTargetCapabilities()
  }

  test("Running batch tests") {
    client.testMultipleUnitTests(
      Lists.newArrayList(
        TestClient.ClientUnitTest.ResolveProjectTest,
        TestClient.ClientUnitTest.CompileSuccessfully
      )
    )
  }

  test("Test Compile of all targets") {
    client.testTargetsCompileSuccessfully(targets = Optional.empty())
  }

  test("Clean cache") {
    client.testCleanCacheSuccessfully()
  }

  test("Run Targets") {
    client.testTargetsRunSuccessfully(
      Optional.of(
        Lists.newArrayList(
          target3
        )
      )
    )
  }

  test("Run Tests") {
    client.testTargetsTestSuccessfully(Optional.of(Lists.newArrayList(
      target2
    )))
  }

}
