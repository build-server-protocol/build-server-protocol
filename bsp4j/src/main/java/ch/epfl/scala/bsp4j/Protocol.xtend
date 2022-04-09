package ch.epfl.scala.bsp4j

import java.util.List
import com.google.gson.annotations.JsonAdapter
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class TextDocumentIdentifier {

  @NonNull String uri

  new (@NonNull String uri) {
    this.uri = uri
  }
}

@JsonRpcData
class BuildTargetIdentifier {

  @NonNull String uri

  new (@NonNull String uri) {
    this.uri = uri
  }
}

@JsonRpcData
class BuildTargetCapabilities {

  @NonNull Boolean canCompile
  @NonNull Boolean canTest
  @NonNull Boolean canRun
  @NonNull Boolean canDebug

  new () {
    this.canCompile = Boolean.FALSE
    this.canTest = Boolean.FALSE
    this.canRun = Boolean.FALSE
    this.canDebug = Boolean.FALSE
  }

  new (@NonNull Boolean canCompile, @NonNull Boolean canTest, @NonNull Boolean canRun) {
    this.canCompile = canCompile
    this.canTest = canTest
    this.canRun = canRun
    this.canDebug = Boolean.FALSE
  }

  new (@NonNull Boolean canCompile, @NonNull Boolean canTest, @NonNull Boolean canRun, @NonNull Boolean canDebug) {
    this.canCompile = canCompile
    this.canTest = canTest
    this.canRun = canRun
    this.canDebug = canDebug
  }
}

@JsonRpcData
class BuildTarget {

  @NonNull BuildTargetIdentifier id
  String displayName
  String baseDirectory
  @NonNull List<String> tags
  @NonNull List<String> languageIds
  @NonNull List<BuildTargetIdentifier> dependencies
  @NonNull BuildTargetCapabilities capabilities
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  new (@NonNull BuildTargetIdentifier id, @NonNull List<String> tags, @NonNull List<String> languageIds,
       @NonNull List<BuildTargetIdentifier> dependencies, @NonNull BuildTargetCapabilities capabilities) {
    this.id = id
    this.tags = tags
    this.dependencies = dependencies
    this.languageIds = languageIds
    this.capabilities = capabilities
  }
}

@JsonRpcData
class InitializeBuildParams {
  @NonNull String rootUri
  @NonNull String displayName
  @NonNull String version
  @NonNull String bspVersion
  @NonNull BuildClientCapabilities capabilities
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  new(@NonNull String displayName, @NonNull String version, @NonNull String bspVersion, @NonNull String rootUri, @NonNull BuildClientCapabilities capabilities) {
    this.displayName = displayName
    this.version = version
    this.bspVersion = bspVersion
    this.rootUri = rootUri
    this.capabilities = capabilities
  }
}

@JsonRpcData
class BuildClientCapabilities {
  @NonNull List<String> languageIds
  new(@NonNull List<String> languageIds) {
    this.languageIds = languageIds
  }
}

@JsonRpcData
class CompileProvider {
  @NonNull List<String> languageIds
  new(@NonNull List<String> languageIds) {
    this.languageIds = languageIds
  }
}

@JsonRpcData
class TestProvider {
  @NonNull List<String> languageIds
  new(@NonNull List<String> languageIds) {
    this.languageIds = languageIds
  }
}

@JsonRpcData
class RunProvider {
  @NonNull List<String> languageIds
  new(@NonNull List<String> languageIds) {
    this.languageIds = languageIds
  }
}

@JsonRpcData
class DebugProvider {
  @NonNull List<String> languageIds
  new(@NonNull List<String> languageIds) {
    this.languageIds = languageIds
  }
}

@JsonRpcData
class BuildServerCapabilities {
  CompileProvider compileProvider
  TestProvider testProvider
  RunProvider runProvider
  DebugProvider debugProvider
  Boolean inverseSourcesProvider
  Boolean dependencySourcesProvider
  Boolean dependencyModulesProvider
  Boolean resourcesProvider
  Boolean buildTargetChangedProvider
  Boolean jvmRunEnvironmentProvider
  Boolean jvmTestEnvironmentProvider
  Boolean canReload
}

@JsonRpcData
class InitializeBuildResult {
  @NonNull String displayName
  @NonNull String version
  @NonNull String bspVersion
  @NonNull BuildServerCapabilities capabilities
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  new(@NonNull String displayName, @NonNull String version, @NonNull String bspVersion, @NonNull BuildServerCapabilities capabilities) {
    this.displayName = displayName
    this.version = version
    this.bspVersion = bspVersion
    this.capabilities = capabilities
  }
}

@JsonRpcData
class TaskId {
  @NonNull String id
  List<String> parents
  new(@NonNull String id) {
    this.id = id
  }
}


@JsonRpcData
class ShowMessageParams {
  @NonNull MessageType type
  TaskId task
  String originId
  @NonNull String message
  new(@NonNull MessageType type, @NonNull String message) {
    this.type = type
    this.message = message
  }
}

@JsonRpcData
class LogMessageParams {
  @NonNull MessageType type
  TaskId task
  String originId
  @NonNull String message
  new(@NonNull MessageType type, @NonNull String message) {
    this.type = type
    this.message = message
  }
}

@JsonRpcData
class Position {
  @NonNull Integer line
  @NonNull Integer character
  new(@NonNull Integer line, @NonNull Integer character) {
    this.line = line
    this.character = character
  }
}

@JsonRpcData
class Range {
  @NonNull Position start
  @NonNull Position end
  new(@NonNull Position start, @NonNull Position end) {
    this.start = start
    this.end = end
  }
}

@JsonRpcData
class Location {
  @NonNull String uri
  @NonNull Range range
  new(@NonNull String uri, @NonNull Range range) {
    this.uri = uri
    this.range = range
  }
}

@JsonRpcData
class DiagnosticRelatedInformation {
  @NonNull Location location
  @NonNull String message
  new(@NonNull Location location, @NonNull String message) {
    this.location = location
    this.message = message
  }
}

@JsonRpcData
class Diagnostic {
  @NonNull Range range
  DiagnosticSeverity severity
  String code
  String source
  @NonNull String message
  List<DiagnosticRelatedInformation> relatedInformation
  new(@NonNull Range range, @NonNull String message) {
    this.range = range
    this.message = message
  }
}

@JsonRpcData
class PublishDiagnosticsParams {
  @NonNull TextDocumentIdentifier textDocument
  @NonNull BuildTargetIdentifier buildTarget
  @NonNull List<Diagnostic> diagnostics
  @NonNull Boolean reset
  String originId
  new(@NonNull TextDocumentIdentifier textDocument, @NonNull BuildTargetIdentifier buildTarget, @NonNull List<Diagnostic> diagnostics, @NonNull Boolean reset) {
    this.textDocument = textDocument
    this.buildTarget = buildTarget
    this.diagnostics = diagnostics
    this.reset = reset
  }
}

@JsonRpcData
class WorkspaceBuildTargetsResult {
  List<BuildTarget> targets
  new(@NonNull List<BuildTarget> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class DidChangeBuildTarget {
  @NonNull List<BuildTargetEvent> changes
  new(@NonNull List<BuildTargetEvent> changes) {
    this.changes = changes
  }
}

@JsonRpcData
class BuildTargetEvent {
  @NonNull BuildTargetIdentifier target
  BuildTargetEventKind kind
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  new(@NonNull BuildTargetIdentifier target) {
    this.target = target
  }
}

@JsonRpcData
class SourcesParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class SourcesResult {
  @NonNull List<SourcesItem> items
  new(@NonNull List<SourcesItem> items) {
    this.items = items
  }
}

@JsonRpcData
class SourcesItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<SourceItem> sources
  List<String> roots
  new(@NonNull BuildTargetIdentifier target, @NonNull List<SourceItem> sources) {
    this.target = target
    this.sources = sources
  }
}

@JsonRpcData
class SourceItem {
  @NonNull String uri
  @NonNull SourceItemKind kind
  @NonNull Boolean generated

  new(@NonNull String uri, @NonNull SourceItemKind kind, @NonNull Boolean generated) {
    this.uri = uri
    this.kind = kind
    this.generated = generated
  }
}

@JsonRpcData
class InverseSourcesParams {
  @NonNull TextDocumentIdentifier textDocument
  new(@NonNull TextDocumentIdentifier textDocument) {
    this.textDocument = textDocument
  }
}

@JsonRpcData
class InverseSourcesResult {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class DependencySourcesParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class DependencySourcesResult {
  @NonNull List<DependencySourcesItem> items
  new(@NonNull List<DependencySourcesItem> items) {
    this.items = items
  }
}

@JsonRpcData
class DependencySourcesItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> sources
  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> sources) {
    this.target = target
    this.sources = sources
  }
}

@JsonRpcData
class ResourcesParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class ResourcesResult {
  @NonNull List<ResourcesItem> items
  new(@NonNull List<ResourcesItem> items) {
    this.items = items
  }
}

@JsonRpcData
class ResourcesItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> resources
  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> resources) {
    this.target = target
    this.resources = resources
  }
}

@JsonRpcData
class CompileParams {
  @NonNull List<BuildTargetIdentifier> targets
  String originId
  List<String> arguments
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class CompileResult {
  String originId
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  @NonNull StatusCode statusCode
  new(@NonNull StatusCode statusCode) {
    this.statusCode = statusCode
  }
}

@JsonRpcData
class CompileReport {
  @NonNull BuildTargetIdentifier target
  String originId
  @NonNull Integer errors
  @NonNull Integer warnings
  Long time
  Boolean noOp
  new(@NonNull BuildTargetIdentifier target, @NonNull Integer errors, @NonNull Integer warnings) {
    this.target = target
    this.errors = errors
    this.warnings = warnings
  }
}

@JsonRpcData
class CompileTask {
  @NonNull BuildTargetIdentifier target

  new(@NonNull BuildTargetIdentifier target) {
    this.target = target
  }
}

@JsonRpcData
class TestParams {
  @NonNull List<BuildTargetIdentifier> targets
  String originId
  List<String> arguments
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class TestResult {
  String originId
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  @NonNull StatusCode statusCode
  new(@NonNull StatusCode statusCode) {
    this.statusCode = statusCode
  }
}

@JsonRpcData
class TestReport {
  @NonNull BuildTargetIdentifier target
  String originId
  @NonNull Integer passed
  @NonNull Integer failed
  @NonNull Integer ignored
  @NonNull Integer cancelled
  @NonNull Integer skipped
  Long time

  new(@NonNull BuildTargetIdentifier target, @NonNull Integer passed, @NonNull Integer failed, @NonNull Integer ignored,
      @NonNull Integer cancelled, @NonNull Integer skipped) {
    this.target = target
    this.passed = passed
    this.failed = failed
    this.ignored = ignored
    this.cancelled = cancelled
    this.skipped = skipped
  }
}

@JsonRpcData
class TestTask {
  @NonNull BuildTargetIdentifier target

  new(@NonNull BuildTargetIdentifier target) {
    this.target = target
  }
}

@JsonRpcData
class TestStart {
  @NonNull String displayName
  Location location

  new(@NonNull String displayName) {
    this.displayName = displayName
  }
}

@JsonRpcData
class TestFinish {
  @NonNull String displayName
  String message

  @NonNull TestStatus status

  Location location
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  new(@NonNull String displayName, @NonNull TestStatus status) {
    this.displayName = displayName
    this.status = status
  }
}

@JsonRpcData
class RunParams {
  @NonNull BuildTargetIdentifier target
  String originId
  List<String> arguments
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  new(@NonNull BuildTargetIdentifier target) {
    this.target = target
  }
}

@JsonRpcData
class RunResult {
  String originId
  @NonNull StatusCode statusCode

  new(@NonNull StatusCode statusCode) {
    this.statusCode = statusCode
  }
}

@JsonRpcData
class CleanCacheParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class CleanCacheResult {
  String message
  @NonNull Boolean cleaned

  new(String message, @NonNull Boolean cleaned) {
    this.message = message
    this.cleaned = cleaned
  }
}

@JsonRpcData
class TaskStartParams {
    @NonNull TaskId taskId
    Long eventTime
    String message
    String dataKind
    @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

    new(@NonNull TaskId taskId) {
      this.taskId = taskId
    }
}

@JsonRpcData
class TaskProgressParams {
    @NonNull TaskId taskId
    Long eventTime
    String message
    Long total
    Long progress
    String unit
    String dataKind
    @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

    new(@NonNull TaskId taskId) {
      this.taskId = taskId
    }
}

@JsonRpcData
class TaskFinishParams {
    @NonNull TaskId taskId
    Long eventTime
    String message
    @NonNull StatusCode status
    String dataKind
    @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

    new(@NonNull TaskId taskId, @NonNull StatusCode status) {
      this.taskId = taskId
      this.status = status
    }
}

@JsonRpcData
class DebugSessionParams {
    @NonNull List<BuildTargetIdentifier> targets;
    @NonNull String dataKind;
    @JsonAdapter(JsonElementTypeAdapter.Factory) Object data;

    new(@NonNull List<BuildTargetIdentifier> targets, @NonNull String dataKind, Object data) {
        this.targets = targets;
        this.dataKind = dataKind;
        this.data = data;
    }
}

@JsonRpcData
class DebugSessionAddress {
    @NonNull String uri;

    new(@NonNull String uri){
        this.uri = uri;
    }
}

@JsonRpcData
class DependencyModulesParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class DependencyModulesResult {
  @NonNull List<DependencyModulesItem> items

  new(@NonNull List<DependencyModulesItem> items) {
    this.items = items
  }
}

@JsonRpcData
class DependencyModulesItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<DependencyModule> modules

  new (@NonNull BuildTargetIdentifier target, @NonNull List<DependencyModule> modules) {
    this.target = target
    this.modules = modules
  }
}

@JsonRpcData
class DependencyModule {
  @NonNull String name
  @NonNull String version
  String dataKind
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  new (@NonNull String name, @NonNull String version) {
    this.name = name
    this.version = version
  }
}
