package bsp.codegen

import java.nio.file.{Files, Paths}
import java.util.stream.Collectors
import scala.collection.JavaConverters.collectionAsScalaIterableConverter

object ExtensionLoader {
  def loadExtensions(): List[String] = {
    try {
      val extensionsPath = Paths.get("spec/src/main/resources/META-INF/smithy/bsp/extensions")
      val extensions = Files
        .list(extensionsPath)
        .collect(Collectors.toList[java.nio.file.Path])
        .asScala
        .map(_.getFileName.toString.split("\\.").head)
        .toList
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
