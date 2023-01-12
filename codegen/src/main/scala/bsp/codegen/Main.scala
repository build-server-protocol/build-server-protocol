package bsp.codegen;

import com.monovore.decline._

object Main
    extends CommandApp(
      name = "bsp-codegen",
      header = "codegenerator for bsp4j",
      main = {
        val out = System.out
        System.setOut(System.err)
        MainArgs.opts.map { args =>
          val results = Codegen.run(args.outputDir)
          results.foreach(path => out.println(path))
        }
      }
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
