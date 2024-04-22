package bsp.codegen.bsp4j

import scala.jdk.CollectionConverters._
import bsp.codegen.ir.SmithyToIR
import org.eclipse.xtend.core.XtendInjectorSingleton
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler
import bsp.codegen.{CodegenFile, FilesGenerator, Loader}

import java.util
import java.io.File
import java.nio.file.{Files, Path}

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      println("Usage: bsp4j <output directory>")
      return
    }

    val model = Loader.INSTANCE.getModel
    val namespaces = Loader.INSTANCE.getNamespaces.asScala.toList
    val ir = new SmithyToIR(model)
    val definitions = namespaces.flatMap(ir.definitions)
    val version = Loader.INSTANCE.getProtocolVersion
    val renderer = new JavaRenderer("ch.epfl.scala.bsp4j", definitions, version)

    val codegenFiles: util.List[CodegenFile] = renderer.render().asJava

    val output = Path.of(args(0))

    val generatorOutput = output.resolve("generator")
    val xtendOutput = output.resolve("xtend")

    new FilesGenerator(generatorOutput, codegenFiles)
      .generateFiles()

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

    // Remove all non-Java files from xtendOutput
    Files.walk(xtendOutput).forEach { path =>
      if (path.toString.endsWith("._trace")) {
        Files.delete(path)
      }
    }

    // Delete the generatorOutput directory
    Files
      .walk(generatorOutput)
      .sorted(java.util.Comparator.reverseOrder())
      .forEach(Files.delete)

    // Move contents of xtendOutput one level up
    Files
      .walk(xtendOutput)
      .filter(Files.isRegularFile(_))
      .forEach { path =>
        val relativePath = xtendOutput.relativize(path)
        val targetPath = output.resolve(relativePath)
        Files.createDirectories(targetPath.getParent)
        Files.move(path, targetPath)
      }

    // Delete the xtendOutput directory
    Files
      .walk(xtendOutput)
      .sorted(java.util.Comparator.reverseOrder())
      .forEach(Files.delete)
  }
}
