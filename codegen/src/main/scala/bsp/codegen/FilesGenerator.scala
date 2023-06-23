package bsp.codegen

import com.monovore.decline._

class FilesGenerator(run: os.Path => List[os.Path])
    extends CommandApp(
      name = "bsp-codegen",
      header = "code generator for bsp",
      main = {
        val out = System.out
        System.setOut(System.err)
        MainArgs.opts.map { args =>
          val generatedFiles = run(args.outputDir)
          generatedFiles.foreach(path => out.println(path))
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
