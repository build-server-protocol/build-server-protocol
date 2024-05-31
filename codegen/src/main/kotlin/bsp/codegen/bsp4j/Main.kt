package bsp.codegen.bsp4j

import bsp.codegen.common.FilesGenerator
import bsp.codegen.common.Loader
import bsp.codegen.common.ir.AbstractionLevel
import bsp.codegen.common.ir.IrConfig
import bsp.codegen.common.ir.SmithyToIr
import bsp.codegen.common.ir.TypeAliasing
import java.nio.file.Files
import kotlin.io.path.Path
import org.eclipse.xtend.core.XtendInjectorSingleton
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    if (args.size != 1) {
      println("Usage: bsp4j <output directory>")
      return
    }

    val output = Path(args[0])

    val config =
        IrConfig(
            strings = TypeAliasing.Pure,
            numbers = TypeAliasing.Pure,
            maps = TypeAliasing.Pure,
            dataWithKind = AbstractionLevel.AsType,
            openEnums = AbstractionLevel.AsType,
            untaggedUnions = AbstractionLevel.AsType,
        )

    val model = Loader.model
    val namespaces = Loader.namespaces
    val ir = SmithyToIr(model, config)
    val definitions = namespaces.flatMap { ir.definitions(it) }
    val version = Loader.protocolVersion
    val renderer = JavaRenderer("ch.epfl.scala.bsp4j", definitions, version)

    val generatorOutput = output.resolve("generator")
    val xtendOutput = output.resolve("xtend")

    val codegenFiles = renderer.render()
    val generator = FilesGenerator(generatorOutput, codegenFiles)
    generator.generateFiles()

    val injector = XtendInjectorSingleton.INJECTOR
    val compiler = injector.getInstance(XtendBatchCompiler::class.java)

    compiler.setSourcePath(generatorOutput.toString())
    compiler.setOutputPath(xtendOutput.toString())
    compiler.setUseCurrentClassLoaderAsParent(true)
    compiler.compile()

    // Copy each .java file from generatorOutput to xtendOutput
    Files.walk(generatorOutput).forEach { path ->
      if (path.toString().endsWith(".java")) {
        val relativePath = generatorOutput.relativize(path)
        val targetPath = xtendOutput.resolve(relativePath)
        Files.createDirectories(targetPath.parent)
        Files.copy(path, targetPath)
      }
    }

    // Remove all non-Java files from xtendOutput
    Files.walk(xtendOutput).forEach { path ->
      if (path.toString().endsWith("._trace")) {
        Files.delete(path)
      }
    }

    // Delete the generatorOutput directory
    Files.walk(generatorOutput).sorted(java.util.Comparator.reverseOrder()).forEach(Files::delete)

    // Move contents of xtendOutput one level up
    Files.walk(xtendOutput)
        .filter { Files.isRegularFile(it) }
        .forEach { path ->
          val relativePath = xtendOutput.relativize(path)
          val targetPath = output.resolve(relativePath)
          Files.createDirectories(targetPath.parent)
          Files.move(path, targetPath)
        }

    // Delete the xtendOutput directory
    Files.walk(xtendOutput).sorted(java.util.Comparator.reverseOrder()).forEach(Files::delete)
  }
}
