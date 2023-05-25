package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class RustRawDependency {
    @NonNull String packageId
    @NonNull String name
    String rename
    String kind
    String target
    boolean optional
    boolean uses_default_features
    @NonNull List<String> features
    new(@NonNull String packageId,
        @NonNull String name,
        String rename,
        String kind,
        String target,
        boolean optional,
        boolean uses_default_features,
        @NonNull List<String> features) {
            this.packageId = packageId
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
        @NonNull String name
        @NonNull String crateRootUrl
        @NonNull String packageRootUrl
        @NonNull String kind
        String edition
        boolean doctest
        List<String> requiredFeatures
        new(@NonNull String name,
                @NonNull String crateRootUrl,
                @NonNull String packageRootUrl,
                @NonNull String kind,
                String edition,
                boolean doctest,
                List<String> requiredFeatures) {
                    this.kind = kind
                    this.name = name
                    this.crateRootUrl = crateRootUrl
                    this.packageRootUrl = packageRootUrl
                    this.edition = edition
                    this.doctest = doctest
                    this.requiredFeatures = requiredFeatures
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
class RustKeyValueMapper {
        @NonNull String key
        @NonNull List<String> value
        
        new(@NonNull String key,
                @NonNull List<String> value) {
                    this.key = key
                    this.value = value
              }
}

@JsonRpcData
class RustCfgOptions {
        @NonNull List<RustKeyValueMapper> keyValueOptions
        @NonNull List<String> nameOptions
        
        new(@NonNull List<RustKeyValueMapper> keyValueOptions,
                @NonNull List<String> nameOptions) {
                    this.keyValueOptions = keyValueOptions
                    this.nameOptions = nameOptions
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
        String source
        @NonNull List<RustTarget> targets
        @NonNull List<RustTarget> allTargets
        @NonNull List<RustFeature> features
        @NonNull List<String> enabledFeatures
        RustCfgOptions cfgOptions
        @NonNull List<RustEnvData> env
        String outDirUrl
        RustProcMacroArtifact procMacroArtifact
        new(@NonNull String id,
                String version,
                String origin,
                String edition,
                String source,                
                @NonNull List<RustTarget> targets,
                @NonNull List<RustTarget> allTargets,
                @NonNull List<RustFeature> features,
                @NonNull List<String> enabledFeatures,
                RustCfgOptions cfgOptions,
                @NonNull List<RustEnvData> env,
                String outDirUrl,
                RustProcMacroArtifact procMacroArtifact) {
                    
        this.id = id
        this.version = version
        this.origin = origin
        this.edition = edition
        this.source = source
        this.targets = targets
        this.allTargets = allTargets
        this.features = features
        this.enabledFeatures = enabledFeatures
        this.cfgOptions = cfgOptions
        this.env = env
        this.outDirUrl = outDirUrl
        this.procMacroArtifact = procMacroArtifact
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
class RustDependency {
    @NonNull String source
    @NonNull String target
    String name
    List<RustDepKindInfo> depKinds
    new(@NonNull String source, 
        @NonNull String target,
        String name,
        List<RustDepKindInfo> depKinds) {
        this.source = source
        this.target = target
        this.name = name
        this.depKinds = depKinds
    }
}

@JsonRpcData
class RustWorkspaceParams {
    @NonNull List<BuildTargetIdentifier> targets
    new(@NonNull List<BuildTargetIdentifier> targets) {
        this.targets = targets
    }
}

@JsonRpcData
class RustWorkspaceResult {
  @NonNull List<RustPackage> packages
  @NonNull List<RustRawDependency> rawDependencies
  @NonNull List<RustDependency> dependencies
  @NonNull List<BuildTargetIdentifier> resolvedTargets
  
  new(@NonNull List<RustPackage> packages,
        @NonNull List<RustRawDependency> rawDependencies,
        @NonNull List<RustDependency> dependencies,
        @NonNull List<BuildTargetIdentifier> resolvedTargets) {
    this.packages = packages
    this.rawDependencies = rawDependencies
    this.dependencies = dependencies
    this.resolvedTargets = resolvedTargets
  }
}

@JsonRpcData
class RustcInfo {
    @NonNull String sysroot
    @NonNull String srcSysroot
    @NonNull String version
    @NonNull String host

    new(@NonNull String sysroot,
        @NonNull String srcSysroot,
        @NonNull String version,
        @NonNull String host) {
            this.sysroot = sysroot
            this.srcSysroot = srcSysroot
            this.version = version
            this.host = host
      }
}

@JsonRpcData
class RustToolchainParams {
    @NonNull List<BuildTargetIdentifier> targets
    new(@NonNull List<BuildTargetIdentifier> targets) {
        this.targets = targets
    }
}

@JsonRpcData
class RustToolchainResult {
    @NonNull List<RustToolchain> toolchains
    new(@NonNull List<RustToolchain> toolchains) {
        this.toolchains = toolchains
    }
}

@JsonRpcData
class RustToolchain {
    RustcInfo rustc
    @NonNull String cargoBinPath
    @NonNull String procMacroSrvPath
    new(RustcInfo rustc,
        @NonNull String cargoBinPath,
        @NonNull String procMacroSrvPath) {
            this.rustc = rustc
            this.cargoBinPath = cargoBinPath
            this.procMacroSrvPath = procMacroSrvPath
      }
}
