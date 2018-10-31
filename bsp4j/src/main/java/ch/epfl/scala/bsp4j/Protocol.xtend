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

  new (Boolean canCompile, Boolean canTest, Boolean canRun) {
    this.canCompile = canCompile
    this.canTest = canTest
    this.canRun = canRun
  }
}

@JsonRpcData
class BuildTarget {

  @NonNull BuildTargetIdentifier id
  String displayName
  @NonNull List<String> tags
  @NonNull List<String> languageIds
  @NonNull List<BuildTargetIdentifier> dependencies
  @NonNull BuildTargetCapabilities capabilities
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data

  new (@NonNull BuildTargetIdentifier id, @NonNull List<String> tags, @NonNull List<String> languageIds,
       @NonNull List<BuildTargetIdentifier> dependencies,  @NonNull BuildTargetCapabilities capabilities) {
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
  @NonNull BuildClientCapabilities capabilities

  new(@NonNull String rootUri, @NonNull BuildClientCapabilities capabilities) {
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
class BuildServerCapabilities {
  CompileProvider compileProvider
  TestProvider testProvider
  RunProvider runProvider
  Boolean inverseSourcesProvider
  Boolean dependencySourcesProvider
  Boolean resourcesProvider
  Boolean buildTargetChangedProvider
}

@JsonRpcData
class InitializeBuildResult {
  @NonNull BuildServerCapabilities capabilities
  new(@NonNull BuildServerCapabilities capabilities) {
    this.capabilities = capabilities
  }
}

@JsonRpcData
class TaskId {
  @NonNull String id
  String parent
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
  DiagnosticRelatedInformation relatedInformation
  new(@NonNull Range range, @NonNull String message) {
    this.range = range
    this.message = message
  }
}

@JsonRpcData
class PublishDiagnosticsParams {
  @NonNull TextDocumentIdentifier textDocument
  @NonNull BuildTargetIdentifier buildTarget
  String originId
  List<Diagnostic> diagnostics
  new(@NonNull TextDocumentIdentifier textDocument, @NonNull BuildTargetIdentifier buildTarget) {
    this.textDocument = textDocument
    this.buildTarget = buildTarget
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
  @NonNull String uri
  BuildTargetEventKind kind
  @JsonAdapter(JsonElementTypeAdapter.Factory) Object data
  new(@NonNull String uri) {
    this.uri = uri
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
  new(@NonNull BuildTargetIdentifier target, @NonNull List<SourceItem> sources) {
    this.target = target
    this.sources = sources
  }
}

@JsonRpcData
class SourceItem {
  @NonNull String uri
  @NonNull Boolean generated
  new(@NonNull String uri, @NonNull Boolean generated) {
    this.uri = uri
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
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class CompileResult {
  String originId
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
  new(@NonNull BuildTargetIdentifier target, @NonNull Integer errors, @NonNull Integer warnings) {
    this.target = target
    this.errors = errors
    this.warnings = warnings
  }
}

@JsonRpcData
class TestParams {
  @NonNull List<BuildTargetIdentifier> targets
  String originId
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class TestResult {
  String originId
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
class RunParams {
  @NonNull BuildTargetIdentifier target
  String originId
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
