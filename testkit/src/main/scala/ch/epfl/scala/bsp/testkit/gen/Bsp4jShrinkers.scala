package ch.epfl.scala.bsp.testkit.gen

import org.scalacheck.Shrink
import org.scalacheck.Shrink._
import ch.epfl.scala.bsp4j
import ch.epfl.scala.bsp4j._

import java.net.URI
import java.util.{List => JList}

import scala.collection.JavaConverters._

import UtilGenerators._

trait Bsp4jShrinkers extends UtilShrinkers {

  implicit def shrinkJavaList[T]: Shrink[JList[T]] =
    Shrink { list => shrink(list.asScala).map(_.asJava) }

  implicit def shrinkBspConnectionDetails: Shrink[BspConnectionDetails] = Shrink { details =>
    for {
      name <- shrink(details.getName)
      argv <- shrink(details.getArgv)
      version <- shrink(details.getVersion)
      bspVersion <- shrink(details.getBspVersion)
      languages <- shrink(details.getLanguages)
    } yield new BspConnectionDetails(name, argv, version, bspVersion, languages)
  }

  implicit def shrinkBuildClientCapabilities: Shrink[BuildClientCapabilities] = Shrink { a =>
    shrink(a.getLanguageIds).map(new BuildClientCapabilities(_))
  }

  implicit def shrinkBuildServerCapabilities: Shrink[BuildServerCapabilities] = Shrink {
    capabilities =>
      for {
        compileProvider <- shrink(capabilities.getCompileProvider)
        testProvider <- shrink(capabilities.getTestProvider)
        debugProvider <- shrink(capabilities.getDebugProvider)
        inverseSourcesProvider <- shrink(capabilities.getInverseSourcesProvider)
        wrappedSourcesProvider <- shrink(capabilities.getWrappedSourcesProvider)
        dependencySourcesProvider <- shrink(capabilities.getDependencySourcesProvider)
        dependencyModulesProvider <- shrink(capabilities.getDependencyModulesProvider)
        resourcesProvider <- shrink(capabilities.getResourcesProvider)
        buildTargetChangedProvider <- shrink(capabilities.getBuildTargetChangedProvider)
      } yield {
        val capabilities = new BuildServerCapabilities()
        capabilities.setCompileProvider(compileProvider)
        capabilities.setTestProvider(testProvider)
        capabilities.setDebugProvider(debugProvider)
        capabilities.setInverseSourcesProvider(inverseSourcesProvider)
        capabilities.setWrappedSourcesProvider(wrappedSourcesProvider)
        capabilities.setDependencySourcesProvider(dependencySourcesProvider)
        capabilities.setResourcesProvider(resourcesProvider)
        capabilities.setBuildTargetChangedProvider(buildTargetChangedProvider)
        capabilities.setDependencyModulesProvider(dependencyModulesProvider)
        capabilities
      }
  }

  implicit def shrinkBuildTarget(
      sl: Shrink[JList[String]],
      s2: Shrink[JList[BuildTargetIdentifier]],
      s3: Shrink[JList[BuildTargetCapabilities]],
      s4: Shrink[String]
  ): Shrink[BuildTarget] = Shrink { target =>
    for {
      id <- shrink(target.getId)
      tags <- shrink(target.getTags)
      languageIds <- shrink(target.getLanguageIds)
      dependencies <- shrink(target.getDependencies)
      capabilities <- shrink(target.getCapabilities)
      displayName <- shrink(target.getDisplayName)
      baseDirectory <- shrinkFileUriString.shrink(target.getBaseDirectory)
    } yield {
      val shrinkTarget = new BuildTarget(id, tags, languageIds, dependencies, capabilities)
      shrinkTarget.setDisplayName(displayName)
      shrinkTarget.setBaseDirectory(baseDirectory)
      shrinkTarget.setDataKind(target.getDataKind) // TODO shrink
      shrinkTarget.setData(target.getData) // TODO shrink
      shrinkTarget
    }
  }

  implicit def shrinkBuildTargetCapabilities: Shrink[BuildTargetCapabilities] = Shrink {
    capabilities =>
      for {
        canCompile <- shrink(capabilities.getCanCompile)
        canTest <- shrink(capabilities.getCanTest)
        canRun <- shrink(capabilities.getCanRun)
        canDebug <- shrink(capabilities.getCanDebug)
      } yield {
        val capabilities = new BuildTargetCapabilities()
        capabilities.setCanCompile(canCompile)
        capabilities.setCanTest(canTest)
        capabilities.setCanRun(canRun)
        capabilities.setCanDebug(canDebug)
        capabilities
      }
  }

  implicit def shrinkBuildTargetEvent: Shrink[BuildTargetEvent] = Shrink { event =>
    for {
      target <- shrink(event.getTarget)
    } yield {
      val event = new BuildTargetEvent(target)
      event.setKind(event.getKind)
      event.setData(event.getData) // TODO shrink build target event data
      event
    }
  }
  implicit def shrinkBuildTargetIdentifier: Shrink[BuildTargetIdentifier] = Shrink { id =>
    val uri = new URI(id.getUri)
    shrink(uri).map(u => new BuildTargetIdentifier(u.toString))
  }

  implicit def shrinkCleanCacheParams: Shrink[CleanCacheParams] = Shrink { a =>
    shrink(a.getTargets).map(new CleanCacheParams(_))
  }

  implicit def shrinkCleanCacheResult: Shrink[CleanCacheResult] = Shrink { a =>
    for {
      cleaned <- shrink(a.getCleaned)
    } yield new CleanCacheResult(cleaned)
  }

  implicit def shrinkCompileParams: Shrink[CompileParams] = Shrink { params =>
    for {
      targets <- shrink(params.getTargets)
      arguments <- shrink(params.getArguments)
      originId <- shrink(params.getOriginId)
    } yield {
      val params = new CompileParams(targets)
      params.setArguments(arguments)
      params.setOriginId(originId)
      params
    }
  }

  implicit def shrinkCompileProvider: Shrink[CompileProvider] = Shrink { a =>
    shrink(a.getLanguageIds).map(new CompileProvider(_))
  }

  implicit def shrinkCompileReport(
      s1: Shrink[Int],
      s2: Shrink[Long],
      s3: Shrink[String]
  ): Shrink[CompileReport] = Shrink { report =>
    for {
      target <- shrink(report.getTarget)
      errors <- shrink(report.getErrors)
      warnings <- shrink(report.getWarnings)
      time <- shrink(report.getTime)
      originId <- shrink(report.getOriginId)
    } yield {
      val report = new CompileReport(target, errors, warnings)
      report.setTime(time)
      report.setOriginId(originId)
      report
    }
  }

  implicit def shrinkCompileResult: Shrink[CompileResult] = Shrink { a =>
    for {
      originId <- shrink(a.getOriginId)
    } yield {
      val result = new CompileResult(a.getStatusCode)
      result.setOriginId(originId)
      result.setDataKind(a.getDataKind)
      result.setData(a.getData)
      result
    }
  }

  implicit def shrinkCompileTask: Shrink[CompileTask] = Shrink { a =>
    shrink(a.getTarget).map(new CompileTask(_))
  }

  implicit def shrinkDependencySourcesItem: Shrink[DependencySourcesItem] = Shrink { item =>
    for {
      target <- shrink(item.getTarget)
      sources <- shrink(item.getSources)
    } yield new DependencySourcesItem(target, sources)
  }

  implicit def shrinkDependencySourcesParams: Shrink[DependencySourcesParams] = Shrink { a =>
    shrink(a.getTargets).map(new DependencySourcesParams(_))
  }

  implicit def shrinkDependencySourcesResult: Shrink[DependencySourcesResult] = Shrink { a =>
    shrink(a.getItems).map(new DependencySourcesResult(_))
  }

  implicit def shrinkDiagnostic: Shrink[Diagnostic] = Shrink { a =>
    for {
      range <- shrink(a.getRange)
      message <- shrink(a.getMessage)
      source <- shrink(a.getSource)
      relatedInformation <- shrink(a.getRelatedInformation)
    } yield {
      val diagnostic = new Diagnostic(range, message)
      diagnostic.setSeverity(a.getSeverity)
      diagnostic.setCode(a.getCode)
      diagnostic.setSource(source)
      diagnostic.setRelatedInformation(relatedInformation)
      diagnostic
    }
  }

  implicit def shrinkDiagnosticRelatedInformation: Shrink[DiagnosticRelatedInformation] = Shrink {
    a =>
      for {
        location <- shrink(a.getLocation)
        message <- shrink(a.getMessage)
      } yield new DiagnosticRelatedInformation(location, message)
  }

  implicit def shrinkDidChangeBuildTarget: Shrink[DidChangeBuildTarget] = Shrink { a =>
    shrinkJavaList[BuildTargetEvent].shrink(a.getChanges).map(new DidChangeBuildTarget(_))
  }

  implicit def shrinkInitializeBuildParams: Shrink[InitializeBuildParams] = Shrink { a =>
    for {
      displayName <- shrink(a.getDisplayName)
      version <- shrink(a.getVersion)
      bspVersion <- shrink(a.getBspVersion)
      rootUri <- shrink(a.getRootUri)
      capabilities <- shrink(a.getCapabilities)
    } yield {
      val params =
        new InitializeBuildParams(displayName, version, bspVersion, rootUri, capabilities)
      params.setData(a.getData)
      params
    }
  }

  implicit def shrinkInitializeBuildResult: Shrink[InitializeBuildResult] = Shrink { a =>
    for {
      displayName <- shrink(a.getDisplayName)
      version <- shrink(a.getVersion)
      bspVersion <- shrink(a.getBspVersion)
      capabilities <- shrink(a.getCapabilities)
    } yield {
      val result = new InitializeBuildResult(displayName, version, bspVersion, capabilities)
      result.setData(a.getData)
      result
    }
  }

  implicit def shrinkInverseSourcesParams: Shrink[InverseSourcesParams] = Shrink { a =>
    shrink(a.getTextDocument).map(new InverseSourcesParams(_))
  }

  implicit def shrinkInverseSourcesResult: Shrink[InverseSourcesResult] = Shrink { a =>
    shrink(a.getTargets).map(new InverseSourcesResult(_))
  }

  implicit def shrinkLocation: Shrink[Location] = Shrink { a =>
    for {
      uri <- shrinkFileUriString.shrink(a.getUri)
      range <- shrink(a.getRange)
    } yield new Location(uri, range)
  }

  implicit def shrinkLogMessageParams: Shrink[LogMessageParams] = Shrink { a =>
    for {
      message <- shrink(a.getMessage)
      task <- shrink(a.getTask)
      originId <- shrink(a.getOriginId)
    } yield {
      val params = new LogMessageParams(a.getType, message)
      params.setTask(task)
      params.setOriginId(originId)
      params
    }
  }

  implicit def shrinkPosition: Shrink[Position] = Shrink { a =>
    for {
      line <- shrink(a.getLine)
      character <- shrink(a.getCharacter)
    } yield new Position(line, character)
  }

  implicit def shrinkPublishDiagnosticsParams: Shrink[PublishDiagnosticsParams] = Shrink { a =>
    for {
      textDocument <- shrink(a.getTextDocument)
      buildTarget <- shrink(a.getBuildTarget)
      diagnostics <- shrink(a.getDiagnostics)
      reset <- shrink(a.getReset)
      originId <- shrink(a.getOriginId)
    } yield {
      val params = new PublishDiagnosticsParams(textDocument, buildTarget, diagnostics, reset)
      params.setOriginId(originId)
      params
    }
  }

  implicit def shrinkRange: Shrink[bsp4j.Range] = Shrink { a =>
    for {
      start <- shrink(a.getStart)
      end <- shrink(a.getEnd)
    } yield new Range(start, end)
  }

  implicit def shrinkResourcesItem: Shrink[ResourcesItem] = Shrink { a =>
    for {
      target <- shrink(a.getTarget)
      resources <- shrink(a.getResources)
    } yield new ResourcesItem(target, resources)
  }

  implicit def shrinkResourcesParams: Shrink[ResourcesParams] = Shrink { a =>
    shrink(a.getTargets).map(new ResourcesParams(_))
  }

  implicit def shrinkResourcesResult: Shrink[ResourcesResult] = Shrink { a =>
    shrink(a.getItems).map(new ResourcesResult(_))
  }

  implicit def shrinkRunParams: Shrink[RunParams] = Shrink { a =>
    for {
      target <- shrink(a.getTarget)
      arguments <- shrink(a.getArguments)
    } yield {
      val runParams = new RunParams(target)
      runParams.setArguments(arguments)
      runParams.setDataKind(a.getDataKind)
      runParams.setData(a.getData)
      runParams
    }
  }

  implicit def shrinkRunProvider: Shrink[RunProvider] = Shrink { a =>
    shrink(a.getLanguageIds).map(new RunProvider(_))
  }

  implicit def shrinkRunResult: Shrink[RunResult] = Shrink { a =>
    for {
      originId <- shrink(a.getOriginId)
    } yield {
      val result = new RunResult(a.getStatusCode)
      result.setOriginId(originId)
      result
    }
  }

  implicit def shrinkJvmBuildTarget: Shrink[JvmBuildTarget] = Shrink { a =>
    for {
      javaHome <- shrink(a.getJavaHome)
      javaVersion <- shrink(a.getJavaVersion)
    } yield {
      val target = new JvmBuildTarget()
      target.setJavaHome(javaHome)
      target.setJavaVersion(javaVersion)
      target
    }
  }

  implicit def shrinkSbtBuildTarget: Shrink[SbtBuildTarget] = Shrink { a =>
    for {
      sbtVersion <- shrink(a.getSbtVersion)
      autoImports <- shrink(a.getAutoImports)
      scalaBuildTarget <- shrink(a.getScalaBuildTarget)
      children <- shrink(a.getChildren)
      parent <- shrink(a.getParent)
    } yield {
      val target = new SbtBuildTarget(sbtVersion, autoImports, scalaBuildTarget, children)
      target.setParent(parent)
      target
    }
  }

  implicit def shrinkScalaBuildTarget: Shrink[ScalaBuildTarget] = Shrink { a =>
    for {
      scalaOrganization <- shrink(a.getScalaOrganization)
      scalaVersion <- shrink(a.getScalaVersion)
      scalaBinaryVersion <- shrink(a.getScalaBinaryVersion)
      platform <- shrink(a.getPlatform)
      jars <- shrink(a.getJars)
      jvmBuildTarget <- shrink(a.getJvmBuildTarget)
    } yield {
      val target =
        new ScalaBuildTarget(scalaOrganization, scalaVersion, scalaBinaryVersion, platform, jars)
      target.setJvmBuildTarget(jvmBuildTarget)
      target
    }
  }

  implicit def shrinkScalacOptionsItem: Shrink[ScalacOptionsItem] = Shrink { a =>
    for {
      target <- shrink(a.getTarget)
      options <- shrink(a.getOptions)
      classpath <- shrink(a.getClasspath)
      classDirectory <- shrink(a.getClassDirectory)
    } yield new ScalacOptionsItem(target, options, classpath, classDirectory)
  }

  implicit def shrinkScalacOptionsParams: Shrink[ScalacOptionsParams] = Shrink { a =>
    shrink(a.getTargets).map(new ScalacOptionsParams(_))
  }

  implicit def shrinkScalacOptionsResult: Shrink[ScalacOptionsResult] = Shrink { a =>
    shrink(a.getItems).map(new ScalacOptionsResult(_))
  }

  implicit def shrinkScalaMainClass: Shrink[ScalaMainClass] = Shrink { a =>
    for {
      className <- shrink(a.getClassName)
      arguments <- shrink(a.getArguments)
      jvmOptions <- shrink(a.getJvmOptions)
      environmentVariables <- shrink(a.getEnvironmentVariables)
    } yield {
      val mainClass = new ScalaMainClass(className, arguments, jvmOptions)
      mainClass.setEnvironmentVariables(environmentVariables)
      mainClass
    }
  }

  implicit def shrinkScalaMainClassesItem: Shrink[ScalaMainClassesItem] = Shrink { a =>
    for {
      target <- shrink(a.getTarget)
      classes <- shrink(a.getClasses)
    } yield new ScalaMainClassesItem(target, classes)
  }

  implicit def shrinkScalaMainClassesParams: Shrink[ScalaMainClassesParams] = Shrink { a =>
    for {
      targets <- shrink(a.getTargets)
      originId <- shrink(a.getOriginId)
    } yield {
      val params = new ScalaMainClassesParams(targets)
      params.setOriginId(originId)
      params
    }
  }

  implicit def shrinkScalaMainClassesResult: Shrink[ScalaMainClassesResult] = Shrink { a =>
    shrink(a.getItems).map(new ScalaMainClassesResult(_))
  }

  implicit def shrinkScalaTestClassesItem: Shrink[ScalaTestClassesItem] = Shrink { a =>
    for {
      target <- shrink(a.getTarget)
      classes <- shrink(a.getClasses)
    } yield new ScalaTestClassesItem(target, classes)
  }

  implicit def shrinkScalaTestClassesParams: Shrink[ScalaTestClassesParams] = Shrink { a =>
    for {
      targets <- shrink(a.getTargets)
      originId <- shrink(a.getOriginId)
    } yield {
      val params = new ScalaTestClassesParams(targets)
      params.setOriginId(originId)
      params
    }
  }

  implicit def shrinkScalaTestClassesResult: Shrink[ScalaTestClassesResult] = Shrink { a =>
    shrink(a.getItems).map(new ScalaTestClassesResult(_))
  }

  implicit def shrinkScalaTestParams: Shrink[ScalaTestParams] = Shrink { a =>
    for {
      items <- shrink(a.getTestClasses)
      jvmOptions <- shrink(a.getJvmOptions)
    } yield {
      val params = new ScalaTestParams()
      params.setTestClasses(items)
      params.setJvmOptions(jvmOptions)
      params
    }
  }

  implicit def shrinkShowMessageParams: Shrink[ShowMessageParams] = Shrink { a =>
    for {
      message <- shrink(a.getMessage)
      taskId <- shrink(a.getTask)
      originId <- shrink(a.getOriginId)
    } yield {
      val params = new ShowMessageParams(a.getType, message)
      params.setTask(taskId)
      params.setOriginId(originId)
      params
    }
  }

  implicit def shrinkSourceItem: Shrink[SourceItem] = Shrink { a =>
    for {
      uri <- shrink(a.getUri)
      generated <- shrink(a.getGenerated)
    } yield new SourceItem(uri, a.getKind, generated)
  }

  implicit def shrinkSourcesItem: Shrink[SourcesItem] = Shrink { a =>
    for {
      target <- shrink(a.getTarget)
      sources <- shrink(a.getSources)
    } yield new SourcesItem(target, sources)
  }

  implicit def shrinkSourcesParams: Shrink[SourcesParams] = Shrink { a =>
    shrink(a.getTargets).map(new SourcesParams(_))
  }

  implicit def shrinkSourcesResult: Shrink[SourcesResult] = Shrink { a =>
    shrink(a.getItems).map(new SourcesResult(_))
  }

  implicit def shrinkOutputPathItem: Shrink[OutputPathItem] = Shrink { a =>
    for {
      uri <- shrink(a.getUri)
    } yield new OutputPathItem(uri, a.getKind)
  }

  implicit def shrinkOutputPathsItem: Shrink[OutputPathsItem] = Shrink { a =>
    for {
      target <- shrink(a.getTarget)
      outputPaths <- shrink(a.getOutputPaths)
    } yield new OutputPathsItem(target, outputPaths)
  }

  implicit def shrinkOutputPathsParams: Shrink[OutputPathsParams] = Shrink { a =>
    shrink(a.getTargets).map(new OutputPathsParams(_))
  }

  implicit def shrinkOutputPathsResult: Shrink[OutputPathsResult] = Shrink { a =>
    shrink(a.getItems).map(new OutputPathsResult(_))
  }

  implicit def shrinkTaskFinishParams: Shrink[TaskFinishParams] = Shrink { a =>
    for {
      taskId <- shrink(a.getTaskId)
      eventTime <- shrink(a.getEventTime)
      message <- shrink(a.getMessage)
    } yield {
      val params = new TaskFinishParams(taskId, a.getStatus)
      params.setEventTime(eventTime)
      params.setMessage(message)
      params.setDataKind(a.getDataKind)
      params.setData(a.getData)
      params
    }
  }

  implicit def shrinkTaskId: Shrink[TaskId] = Shrink { a =>
    for {
      id <- shrink(a.getId)
      parents <- shrink(a.getParents)
    } yield {
      val taskId = new TaskId(id)
      taskId.setParents(parents)
      taskId
    }
  }

  implicit def shrinkTaskProgressParams: Shrink[TaskProgressParams] = Shrink { a =>
    for {
      taskId <- shrink(a.getTaskId)
      eventTime <- shrink(a.getEventTime)
      message <- shrink(a.getMessage)
      progress <- shrink(a.getProgress)
      total <- shrink(a.getTotal)
      unit <- shrink(a.getUnit)
    } yield {
      val params = new TaskProgressParams(taskId)
      params.setEventTime(eventTime)
      params.setMessage(message)
      params.setProgress(progress)
      params.setTotal(total)
      params.setUnit(unit)
      params.setDataKind(a.getDataKind)
      params.setData(a.getData)
      params
    }
  }

  implicit def shrinkTaskStartParams: Shrink[TaskStartParams] = Shrink { a =>
    for {
      taskId <- shrink(a.getTaskId)
      eventTime <- shrink(a.getEventTime)
      message <- shrink(a.getMessage)
    } yield {
      val params = new TaskStartParams(taskId)
      params.setEventTime(eventTime)
      params.setMessage(message)
      params.setDataKind(a.getDataKind)
      params.setData(a.getData)
      params
    }
  }

  implicit def shrinkTestFinish: Shrink[TestFinish] = Shrink { a =>
    for {
      displayName <- shrink(a.getDisplayName)
      location <- shrink(a.getLocation)
      message <- shrink(a.getMessage)
    } yield {
      val testFinish = new TestFinish(displayName, a.getStatus)
      testFinish.setDisplayName(displayName)
      testFinish.setLocation(location)
      testFinish.setMessage(message)
      testFinish.setDataKind(a.getDataKind)
      testFinish.setData(a.getData)
      testFinish
    }
  }

  implicit def shrinkTestParams: Shrink[TestParams] = Shrink { a =>
    for {
      targets <- shrink(a.getTargets)
      arguments <- shrink(a.getArguments)
      originId <- shrink(a.getOriginId)
    } yield {
      val params = new TestParams(targets)
      params.setArguments(arguments)
      params.setOriginId(originId)
      params.setDataKind(a.getDataKind)
      params.setData(a.getData)
      params
    }
  }

  implicit def shrinkTestProvider: Shrink[TestProvider] = Shrink { a =>
    shrink(a.getLanguageIds).map(new TestProvider(_))
  }

  implicit def shrinkTestReport: Shrink[TestReport] = Shrink { a =>
    for {
      target <- shrink(a.getTarget)
      passed <- shrink(a.getPassed)
      failed <- shrink(a.getFailed)
      ignored <- shrink(a.getIgnored)
      cancelled <- shrink(a.getCancelled)
      skipped <- shrink(a.getSkipped)
      time <- shrink(a.getTime)
      originId <- shrink(a.getOriginId)
    } yield {
      val report = new TestReport(target, passed, failed, ignored, cancelled, skipped)
      report.setTime(time)
      report.setOriginId(originId)
      report
    }
  }

  implicit def shrinkTestResult: Shrink[TestResult] = Shrink { a =>
    for {
      originId <- shrink(a.getOriginId)
    } yield {
      val result = new TestResult(a.getStatusCode)
      result.setOriginId(originId)
      result.setDataKind(a.getDataKind)
      result.setData(a.getData)
      result
    }
  }

  implicit def shrinkTestStart: Shrink[TestStart] = Shrink { a =>
    for {
      displayName <- shrink(a.getDisplayName)
      location <- shrink(a.getLocation)
    } yield {
      val testStart = new TestStart(displayName)
      testStart.setLocation(location)
      testStart
    }
  }

  implicit def shrinkTestTask: Shrink[TestTask] = Shrink { a =>
    shrink(a.getTarget).map(new TestTask(_))
  }

  implicit def shrinkTextDocumentIdentifier: Shrink[TextDocumentIdentifier] = Shrink { a =>
    shrink(a.getUri).map(new TextDocumentIdentifier(_))
  }

  implicit def shrinkWorkspaceBuildTargetsResult: Shrink[WorkspaceBuildTargetsResult] = Shrink {
    a =>
      shrink(a.getTargets).map(new WorkspaceBuildTargetsResult(_))
  }

}

object Bsp4jShrinkers extends Bsp4jShrinkers
