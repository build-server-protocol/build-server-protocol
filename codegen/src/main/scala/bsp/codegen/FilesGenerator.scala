package bsp.codegen

import com.monovore.decline._

class FilesGenerator(codegenFiles: List[CodegenFile])
    extends CommandApp(
      name = "bsp-codegen",
      header = "code generator for bsp",
      main = {
        val out = System.out
        System.setOut(System.err)

        MainArgs.opts.map { args =>
          codegenFiles.map { file =>
            val fullPath = args.outputDir / file.path
            os.write.over(fullPath, file.contents, createFolders = true)
            fullPath
          }
          codegenFiles.foreach(file => out.println(file.path))
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
