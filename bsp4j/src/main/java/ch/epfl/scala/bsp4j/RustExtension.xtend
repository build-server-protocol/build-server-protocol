package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class RustRawDependency {
    @NonNull String id
    @NonNull String name
    String rename
    String kind
    String target
    boolean optional
    boolean uses_default_features
    @NonNull List<String> features
    new(@NonNull String name,
        String rename,
        String kind,
        String target,
        boolean optional,
        boolean uses_default_features,
        @NonNull List<String> features) {
            this.name = name
            this.rename = rename
            this.kind = kind
            this.target = target
            this.optional = optional
            this.uses_default_features = uses_default_features
            this.features = features
      }
}

@JsonRpcData
class RustTarget {
        @NonNull List<String> kind
        @NonNull String name
        @NonNull String src_path
        @NonNull List<String> crate_types
        String edition
        boolean doctest
        List<String> required_features
        new(@NonNull List<String> kind,
                @NonNull String name,
                @NonNull String src_path,
                @NonNull List<String> crate_types,
                String edition,
                boolean doctest,
                List<String> required_features) {
                    this.kind = kind
                    this.name = name
                    this.src_path = src_path
                    this.crate_types = crate_types
                    this.edition = edition
                    this.doctest = doctest
                    this.required_features = required_features
              }
}

@JsonRpcData
class RustFeature {
        @NonNull String name
        @NonNull List<String> deps
        
        new(@NonNull String name,
                @NonNull List<String> deps) {
                    this.name = name
                    this.deps = deps
              }
}

@JsonRpcData
class RustEnvData {
        @NonNull String name
        @NonNull String value
        
        new(@NonNull String name,
                @NonNull String value) {
                    this.name = name
                    this.value = value
              }
}

@JsonRpcData
class RustProcMacroArtifact {
        @NonNull String path
        @NonNull String hash
        
        new(@NonNull String path,
                @NonNull String hash) {
                    this.path = path
                    this.hash = hash
              }
}

@JsonRpcData
class RustPackage {
        @NonNull String id
        String version
        String origin
        String edition
        @NonNull List<RustTarget> targets
        @NonNull List<RustFeature> features
        @NonNull List<String> enabledFeatures
        RustCfgOptions cfgOptions
        @NonNull List<RustEnvData> env
        String outDirUrl
        RustProcMacroArtifact procMacroArtifact
        new() {
              }
}

@JsonRpcData
class RustDepKindInfo {
    @NonNull String kind
    String target
    new(@NonNull String kind, 
        String target) {
        this.kind = kind
        this.target = target
      }
}

@JsonRpcData
class RustDepMapper {
    @NonNull String source
    @NonNull String target
    String name
    List<RustDepKindInfo> dep_kinds
    new(@NonNull String source, 
        @NonNull String target,
        String name,
        List<RustDepKindInfo> dep_kinds) {
        this.source = source
        this.target = target
        this.name = name
        this.dep_kinds = dep_kinds
    }
}

@JsonRpcData
class RustRawMapper {
    @NonNull String packageId
    @NonNull String rawId
    
    new(@NonNull String packageId, 
        @NonNull String rawId) {
        this.packageId = packageId
        this.rawId = rawId
    }
    
}

@JsonRpcData
class RustWorkspaceResult {
  @NonNull List<RustPackage> packages
  @NonNull List<RustRawDependency> rawDependencies
  @NonNull List<RustRawMapper> packageToRawMapper
  @NonNull List<RustDepMapper> packageToDepMapper  
  
  new(@NonNull List<RustRawDependency> rawDependencies) {
    this.dependencies = rawDependencies
    this.workspaceRoot = workspaceRoot
  }
}

