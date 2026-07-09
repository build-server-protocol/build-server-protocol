package ch.epfl.scala.bsp.testkit.gen

import java.{lang, util}
import UtilGenerators._
import ch.epfl.scala.bsp4j._
import com.google.gson.{Gson, JsonElement}
import org.eclipse.lsp4j.jsonrpc.messages.Either.forLeft
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck._

import scala.collection.JavaConverters._

trait Bsp4jGenerators {

  lazy val genBspConnectionDetails: Gen[BspConnectionDetails] = for {
    name <- arbitrary[String]
    argv <- arbitrary[String].list
    version <- arbitrary[String]
    bspVersion <- arbitrary[String]
    languages <- genLanguageId.list
  } yield new BspConnectionDetails(name, argv, version, bspVersion, languages)

  lazy val genBuildClientCapabilities: Gen[BuildClientCapabilities] = for {
    languageIds <- genLanguageId.list
  } yield new BuildClientCapabilities(languageIds)

  val genBuildServerCapabilities: Gen[BuildServerCapabilities] = for {
    compileProvider <- genCompileProvider.nullable
    testProvider <- genTestProvider.nullable
    debugProvider <- genDebugProvider.nullable
    inverseSourcesProvider <- BoxedGen.boolean.nullable
    wrappedSourcesProvider <- BoxedGen.boolean.nullable
    dependencySourcesProvider <- BoxedGen.boolean.nullable
    dependencyModulesProvider <- BoxedGen.boolean.nullable
    resourcesProvider <- BoxedGen.boolean.nullable
    buildTargetChangedProvider <- BoxedGen.boolean.nullable
    canReload <- BoxedGen.boolean.nullable
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
    capabilities.setCanReload(canReload)
    capabilities.setDependencyModulesProvider(dependencyModulesProvider)
    capabilities
  }

  lazy val genBuildTarget: Gen[BuildTarget] = for {
    id <- genBuildTargetIdentifier
    tags <- genBuildTargetTag.list
    languageIds <- genLanguageId.list
    dependencies <- genBuildTargetIdentifier.list
    capabilities <- genBuildTargetCapabilities
    displayName <- arbitrary[String].nullable
    baseDirectory <- genFileUriString.nullable
  } yield {
    val buildTarget = new BuildTarget(id, tags, languageIds, dependencies, capabilities)
    buildTarget.setDisplayName(displayName)
    buildTarget.setBaseDirectory(baseDirectory)
    buildTarget.setDataKind(null) // TODO specific build target kind
    buildTarget.setData(null) // TODO build target data according to dataKind
    buildTarget
  }

  def genBuildTarget(dataKind: String, data: JsonElement): Gen[BuildTarget] = for {
    target <- genBuildTarget
  } yield {
    target.setDataKind(dataKind)
    target.setData(data)
    target
  }

  def genBuildTargetWithScala(implicit gson: Gson): Gen[BuildTarget] = for {
    scalaBuildTarget <- genScalaBuildTarget
    scalaJson = gson.toJsonTree(scalaBuildTarget)
    target <- genBuildTarget("scala", scalaJson)
  } yield target

  lazy val genBuildTargetCapabilities: Gen[BuildTargetCapabilities] = for {
    canCompile <- arbitrary[Boolean]
    canTest <- arbitrary[Boolean]
    canRun <- arbitrary[Boolean]
    canDebug <- arbitrary[Boolean]
  } yield {
    val capabilities = new BuildTargetCapabilities()
    capabilities.setCanCompile(canCompile)
    capabilities.setCanTest(canTest)
    capabilities.setCanRun(canRun)
    capabilities.setCanDebug(canDebug)
    capabilities
  }

  lazy val genBuildTargetEvent: Gen[BuildTargetEvent] = for {
    target <- genBuildTargetIdentifier
    kind <- genBuildTargetEventKind.nullable
  } yield {
    val event = new BuildTargetEvent(target)
    event.setKind(kind)
    event.setData(null) // TODO build target event data?
    event
  }

  lazy val genBuildTargetEventKind: Gen[BuildTargetEventKind] =
    Gen.oneOf(BuildTargetEventKind.values)

  lazy val genBuildTargetIdentifier: Gen[BuildTargetIdentifier] = for {
    uri <- genUri
  } yield new BuildTargetIdentifier(uri)

  lazy val genBuildTargetTag: Gen[String] = Gen.oneOf(
    BuildTargetTag.APPLICATION,
    BuildTargetTag.BENCHMARK,
    BuildTargetTag.INTEGRATION_TEST,
    BuildTargetTag.LIBRARY,
    BuildTargetTag.NO_IDE,
    BuildTargetTag.TEST,
    BuildTargetTag.MANUAL
  )

  lazy val genCleanCacheParams: Gen[CleanCacheParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new CleanCacheParams(targets)

  lazy val genCleanCacheResult: Gen[CleanCacheResult] = for {
    cleaned <- arbitrary[Boolean]
  } yield new CleanCacheResult(cleaned)

  lazy val genCompileParams: Gen[CompileParams] = for {
    targets <- genBuildTargetIdentifier.list
    arguments <- arbitrary[String].list.nullable
    originId <- arbitrary[String].nullable
  } yield {
    val params = new CompileParams(targets)
    params.setArguments(arguments)
    params.setOriginId(originId)
    params
  }

  lazy val genCompileProvider: Gen[CompileProvider] = for {
    languageIds <- genLanguageId.list
  } yield new CompileProvider(languageIds)

  lazy val genCompileReport: Gen[CompileReport] = for {
    target <- genBuildTargetIdentifier
    errors <- arbitrary[Int]
    warnings <- arbitrary[Int]
    time <- BoxedGen.long.nullable
    originId <- arbitrary[String].nullable
  } yield {
    val report = new CompileReport(target, errors, warnings)
    report.setTime(time)
    report.setOriginId(originId)
    report
  }

  lazy val genCompileResult: Gen[CompileResult] = for {
    statusCode <- genStatusCode
    originId <- arbitrary[String]
  } yield {
    val result = new CompileResult(statusCode)
    result.setOriginId(originId)
    result.setDataKind(null)
    result.setData(null)
    result
  }

  lazy val genCompileTask: Gen[CompileTask] = for {
    target <- genBuildTargetIdentifier
  } yield new CompileTask(target)

  lazy val genDependencySourcesItem: Gen[DependencySourcesItem] = for {
    target <- genBuildTargetIdentifier
    sources <- genFileUriString.list
  } yield new DependencySourcesItem(target, sources)

  lazy val genDependencySourcesParams: Gen[DependencySourcesParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new DependencySourcesParams(targets)

  lazy val genDependencySourcesResult: Gen[DependencySourcesResult] = for {
    items <- genDependencySourcesItem.list
  } yield new DependencySourcesResult(items)

  lazy val genDiagnostic: Gen[Diagnostic] = for {
    range <- genRange
    message <- arbitrary[String]
    severity <- genDiagnosticSeverity.nullable
    code <- arbitrary[String].nullable
    source <- arbitrary[String].nullable
    relatedInformation <- genDiagnosticRelatedInformation.list.nullable
  } yield {
    val diagnostic = new Diagnostic(range, message)
    diagnostic.setSeverity(severity)
    diagnostic.setCode(code)
    diagnostic.setSource(source)
    diagnostic.setRelatedInformation(relatedInformation)
    diagnostic
  }

  def genDiagnostic(dataKind: String, data: JsonElement): Gen[Diagnostic] = for {
    diagnostic <- genDiagnostic
  } yield {
    diagnostic.setDataKind(dataKind)
    diagnostic.setData(data)
    diagnostic
  }

  def genDiagnosticWithScala(implicit gson: Gson): Gen[Diagnostic] = for {
    scalaDiagnostic <- genScalaDiagnostic
    scalaJson = gson.toJsonTree(scalaDiagnostic)
    diagnostic <- genDiagnostic("scala", scalaJson)
  } yield diagnostic

  lazy val genDiagnosticRelatedInformation: Gen[DiagnosticRelatedInformation] = for {
    location <- genLocation
    message <- arbitrary[String]
  } yield new DiagnosticRelatedInformation(location, message)

  lazy val genDiagnosticSeverity: Gen[DiagnosticSeverity] = Gen.oneOf(DiagnosticSeverity.values())

  lazy val genScalaDiagnostic: Gen[ScalaDiagnostic] = for {
    actions <- genScalaAction.list
  } yield {
    val diagnostic = new ScalaDiagnostic()
    diagnostic.setActions(actions)
    diagnostic
  }

  lazy val genScalaAction: Gen[ScalaAction] = for {
    title <- arbitrary[String]
    description <- arbitrary[String].nullable
    edit <- genScalaWorkspaceEdit.nullable
  } yield {
    val action = new ScalaAction(title)
    action.setDescription(description)
    action.setEdit(edit)
    action
  }

  lazy val genScalaWorkspaceEdit: Gen[ScalaWorkspaceEdit] = for {
    changes <- genScalaTextEdit.list
  } yield new ScalaWorkspaceEdit(changes)

  lazy val genScalaTextEdit: Gen[ScalaTextEdit] = for {
    range <- genRange
    newText <- arbitrary[String]
  } yield new ScalaTextEdit(range, newText)

  lazy val genDidChangeBuildTarget: Gen[DidChangeBuildTarget] = for {
    events <- genBuildTargetEvent.list
  } yield new DidChangeBuildTarget(events)

  lazy val genInitializeBuildParams: Gen[InitializeBuildParams] = for {
    displayName <- arbitrary[String]
    version <- arbitrary[String]
    bspVersion <- arbitrary[String]
    rootUri <- genFileUriString
    capabilities <- genBuildClientCapabilities
  } yield {
    val params = new InitializeBuildParams(displayName, version, bspVersion, rootUri, capabilities)
    params.setData(null)
    params
  }

  lazy val genInitializeBuildResult: Gen[InitializeBuildResult] = for {
    displayName <- arbitrary[String]
    version <- arbitrary[String]
    bspVersion <- arbitrary[String]
    capabilities <- genBuildServerCapabilities
  } yield {
    val result = new InitializeBuildResult(displayName, version, bspVersion, capabilities)
    result.setData(null)
    result
  }

  lazy val genInverseSourcesParams: Gen[InverseSourcesParams] = for {
    textDocument <- genTextDocumentIdentifier
  } yield new InverseSourcesParams(textDocument)

  lazy val genInverseSourcesResult: Gen[InverseSourcesResult] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new InverseSourcesResult(targets)

  /** A language id supported by bsp protocol. */
  lazy val genLanguageId: Gen[String] = Gen.oneOf("scala", "java")

  lazy val genLocation: Gen[Location] = for {
    uri <- genFileUriString
    range <- genRange
  } yield new Location(uri, range)

  lazy val genLogMessageParams: Gen[LogMessageParams] = for {
    messageType <- genMessageType
    message <- arbitrary[String]
    task <- genTaskId.nullable
    originId <- arbitrary[String].nullable
  } yield {
    val params = new LogMessageParams(messageType, message)
    params.setTask(task)
    params.setOriginId(originId)
    params
  }

  lazy val genMessageType: Gen[MessageType] = Gen.oneOf(MessageType.values)

  lazy val genPosition: Gen[Position] = for {
    line <- arbitrary[Int]
    character <- arbitrary[Int]
  } yield new Position(line, character)

  lazy val genPublishDiagnosticsParams: Gen[PublishDiagnosticsParams] = for {
    textDocument <- genTextDocumentIdentifier
    buildTarget <- genBuildTargetIdentifier
    diagnostics <- genDiagnostic.list
    reset <- arbitrary[Boolean]
    originId <- arbitrary[String]
  } yield {
    val params = new PublishDiagnosticsParams(textDocument, buildTarget, diagnostics, reset)
    params.setOriginId(originId)
    params
  }

  lazy val genRange: Gen[Range] = for {
    start <- genPosition
    end <- genPosition
  } yield new Range(start, end)

  lazy val genResourcesItem: Gen[ResourcesItem] = for {
    target <- genBuildTargetIdentifier
    resources <- genFileUriString.list
  } yield new ResourcesItem(target, resources)

  lazy val genResourcesParams: Gen[ResourcesParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new ResourcesParams(targets)

  lazy val genResourcesResult: Gen[ResourcesResult] = for {
    items <- genResourcesItem.list
  } yield new ResourcesResult(items)

  lazy val genRunParams: Gen[RunParams] = for {
    target <- genBuildTargetIdentifier
    arguments <- arbitrary[String].list.nullable
  } yield {
    val runParams = new RunParams(target)
    runParams.setArguments(arguments)
    runParams.setDataKind(null)
    runParams.setData(null)
    runParams
  }

  lazy val genRunProvider: Gen[RunProvider] = for {
    languageIds <- genLanguageId.list
  } yield new RunProvider(languageIds)

  lazy val genRunResult: Gen[RunResult] = for {
    statusCode <- genStatusCode
    originId <- arbitrary[String].nullable
  } yield {
    val result = new RunResult(statusCode)
    result.setOriginId(originId)
    result
  }

  lazy val genJvmBuildTarget: Gen[JvmBuildTarget] = for {
    javaHome <- genFileUriString.nullable
    javaVersion <- arbitrary[String].nullable
  } yield {
    val buildTarget = new JvmBuildTarget()
    buildTarget.setJavaHome(javaHome)
    buildTarget.setJavaVersion(javaVersion)
    buildTarget
  }

  lazy val genSbtBuildTarget: Gen[SbtBuildTarget] = for {
    sbtVersion <- arbitrary[String]
    autoImports <- arbitrary[String].list
    scalaBuildTarget <- genScalaBuildTarget
    children <- genBuildTargetIdentifier.list
    parent <- genBuildTargetIdentifier.nullable
  } yield {
    val target = new SbtBuildTarget(sbtVersion, autoImports, scalaBuildTarget, children)
    target.setParent(parent)
    target
  }

  lazy val genScalaBuildTarget: Gen[ScalaBuildTarget] = for {
    scalaOrganization <- arbitrary[String]
    scalaVersion <- arbitrary[String]
    scalaBinaryVersion <- arbitrary[String]
    platform <- genScalaPlatform
    jars <- genFileUriString.list
    jvmBuildTarget <- genJvmBuildTarget.nullable
  } yield {
    val target =
      new ScalaBuildTarget(scalaOrganization, scalaVersion, scalaBinaryVersion, platform, jars)
    target.setJvmBuildTarget(jvmBuildTarget)
    target
  }

  lazy val genScalacOptionsItem: Gen[ScalacOptionsItem] = for {
    target <- genBuildTargetIdentifier
    options <- arbitrary[String].list
    classpath <- genFileUriString.list
    classDirectory <- genFileUriString
  } yield new ScalacOptionsItem(target, options, classpath, classDirectory)

  lazy val genScalacOptionsParams: Gen[ScalacOptionsParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new ScalacOptionsParams(targets)

  lazy val genScalacOptionsResult: Gen[ScalacOptionsResult] = for {
    items <- genScalacOptionsItem.list
  } yield new ScalacOptionsResult(items)

  lazy val genScalaMainClass: Gen[ScalaMainClass] = for {
    className <- genClassName
    arguments <- arbitrary[String].list
    jvmOptions <- arbitrary[String].list
    environmentVariables <- arbitrary[String].list
  } yield {
    val mainClass = new ScalaMainClass(className, arguments, jvmOptions)
    mainClass.setEnvironmentVariables(environmentVariables)
    mainClass
  }

  lazy val genScalaMainClassesItem: Gen[ScalaMainClassesItem] = for {
    target <- genBuildTargetIdentifier
    classes <- genScalaMainClass.list
  } yield new ScalaMainClassesItem(target, classes)

  lazy val genScalaMainClassesParams: Gen[ScalaMainClassesParams] = for {
    targets <- genBuildTargetIdentifier.list
    originId <- arbitrary[String].nullable
  } yield {
    val params = new ScalaMainClassesParams(targets)
    params.setOriginId(originId)
    params
  }

  lazy val genScalaMainClassesResult: Gen[ScalaMainClassesResult] = for {
    items <- genScalaMainClassesItem.list
  } yield new ScalaMainClassesResult(items)

  lazy val genScalaPlatform: Gen[ScalaPlatform] = Gen.oneOf(ScalaPlatform.values())

  lazy val genScalaTestClassesItem: Gen[ScalaTestClassesItem] = for {
    target <- genBuildTargetIdentifier
    classes <- genFQN.list
  } yield new ScalaTestClassesItem(target, classes)

  lazy val genScalaTestClassesParams: Gen[ScalaTestClassesParams] = for {
    targets <- genBuildTargetIdentifier.list
    originId <- arbitrary[String].nullable
  } yield {
    val params = new ScalaTestClassesParams(targets)
    params.setOriginId(originId)
    params
  }

  lazy val genScalaTestClassesResult: Gen[ScalaTestClassesResult] = for {
    items <- genScalaTestClassesItem.list
  } yield new ScalaTestClassesResult(items)

  lazy val genScalaTestParams: Gen[ScalaTestParams] = for {
    items <- genScalaTestClassesItem.list.nullable
    jvmOptions <- arbitrary[String].list.nullable
  } yield {
    val params = new ScalaTestParams()
    params.setTestClasses(items)
    params.setJvmOptions(jvmOptions)
    params
  }

  lazy val genShowMessageParams: Gen[ShowMessageParams] = for {
    messageType <- genMessageType
    message <- arbitrary[String]
    taskId <- genTaskId.nullable
    originId <- arbitrary[String].nullable
  } yield {
    val params = new ShowMessageParams(messageType, message)
    params.setTask(taskId)
    params.setOriginId(originId)
    params
  }

  lazy val genSourceItem: Gen[SourceItem] = for {
    uri <- genFileUriString
    kind <- genSourceItemKind
    generated <- arbitrary[Boolean]
  } yield new SourceItem(uri, kind, generated)

  lazy val genSourceItemKind: Gen[SourceItemKind] =
    Gen.oneOf(SourceItemKind.values)

  lazy val genSourcesItem: Gen[SourcesItem] = for {
    target <- genBuildTargetIdentifier
    sources <- genSourceItem.list
    roots <- genFileUriString.list.nullable
  } yield {
    val item = new SourcesItem(target, sources)
    item.setRoots(roots)
    item
  }

  lazy val genSourcesParams: Gen[SourcesParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new SourcesParams(targets)

  lazy val genSourcesResult: Gen[SourcesResult] = for {
    items <- genSourcesItem.list
  } yield new SourcesResult(items)

  lazy val genOutputPathItem: Gen[OutputPathItem] = for {
    uri <- genFileUriString
    kind <- genOutputPathItemKind
  } yield new OutputPathItem(uri, kind)

  lazy val genOutputPathItemKind: Gen[OutputPathItemKind] =
    Gen.oneOf(OutputPathItemKind.values)

  lazy val genOutputPathsItem: Gen[OutputPathsItem] = for {
    target <- genBuildTargetIdentifier
    outputPaths <- genOutputPathItem.list
  } yield {
    new OutputPathsItem(target, outputPaths)
  }

  lazy val genOutputPathsParams: Gen[OutputPathsParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new OutputPathsParams(targets)

  lazy val genOutputPathsResult: Gen[OutputPathsResult] = for {
    items <- genOutputPathsItem.list
  } yield new OutputPathsResult(items)

  lazy val genStatusCode: Gen[StatusCode] = Gen.oneOf(StatusCode.values)

  lazy val genTaskFinishDataKind: Gen[String] = Gen.oneOf(
    TaskFinishDataKind.COMPILE_REPORT,
    TaskFinishDataKind.TEST_FINISH,
    TaskFinishDataKind.TEST_REPORT
  )
  lazy val genTaskProgressDataKind: Gen[String] = Gen.oneOf(
    "unknown1",
    "unknown2",
    "unknown3"
  )
  lazy val genTaskStartDataKind: Gen[String] = Gen.oneOf(
    TaskStartDataKind.COMPILE_TASK,
    TaskStartDataKind.TEST_START,
    TaskStartDataKind.TEST_TASK
  )

  lazy val genTaskFinishParams: Gen[TaskFinishParams] = for {
    taskId <- genTaskId
    status <- genStatusCode
    eventTime <- BoxedGen.long.nullable
    message <- arbitrary[String].nullable
    dataKind <- genTaskFinishDataKind.nullable
  } yield {
    val params = new TaskFinishParams(taskId, status)
    params.setEventTime(eventTime)
    params.setMessage(message)
    params.setDataKind(dataKind)
    params.setData(null) // TODO data according to dataKind
    params
  }

  lazy val genTaskId: Gen[TaskId] = for {
    id <- arbitrary[String]
    parents <- arbitrary[String].list.nullable
  } yield {
    val taskId = new TaskId(id)
    taskId.setParents(parents)
    taskId
  }

  lazy val genTaskProgressParams: Gen[TaskProgressParams] = for {
    taskId <- genTaskId
    eventTime <- BoxedGen.long.nullable
    message <- arbitrary[String].nullable
    progress <- BoxedGen.long.nullable
    total <- BoxedGen.long.nullable
    unit <- arbitrary[String]
    dataKind <- genTaskProgressDataKind.nullable
  } yield {
    val params = new TaskProgressParams(taskId)
    params.setEventTime(eventTime)
    params.setMessage(message)
    params.setProgress(progress)
    params.setTotal(total)
    params.setUnit(unit)
    params.setDataKind(dataKind)
    params.setData(null) // TODO data according to dataKind
    params
  }

  lazy val genTaskStartParams: Gen[TaskStartParams] = for {
    taskId <- genTaskId
    eventTime <- BoxedGen.long.nullable
    message <- arbitrary[String].nullable
    dataKind <- genTaskStartDataKind.nullable
  } yield {
    val params = new TaskStartParams(taskId)
    params.setEventTime(eventTime)
    params.setMessage(message)
    params.setDataKind(dataKind)
    params.setData(null)
    params
  }

  lazy val genTestFinish: Gen[TestFinish] = for {
    displayName <- arbitrary[String]
    status <- genTestStatus
    location <- genLocation.nullable
    message <- arbitrary[String].nullable
  } yield {
    val testFinish = new TestFinish(displayName, status)
    testFinish.setDisplayName(displayName)
    testFinish.setLocation(location)
    testFinish.setMessage(message)
    testFinish.setDataKind(null)
    testFinish.setData(null)
    testFinish
  }

  lazy val genTestParams: Gen[TestParams] = for {
    targets <- genBuildTargetIdentifier.list
    arguments <- arbitrary[String].list.nullable
    originId <- arbitrary[String].nullable
  } yield {
    val params = new TestParams(targets)
    params.setArguments(arguments)
    params.setOriginId(originId)
    params.setDataKind(null)
    params.setData(null)
    params
  }

  lazy val genTestProvider: Gen[TestProvider] = for {
    languageIds <- genLanguageId.list
  } yield new TestProvider(languageIds)

  lazy val genDebugProvider: Gen[DebugProvider] = for {
    languageIds <- genLanguageId.list
  } yield new DebugProvider(languageIds)

  lazy val genTestReport: Gen[TestReport] = for {
    target <- genBuildTargetIdentifier
    passed <- arbitrary[Int]
    failed <- arbitrary[Int]
    ignored <- arbitrary[Int]
    cancelled <- arbitrary[Int]
    skipped <- arbitrary[Int]
    time <- BoxedGen.long.nullable
    originId <- arbitrary[String].nullable
  } yield {
    val report = new TestReport(target, passed, failed, ignored, cancelled, skipped)
    report.setTime(time)
    report.setOriginId(originId)
    report
  }

  lazy val genTestResult: Gen[TestResult] = for {
    statusCode <- genStatusCode
    originId <- arbitrary[String]
  } yield {
    val result = new TestResult(statusCode)
    result.setOriginId(originId)
    result.setDataKind(null)
    result.setData(null)
    result
  }

  lazy val genTestStart: Gen[TestStart] = for {
    displayName <- arbitrary[String]
    location <- genLocation.nullable
  } yield {
    val testStart = new TestStart(displayName)
    testStart.setLocation(location)
    testStart
  }

  lazy val genTestStatus: Gen[TestStatus] = Gen.oneOf(TestStatus.values)

  lazy val genTestTask: Gen[TestTask] = for {
    target <- genBuildTargetIdentifier
  } yield new TestTask(target)

  lazy val genTextDocumentIdentifier: Gen[TextDocumentIdentifier] = for {
    uri <- genFileUriString
  } yield new TextDocumentIdentifier(uri)

  lazy val genWorkspaceBuildTargetsResult: Gen[WorkspaceBuildTargetsResult] = for {
    targets <- genBuildTarget.list
  } yield new WorkspaceBuildTargetsResult(targets)

  lazy val genEnvironmentVariables = for {
    varName <- arbitrary[String]
    varVal <- arbitrary[String]
  } yield (varName, varVal)

  lazy val genJvmMainClass: Gen[JvmMainClass] = for {
    className <- arbitrary[String]
    arguments <- arbitrary[String].list
  } yield new JvmMainClass(className, arguments)

  lazy val genJvmEnvironmentItem: Gen[JvmEnvironmentItem] = for {
    target <- genBuildTargetIdentifier
    options <- arbitrary[String].list
    envVars <- genEnvironmentVariables.list
    classpath <- genFileUriString.list
    workdir <- genFileUriString
    mainClass <- genJvmMainClass.list.nullable
  } yield {
    val item = new JvmEnvironmentItem(
      target,
      options,
      classpath,
      workdir,
      envVars.asScala.toMap.asJava
    )
    item.setMainClasses(mainClass)
    item
  }

  lazy val genJvmTestEnvironmentParams: Gen[JvmTestEnvironmentParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new JvmTestEnvironmentParams(targets)

  lazy val genJvmTestEnvironmentResult: Gen[JvmTestEnvironmentResult] = for {
    items <- genJvmEnvironmentItem.list
  } yield new JvmTestEnvironmentResult(items)

  lazy val genJvmRunEnvironmentParams: Gen[JvmRunEnvironmentParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new JvmRunEnvironmentParams(targets)

  lazy val genJvmRunEnvironmentResult: Gen[JvmRunEnvironmentResult] = for {
    items <- genJvmEnvironmentItem.list
  } yield new JvmRunEnvironmentResult(items)

  lazy val genJavacOptionsItem: Gen[JavacOptionsItem] = for {
    target <- genBuildTargetIdentifier
    options <- arbitrary[String].list
    classpath <- genFileUriString.list
    classDirectory <- genFileUriString
  } yield new JavacOptionsItem(target, options, classpath, classDirectory)

  lazy val genJavacOptionsParams: Gen[JavacOptionsParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new JavacOptionsParams(targets)

  lazy val genJavacOptionsResult: Gen[JavacOptionsResult] = for {
    items <- genJavacOptionsItem.list
  } yield new JavacOptionsResult(items)

  lazy val genCppBuildTarget: Gen[CppBuildTarget] = for {
    version <- arbitrary[String].nullable
    compiler <- arbitrary[String].nullable
    cCompiler <- genFileUriString.nullable
    cppCompiler <- genFileUriString.nullable
  } yield {
    val cppBuildTarget = new CppBuildTarget()
    cppBuildTarget.setVersion(version)
    cppBuildTarget.setCompiler(compiler)
    cppBuildTarget.setCCompiler(cCompiler)
    cppBuildTarget.setCppCompiler(cppCompiler)
    cppBuildTarget
  }

  lazy val genCppOptionsItem: Gen[CppOptionsItem] = for {
    target <- genBuildTargetIdentifier
    copts <- arbitrary[String].list
    defines <- arbitrary[String].list
    linkopts <- arbitrary[String].list
    linkshared <- arbitrary[Boolean]
  } yield {
    val cppOptionsItem = new CppOptionsItem(target, copts, defines, linkopts)
    cppOptionsItem.setLinkshared(linkshared)
    cppOptionsItem
  }

  lazy val genCppOptionsParams: Gen[CppOptionsParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new CppOptionsParams(targets)

  lazy val genCppOptionsResult: Gen[CppOptionsResult] = for {
    items <- genCppOptionsItem.list
  } yield new CppOptionsResult(items)

  lazy val genPythonBuildTarget: Gen[PythonBuildTarget] = for {
    version <- arbitrary[String].nullable
    interpreter <- genFileUriString.nullable
  } yield {
    val pythonBuildTarget = new PythonBuildTarget()
    pythonBuildTarget.setVersion(version)
    pythonBuildTarget.setInterpreter(interpreter)
    pythonBuildTarget
  }

  lazy val genPythonOptionsItem: Gen[PythonOptionsItem] = for {
    target <- genBuildTargetIdentifier
    interpreterOpts <- arbitrary[String].list
  } yield new PythonOptionsItem(target, interpreterOpts)

  lazy val genPythonOptionsParams: Gen[PythonOptionsParams] = for {
    targets <- genBuildTargetIdentifier.list
  } yield new PythonOptionsParams(targets)

  lazy val genPythonOptionsResult: Gen[PythonOptionsResult] = for {
    items <- genPythonOptionsItem.list
  } yield new PythonOptionsResult(items)

  lazy val genCancelRequestParams: Gen[CancelRequestParams] = for {
    id <- arbitrary[String]
  } yield new CancelRequestParams(forLeft(id))

  implicit class GenExt[T](gen: Gen[T]) {
    def optional: Gen[Option[T]] = Gen.option(gen)
    def nullable(implicit ev: Null <:< T): Gen[T] = Gen.option(gen).map(g => g.orNull)
    def list: Gen[util.List[T]] = Gen.listOf(gen).map(_.asJava)
  }

  private object BoxedGen {
    def boolean: Gen[lang.Boolean] = arbitrary[Boolean].map(Boolean.box)
    def int: Gen[lang.Integer] = arbitrary[Int].map(Int.box)
    def long: Gen[lang.Long] = arbitrary[Long].map(Long.box)
  }

}

object Bsp4jGenerators extends Bsp4jGenerators
