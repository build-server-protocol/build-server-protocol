package bsp.codegen

import java.nio.file.{Files, Paths}

object VersionLoader {
  def version(): String = {
    try {
      val path = Paths.get("spec/src/main/resources/META-INF/smithy/bsp/version")
      Files.readString(path).trim
    } catch {
      case e: Throwable =>
        throw new RuntimeException(
          "Failed to load extensions, make sure that the working directory is set correctly",
          e
        )
    }
  }
}
