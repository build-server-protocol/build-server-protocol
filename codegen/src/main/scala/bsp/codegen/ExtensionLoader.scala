package bsp.codegen

import java.nio.file.{Files, Paths}
import scala.jdk.StreamConverters.StreamHasToScala

object ExtensionLoader {
  def loadExtensions(): List[String] = {
    try {
      val extensionsPath = Paths.get("spec/src/main/resources/META-INF/smithy/bsp/extensions")
      val extensions = Files
        .list(extensionsPath)
        .toScala(List)
        .map(_.getFileName.toString.split("\\.").head)
      extensions
    } catch {
      case e: Throwable =>
        throw new RuntimeException(
          "Failed to load extensions, make sure that the working directory is set correctly",
          e
        )
    }
  }

  def namespaces(): List[String] = {
    val extensions = loadExtensions()
    "bsp" :: extensions.map(ext => s"bsp.$ext")
  }
}
