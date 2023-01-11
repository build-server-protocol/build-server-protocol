package bsp.codegen;

import com.monovore.decline._

object Main
    extends CommandApp(
      name = "bsp-codegen",
      header = "codegenerator for bsp4j",
      main = MainArgs.opts.map(println)
    )

case class MainArgs(
    outputDir: os.Path
)

object MainArgs {

  val outputDir = Opts
    .option[java.nio.file.Path](long = "output", help = "output directory", short = "o")
    .map(os.Path(_))
    .withDefault(os.pwd)

  val opts = outputDir.map(MainArgs(_))

}
