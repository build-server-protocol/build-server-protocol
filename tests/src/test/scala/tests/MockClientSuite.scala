package tests

import ch.epfl.scala.bsp.testkit.client.TestClient
import ch.epfl.scala.bsp.testkit.mock.MockServer
import ch.epfl.scala.bsp.testkit.mock.MockServer.LocalMockServer
import ch.epfl.scala.bsp4j._
import com.google.common.collect.Lists
import org.scalatest.funsuite.AnyFunSuite

import java.net.URI
import java.nio.file.{Files, Paths}
import java.util.Collections
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

class MockClientSuite extends AnyFunSuite {

  private val testDirectory = Files.createTempDirectory("bsp.MockClientSuite")
  private val initializeBuildParams =
    new InitializeBuildParams(
      "Mock-Client",
      "0.0",
      "2.0",
      testDirectory.toUri.toString,
      new BuildClientCapabilities(Collections.singletonList("scala"))
    )

  val baseUri: URI = testDirectory.toFile.getCanonicalFile.toURI
  private implicit val executionContext: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  val targetId1 = new BuildTargetIdentifier(baseUri.resolve("target1").toString)
  val targetId2 = new BuildTargetIdentifier(baseUri.resolve("target2").toString)
  val targetId3 = new BuildTargetIdentifier(baseUri.resolve("target3").toString)
  val targetId4 = new BuildTargetIdentifier(baseUri.resolve("target4").toString)
  val targetId5 = new BuildTargetIdentifier(baseUri.resolve("target5").toString)

  private val languageIds = List("scala").asJava

  val target1 = new BuildTarget(
    targetId1,
    List(BuildTargetTag.LIBRARY).asJava,
    languageIds,
    Collections.emptyList(), {
      val capabilities = new BuildTargetCapabilities()
      capabilities.setCanCompile(true)
      capabilities
    }
  )

  val target2 = new BuildTarget(
    targetId2,
    List(BuildTargetTag.TEST).asJava,
    languageIds,
    List(targetId1).asJava, {
      val capabilities = new BuildTargetCapabilities()
      capabilities.setCanCompile(true)
      capabilities.setCanTest(true)
      capabilities
    }
  )
  val target3 = new BuildTarget(
    targetId3,
    List(BuildTargetTag.APPLICATION).asJava,
    languageIds,
    List(targetId1).asJava, {
      val capabilities = new BuildTargetCapabilities()
      capabilities.setCanCompile(true)
      capabilities.setCanRun(true)
      capabilities
    }
  )
  val target4 = new BuildTarget(
    targetId4,
    List(BuildTargetTag.APPLICATION).asJava,
    List("cpp").asJava,
    List.empty.asJava, {
      val capabilities = new BuildTargetCapabilities()
      capabilities.setCanCompile(true)
      capabilities.setCanRun(true)
      capabilities
    }
  )
  val target5 = new BuildTarget(
    targetId5,
    List(BuildTargetTag.APPLICATION).asJava,
    List("python").asJava,
    List.empty.asJava, {
      val capabilities = new BuildTargetCapabilities()
      capabilities.setCanCompile(true)
      capabilities.setCanRun(true)
      capabilities
    }
  )

  private val client = TestClient(
    () => {
      val LocalMockServer(running, in, out) = MockServer.startMockServer(testDirectory.toFile)
      (out, in, () => running.cancel(true))
    },
    initializeBuildParams
  )

  test("Initialize connection followed by its shutdown") {
    client.testInitializeAndShutdown()
  }

  test("Initial imports") {
    client.testResolveProject()
  }

  test("Test server capabilities") {
    client.testTargetCapabilities()
  }

  test("Running batch tests") {
    client.wrapTest(session => {
      client
        .testResolveProject(session, false, false)
        .map(_ => client.targetsCompileSuccessfully(session))
        .flatMap(_ => client.cleanCacheSuccessfully(session))
    })
  }

  test("Test Compile of all targets") {
    client.testTargetsCompileSuccessfully(true)
  }

  test("Clean cache") {
    client.testCleanCacheSuccessfully()
  }

  test("Run Targets") {
    client.testTargetsRunSuccessfully(
      Lists.newArrayList(
        target3
      )
    )
  }

  test("Run Tests") {
    client.testTargetsTestSuccessfully(
      Lists.newArrayList(
        target2
      )
    )
  }

  test("Run javacOptions") {
    val classDirectory = "file:" + testDirectory.resolve("out").toString
    val javacOptionsItems = List(
      new JavacOptionsItem(
        targetId1,
        Collections.emptyList(),
        List("guava.jar").asJava,
        classDirectory
      ),
      new JavacOptionsItem(
        targetId2,
        Collections.emptyList(),
        List("guava.jar").asJava,
        classDirectory
      ),
      new JavacOptionsItem(
        targetId3,
        Collections.emptyList(),
        List("guava.jar").asJava,
        classDirectory
      )
    ).asJava

    client.testJavacOptions(
      new JavacOptionsParams(Collections.emptyList()),
      new JavacOptionsResult(javacOptionsItems)
    )
  }

  test("Run scalacOptions") {
    val classDirectory = "file:" + testDirectory.resolve("out").toString
    val scalacOptionsItems = List(
      new ScalacOptionsItem(
        targetId1,
        Collections.emptyList(),
        List("scala-library.jar").asJava,
        classDirectory
      ),
      new ScalacOptionsItem(
        targetId2,
        Collections.emptyList(),
        List("scala-library.jar").asJava,
        classDirectory
      ),
      new ScalacOptionsItem(
        targetId3,
        Collections.emptyList(),
        List("scala-library.jar").asJava,
        classDirectory
      )
    ).asJava

    client.testScalacOptions(
      new ScalacOptionsParams(Collections.emptyList()),
      new ScalacOptionsResult(scalacOptionsItems)
    )
  }

  test("Run cppOptions") {
    val copts = List("-Iexternal/gtest/include").asJava
    val defines = List("BOOST_FALLTHROUGH").asJava
    val linkopts = List("-pthread").asJava
    val item = new CppOptionsItem(targetId4, copts, defines, linkopts)
    val cppOptionsItem = List(item).asJava

    client.testCppOptions(
      new CppOptionsParams(Collections.emptyList()),
      new CppOptionsResult(cppOptionsItem)
    )
  }

  test("Run pythonOptions") {
    val interpreterOptions = List("-E").asJava
    val item = new PythonOptionsItem(targetId5, interpreterOptions)
    val pythonOptionsItem = List(item).asJava

    client.testPythonOptions(
      new PythonOptionsParams(Collections.emptyList()),
      new PythonOptionsResult(pythonOptionsItem)
    )
  }

  test("Run Scala Test Classes") {
    val classes1 = List("class1").asJava
    val classes2 = List("class2").asJava
    val testClassesItems = List(
      new ScalaTestClassesItem(targetId1, classes1),
      new ScalaTestClassesItem(targetId2, classes2)
    ).asJava
    val result = new ScalaTestClassesResult(testClassesItems)
    client.testScalaTestClasses(
      new ScalaTestClassesParams(Collections.emptyList()),
      result
    )
  }

  test("Scala Test Classes with less items should fail") {
    val classes1 = List("class1").asJava
    val testClassesItems = List(new ScalaTestClassesItem(targetId1, classes1)).asJava
    val result = new ScalaTestClassesResult(testClassesItems)
    Try(
      client.testScalaTestClasses(
        new ScalaTestClassesParams(Collections.emptyList()),
        result
      )
    ) match {
      case Failure(_) =>
      case Success(_) => fail("Test Classes should expect all item classes to be defined!")
    }
  }

  test("Run Scala Main Classes") {
    val classes1 = List(
      new ScalaMainClass("class1", List("arg1", "arg2").asJava, List("-deprecated").asJava)
    ).asJava
    val classes2 = List(
      new ScalaMainClass("class2", List("arg1", "arg2").asJava, List("-deprecated").asJava)
    ).asJava
    val mainClassesItems = List(
      new ScalaMainClassesItem(targetId1, classes1),
      new ScalaMainClassesItem(targetId1, classes2)
    ).asJava
    val result = new ScalaMainClassesResult(mainClassesItems)
    client.testScalaMainClasses(
      new ScalaMainClassesParams(Collections.emptyList()),
      result
    )
  }

  test("Scala Main Classes with less items should fail") {
    val classes1 = List(
      new ScalaMainClass("class1", List("arg1", "arg2").asJava, List("-deprecated").asJava)
    ).asJava
    val mainClassesItems = List(new ScalaMainClassesItem(targetId1, classes1)).asJava
    val result = new ScalaMainClassesResult(mainClassesItems)
    Try(
      client.testScalaMainClasses(
        new ScalaMainClassesParams(Collections.emptyList()),
        result
      )
    ) match {
      case Failure(_) =>
      case Success(_) => fail("Test Classes should expect all item classes to be defined!")
    }
  }

  test("Workspace Build Targets") {
    val targets = List(target1, target2, target3, target4, target5).asJava
    val javaHome = sys.props.get("java.home").map(p => Paths.get(p).toUri.toString)
    val javaVersion = sys.props.get("java.vm.specification.version")
    val jvmBuildTarget = new JvmBuildTarget()
    jvmBuildTarget.setJavaVersion(javaVersion.get)
    jvmBuildTarget.setJavaHome(javaHome.get)
    val scalaJars = List("scala-compiler.jar", "scala-reflect.jar", "scala-library.jar").asJava
    val scalaBuildTarget =
      new ScalaBuildTarget("org.scala-lang", "2.12.7", "2.12", ScalaPlatform.JVM, scalaJars)
    scalaBuildTarget.setJvmBuildTarget(jvmBuildTarget)
    val autoImports = List("task-key").asJava
    val children = List(targetId3).asJava
    val sbtBuildTarget =
      new SbtBuildTarget("1.0.0", autoImports, scalaBuildTarget, children)
    val cppBuildTarget =
      new CppBuildTarget()
    cppBuildTarget.setVersion("C++11")
    cppBuildTarget.setCompiler("gcc")
    cppBuildTarget.setCCompiler("/usr/bin/gcc")
    cppBuildTarget.setCppCompiler("/usr/bin/g++")
    val pythonBuildTarget =
      new PythonBuildTarget()
    pythonBuildTarget.setInterpreter("/usr/bin/python")
    pythonBuildTarget.setVersion("3.9")

    target1.setDisplayName("target 1")
    target1.setBaseDirectory(targetId1.getUri)
    target1.setDataKind(BuildTargetDataKind.SCALA)
    target1.setData(scalaBuildTarget)

    target2.setDisplayName("target 2")
    target2.setBaseDirectory(targetId2.getUri)
    target2.setDataKind(BuildTargetDataKind.JVM)
    target2.setData(jvmBuildTarget)

    target3.setDisplayName("target 3")
    target3.setBaseDirectory(targetId3.getUri)
    target3.setDataKind(BuildTargetDataKind.SBT)
    target3.setData(sbtBuildTarget)

    target4.setDisplayName("target 4")
    target4.setBaseDirectory(targetId4.getUri)
    target4.setDataKind(BuildTargetDataKind.CPP)
    target4.setData(cppBuildTarget)

    target5.setDisplayName("target 5")
    target5.setBaseDirectory(targetId5.getUri)
    target5.setDataKind(BuildTargetDataKind.PYTHON)
    target5.setData(pythonBuildTarget)

    val workspaceBuildTargetsResult = new WorkspaceBuildTargetsResult(targets)
    client.testCompareWorkspaceTargetsResults(workspaceBuildTargetsResult)
  }

  private def environmentItem(testing: Boolean) = {
    val classpath = List("scala-library.jar").asJava
    val jvmOptions = List("-Xms256m").asJava
    val environmentVariables = Map("A" -> "a", "TESTING" -> testing.toString).asJava
    val workdir = "/tmp"
    val item1 = new JvmEnvironmentItem(
      targetId1,
      classpath,
      jvmOptions,
      workdir,
      environmentVariables
    )
    val mainClass = new JvmMainClass("MainClass.java", List.empty[String].asJava)
    item1.setMainClasses(List(mainClass).asJava)
    List(item1).asJava
  }

  test("Jvm Run Environment") {
    client.testJvmRunEnvironment(
      new JvmRunEnvironmentParams(Collections.emptyList()),
      new JvmRunEnvironmentResult(environmentItem(testing = false))
    )
  }

  test("Jvm Test Environment") {
    client.testJvmTestEnvironment(
      new JvmTestEnvironmentParams(Collections.emptyList()),
      new JvmTestEnvironmentResult(environmentItem(testing = true))
    )
  }

}
