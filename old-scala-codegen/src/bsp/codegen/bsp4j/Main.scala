package bsp.codegen.bsp4j

import scala.jdk.CollectionConverters._
import bsp.codegen.ir.SmithyToIR
import org.eclipse.xtend.core.XtendInjectorSingleton
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler
import org.jetbrains.bsp.generators.{CodegenFile, FilesGenerator, Loader}

import java.util
import java.io.File
import java.nio.file.{Files, Path}

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("Usage: bsp4s <name> <output directory> <generator script path>")
      return
    }

    val model = Loader.INSTANCE.getModel
    val namespaces = Loader.INSTANCE.getNamespaces.asScala.toList
    val ir = new SmithyToIR(model)
    val definitions = namespaces.flatMap(ir.definitions)
    val version = Loader.INSTANCE.getProtocolVersion
    val renderer = new JavaRenderer("ch.epfl.scala.bsp4j", definitions, version)

    val codegenFiles: util.List[CodegenFile] = renderer.render().asJava

    val name = args(0)
    val output = Path.of(args(1))
    val generatorScript = new File(args(2))

    val additionalCommands = new util.ArrayList[String]()

    val generatorOutput = output.resolve("generator")
    val xtendOutput = output.resolve("xtend")

    new FilesGenerator(name, generatorOutput, generatorScript, codegenFiles, additionalCommands).generateFiles()

    val injector = XtendInjectorSingleton.INJECTOR
    val compiler: XtendBatchCompiler = injector.getInstance(classOf[XtendBatchCompiler])

    compiler.setSourcePath(generatorOutput.toString)
    compiler.setOutputPath(xtendOutput.toString)
    compiler.setUseCurrentClassLoaderAsParent(true)
    compiler.compile()

    // Copy each .java file from generatorOutput to xtendOutput
    Files.walk(generatorOutput).forEach { path =>
      if (path.toString.endsWith(".java")) {
        val relativePath = generatorOutput.relativize(path)
        val targetPath = xtendOutput.resolve(relativePath)
        Files.createDirectories(targetPath.getParent)
        Files.copy(path, targetPath)
      }
    }

    val codegenFilesAfterXtend = codegenFiles.asScala.map { file =>
      val path = file.getPath
      val pathAfterXtend = Path.of(path.toString.replace(".xtend", ".java"))
      val contentAfterXtend = "" // Does not matter, we only care about the path
      new CodegenFile(pathAfterXtend, contentAfterXtend)
    }.asJava

    new FilesGenerator(name, xtendOutput, generatorScript, codegenFilesAfterXtend, additionalCommands).writeScript()
  }
}

