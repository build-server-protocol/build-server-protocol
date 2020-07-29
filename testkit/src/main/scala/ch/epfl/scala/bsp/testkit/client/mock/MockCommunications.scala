package ch.epfl.scala.bsp.testkit.client.mock

import java.io.File
import java.util.concurrent.Future

import ch.epfl.scala.bsp.testkit.client.mock
import ch.epfl.scala.bsp4j.{BspConnectionDetails, BuildClientCapabilities, BuildServer, InitializeBuildParams}
import com.google.gson.{Gson, JsonArray, JsonObject}

import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Try}

private class MockCommunications(details: BspConnectionDetails, workspace: File, compilerOutput: File, capabilities: BuildClientCapabilities){
  private def createInitializeBuildParams(): InitializeBuildParams = {
    val dataJson = new JsonObject
    dataJson.addProperty("clientClassesRootDir", compilerOutput.toURI.toString)
    dataJson.add("supportedScalaVersions", new JsonArray())

    val initializeBuildParams = new InitializeBuildParams("Mock-Client","0.0" , "2.0", workspace.toURI.toString, capabilities)
    initializeBuildParams.setData(dataJson)

    initializeBuildParams
  }

  private def connect(): MockSession = {
    val process =
      new ProcessBuilder(details.getArgv)
        .directory(workspace)
        .start()


    val cleanup = () => process.destroy()
    mock.MockSession(
      process,
      workspace.toURI,
      compilerOutput.toURI,
      createInitializeBuildParams(),
      cleanup
    )
  }
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

    files.map {file =>
      if(file.canRead)
        Try(gson.fromJson(Source.fromFile(file).bufferedReader(), classOf[BspConnectionDetails]))
      else Failure(BspConfigurationError(s"Unreadable configuration file found: ${file.getName}"))
    }
  }

  def prepareSession(workspace: File): (BuildClientCapabilities, List[Try[BspConnectionDetails]]) = {
    val capabilities = new BuildClientCapabilities(List("scala", "java").asJava)
    val workspaceConfigurationsFiles = workspaceConfigurations(workspace)
    val connectionFiles = readConnectionFiles(workspaceConfigurationsFiles)

    (capabilities, connectionFiles)
  }

  def connect(workspace: File, compilerOutput: File, capabilities: BuildClientCapabilities, details: BspConnectionDetails): MockSession = {
    val mockCommunications = new MockCommunications(details, workspace, compilerOutput, capabilities)
    mockCommunications.connect()
  }
}
