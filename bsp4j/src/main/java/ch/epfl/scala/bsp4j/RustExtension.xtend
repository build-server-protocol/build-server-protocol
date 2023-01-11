package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class RustBuildTarget {
  String edition
  String compiler
  new(String edition, 
    String compiler) {
     this.edition = edition
     this.compiler = compiler
  }
}

@JsonRpcData
class RustOptionsParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class RustMetadataParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class RustRawDependency {
    @NonNull String name
    String rename
    String kind
    String target
    boolean optional
    boolean uses_default_features
    @NonNull String features
    new(@NonNull String name,
        String rename,
        String kind,
        String target,
        boolean optional,
        boolean uses_default_features,
        @NonNull String features) {
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
class RustPackage {
        @NonNull String name
        @NonNull String version
        @NonNull List<String> authors
        String description
        String repository
        String license
        String license_file
        String source
        @NonNull String id
        @NonNull String manifest_path
        @NonNull List<RustTarget> targets
        String edition
        @NonNull List<RustFeature> features
        @NonNull List<RustRawDependency> dependencies
        new(@NonNull String name,
                @NonNull String version,
                @NonNull List<String> authors,
                String description,
                String repository,
                String license,
                String license_file,
                String source,
                @NonNull String id,
                @NonNull String manifest_path,
                @NonNull List<RustTarget> targets,
                String edition,
                @NonNull List<RustFeature> features,
                @NonNull List<RustRawDependency> dependencies) {
                    this.name = name
                    this.version = version
                    this.authors = authors
                    this.description = description
                    this.repository = repository
                    this.license = license
                    this.license_file = license_file
                    this.source = source
                    this.id = id
                    this.manifest_path = manifest_path
                    this.targets = targets
                    this.edition = edition
                    this.features = features
                    this.dependencies = dependencies
              }
}

@JsonRpcData
class RustDepKindInfo {
    String kind
    String target
    new(String kind, 
        String target) {
        this.kind = kind
        this.target = target
      }
}

@JsonRpcData
class RustDep {
    @NonNull String pkg
    String name
    List<RustDepKindInfo> dep_kinds
    new(@NonNull String pkg, 
        String name,
        List<RustDepKindInfo> dep_kinds) {
        this.pkg = pkg
        this.name = name
        this.dep_kinds = dep_kinds
    }
}

@JsonRpcData
class RustResolveNode {
    @NonNull String id
    @NonNull List<String> dependencies
    List<RustDep> deps
    List<String> features
    
    new(@NonNull String id, 
        @NonNull List<String> dependencies,
        List<RustDep> deps,
        List<String> features) {
        this.id = id
        this.dependencies = dependencies
        this.deps = deps
        this.features = features
    }
    
}

@JsonRpcData
class RustMetadataResult {
  @NonNull List<RustPackage> packages
  @NonNull List<RustResolveNode> dependencies
  @NonNull Integer version
  @NonNull List<String> workspaceMembers
  @NonNull String workspaceRoot
  new(@NonNull List<RustPackage> packages,
        @NonNull List<RustResolveNode> dependencies,
        @NonNull Integer version,
        @NonNull List<String> workspaceMembers,
        @NonNull String workspaceRoot) {
    this.packages = packages
    this.dependencies = dependencies
    this.version = version
    this.workspaceMembers = workspaceMembers
    this.workspaceRoot = workspaceRoot
  }
}

@JsonRpcData
class RustOptionsResult {
  @NonNull List<RustOptionsItem> items
  new(@NonNull List<RustOptionsItem> items) {
    this.items = items
  }
}

@JsonRpcData
class RustOptionsItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> compilerOptions
  new(@NonNull BuildTargetIdentifier target,
      @NonNull List<String> compilerOptions) {
    this.target = target
    this.compilerOptions = compilerOptions
   }
}
