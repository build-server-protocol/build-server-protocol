package bsp.codegen

import java.nio.file.{Files, Paths}

object ProtocolVersionLoader {
  def version(): String = {
    try {
      val path = Paths.get("spec/src/main/resources/META-INF/smithy/bsp/version")
      Files.readString(path).trim
    } catch {
      case e: Throwable =>
        throw new RuntimeException(
          "Failed to load the protocol version, make sure that the working directory is set correctly",
          e
        )
    }
  }
}
