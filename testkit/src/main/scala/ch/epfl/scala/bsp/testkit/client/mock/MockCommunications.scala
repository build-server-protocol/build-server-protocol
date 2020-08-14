package ch.epfl.scala.bsp.testkit.client.mock

import java.io.File

import ch.epfl.scala.bsp.testkit.client.TestClient
import ch.epfl.scala.bsp4j.{BspConnectionDetails, BuildClientCapabilities, InitializeBuildParams}
import com.google.gson.{Gson, JsonObject}

import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Try}

private class MockCommunications(
    details: BspConnectionDetails,
    workspace: File,
    capabilities: BuildClientCapabilities,
    properties: Map[String, String],
    timeoutDuration: java.time.Duration
) {
  private def createInitializeBuildParams(): InitializeBuildParams = {
    val dataJson = new JsonObject
    properties.foreach(property => dataJson.addProperty(property._1, property._2))

    val initializeBuildParams =
      new InitializeBuildParams("Mock-Client", "0.0", "2.0", workspace.toURI.toString, capabilities)
    initializeBuildParams.setData(dataJson)

    initializeBuildParams
  }

  private def connect(): TestClient =
    TestClient(
      () => {
        val process =
          new ProcessBuilder(details.getArgv)
            .directory(workspace)
            .start()
        val cleanup = () => process.destroy()
        (process.getOutputStream, process.getInputStream, cleanup)
      },
      createInitializeBuildParams(),
      timeoutDuration
    )
}

object MockCommunications {
  private val BspWorkspaceConfigDirName = ".bsp"

  private def workspaceConfigurations(workspace: File): List[File] = {
    val bspDir = new File(workspace, BspWorkspaceConfigDirName)
    if (bspDir.isDirectory)
      bspDir.listFiles(file => file.getName.endsWith(".json")).toList
    else List.empty
  }

  private def readConnectionFiles(files: List[File]): List[Try[BspConnectionDetails]] = {
    val gson = new Gson()

    files.map { file =>
      if (file.canRead)
        Try(gson.fromJson(Source.fromFile(file).bufferedReader(), classOf[BspConnectionDetails]))
      else Failure(BspConfigurationError(s"Unreadable configuration file found: ${file.getName}"))
    }
  }

  def prepareSession(
      workspace: File
  ): (BuildClientCapabilities, List[Try[BspConnectionDetails]]) = {
    val capabilities = new BuildClientCapabilities(List("scala", "java").asJava)
    val workspaceConfigurationsFiles = workspaceConfigurations(workspace)
    val connectionFiles = readConnectionFiles(workspaceConfigurationsFiles)

    (capabilities, connectionFiles)
  }

  def connect(
      workspace: File,
      capabilities: BuildClientCapabilities,
      details: BspConnectionDetails,
      properties: Map[String, String],
      timeoutDuration: java.time.Duration
  ): TestClient = {
    val mockCommunications =
      new MockCommunications(details, workspace, capabilities, properties, timeoutDuration)
    mockCommunications.connect()
  }
}
