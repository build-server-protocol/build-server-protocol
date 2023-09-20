package ch.epfl.scala.bsp4j;

import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The capabilities of the build server.
 * Clients can use these capabilities to notify users what BSP endpoints can and
 * cannot be used and why.
 */
@SuppressWarnings("all")
public class BuildServerCapabilities {
  private CompileProvider compileProvider;

  private TestProvider testProvider;

  private RunProvider runProvider;

  private DebugProvider debugProvider;

  private Boolean inverseSourcesProvider;

  private Boolean dependencySourcesProvider;

  private Boolean dependencyModulesProvider;

  private Boolean resourcesProvider;

  private Boolean outputPathsProvider;

  private Boolean buildTargetChangedProvider;

  private Boolean jvmRunEnvironmentProvider;

  private Boolean jvmTestEnvironmentProvider;

  private Boolean cargoFeaturesProvider;

  private Boolean canReload;

  public BuildServerCapabilities() {
  }

  @Pure
  public CompileProvider getCompileProvider() {
    return this.compileProvider;
  }

  public void setCompileProvider(final CompileProvider compileProvider) {
    this.compileProvider = compileProvider;
  }

  @Pure
  public TestProvider getTestProvider() {
    return this.testProvider;
  }

  public void setTestProvider(final TestProvider testProvider) {
    this.testProvider = testProvider;
  }

  @Pure
  public RunProvider getRunProvider() {
    return this.runProvider;
  }

  public void setRunProvider(final RunProvider runProvider) {
    this.runProvider = runProvider;
  }

  @Pure
  public DebugProvider getDebugProvider() {
    return this.debugProvider;
  }

  public void setDebugProvider(final DebugProvider debugProvider) {
    this.debugProvider = debugProvider;
  }

  @Pure
  public Boolean getInverseSourcesProvider() {
    return this.inverseSourcesProvider;
  }

  public void setInverseSourcesProvider(final Boolean inverseSourcesProvider) {
    this.inverseSourcesProvider = inverseSourcesProvider;
  }

  @Pure
  public Boolean getDependencySourcesProvider() {
    return this.dependencySourcesProvider;
  }

  public void setDependencySourcesProvider(final Boolean dependencySourcesProvider) {
    this.dependencySourcesProvider = dependencySourcesProvider;
  }

  @Pure
  public Boolean getDependencyModulesProvider() {
    return this.dependencyModulesProvider;
  }

  public void setDependencyModulesProvider(final Boolean dependencyModulesProvider) {
    this.dependencyModulesProvider = dependencyModulesProvider;
  }

  @Pure
  public Boolean getResourcesProvider() {
    return this.resourcesProvider;
  }

  public void setResourcesProvider(final Boolean resourcesProvider) {
    this.resourcesProvider = resourcesProvider;
  }

  @Pure
  public Boolean getOutputPathsProvider() {
    return this.outputPathsProvider;
  }

  public void setOutputPathsProvider(final Boolean outputPathsProvider) {
    this.outputPathsProvider = outputPathsProvider;
  }

  @Pure
  public Boolean getBuildTargetChangedProvider() {
    return this.buildTargetChangedProvider;
  }

  public void setBuildTargetChangedProvider(final Boolean buildTargetChangedProvider) {
    this.buildTargetChangedProvider = buildTargetChangedProvider;
  }

  @Pure
  public Boolean getJvmRunEnvironmentProvider() {
    return this.jvmRunEnvironmentProvider;
  }

  public void setJvmRunEnvironmentProvider(final Boolean jvmRunEnvironmentProvider) {
    this.jvmRunEnvironmentProvider = jvmRunEnvironmentProvider;
  }

  @Pure
  public Boolean getJvmTestEnvironmentProvider() {
    return this.jvmTestEnvironmentProvider;
  }

  public void setJvmTestEnvironmentProvider(final Boolean jvmTestEnvironmentProvider) {
    this.jvmTestEnvironmentProvider = jvmTestEnvironmentProvider;
  }

  @Pure
  public Boolean getCargoFeaturesProvider() {
    return this.cargoFeaturesProvider;
  }

  public void setCargoFeaturesProvider(final Boolean cargoFeaturesProvider) {
    this.cargoFeaturesProvider = cargoFeaturesProvider;
  }

  @Pure
  public Boolean getCanReload() {
    return this.canReload;
  }

  public void setCanReload(final Boolean canReload) {
    this.canReload = canReload;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("compileProvider", this.compileProvider);
    b.add("testProvider", this.testProvider);
    b.add("runProvider", this.runProvider);
    b.add("debugProvider", this.debugProvider);
    b.add("inverseSourcesProvider", this.inverseSourcesProvider);
    b.add("dependencySourcesProvider", this.dependencySourcesProvider);
    b.add("dependencyModulesProvider", this.dependencyModulesProvider);
    b.add("resourcesProvider", this.resourcesProvider);
    b.add("outputPathsProvider", this.outputPathsProvider);
    b.add("buildTargetChangedProvider", this.buildTargetChangedProvider);
    b.add("jvmRunEnvironmentProvider", this.jvmRunEnvironmentProvider);
    b.add("jvmTestEnvironmentProvider", this.jvmTestEnvironmentProvider);
    b.add("cargoFeaturesProvider", this.cargoFeaturesProvider);
    b.add("canReload", this.canReload);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BuildServerCapabilities other = (BuildServerCapabilities) obj;
    if (this.compileProvider == null) {
      if (other.compileProvider != null)
        return false;
    } else if (!this.compileProvider.equals(other.compileProvider))
      return false;
    if (this.testProvider == null) {
      if (other.testProvider != null)
        return false;
    } else if (!this.testProvider.equals(other.testProvider))
      return false;
    if (this.runProvider == null) {
      if (other.runProvider != null)
        return false;
    } else if (!this.runProvider.equals(other.runProvider))
      return false;
    if (this.debugProvider == null) {
      if (other.debugProvider != null)
        return false;
    } else if (!this.debugProvider.equals(other.debugProvider))
      return false;
    if (this.inverseSourcesProvider == null) {
      if (other.inverseSourcesProvider != null)
        return false;
    } else if (!this.inverseSourcesProvider.equals(other.inverseSourcesProvider))
      return false;
    if (this.dependencySourcesProvider == null) {
      if (other.dependencySourcesProvider != null)
        return false;
    } else if (!this.dependencySourcesProvider.equals(other.dependencySourcesProvider))
      return false;
    if (this.dependencyModulesProvider == null) {
      if (other.dependencyModulesProvider != null)
        return false;
    } else if (!this.dependencyModulesProvider.equals(other.dependencyModulesProvider))
      return false;
    if (this.resourcesProvider == null) {
      if (other.resourcesProvider != null)
        return false;
    } else if (!this.resourcesProvider.equals(other.resourcesProvider))
      return false;
    if (this.outputPathsProvider == null) {
      if (other.outputPathsProvider != null)
        return false;
    } else if (!this.outputPathsProvider.equals(other.outputPathsProvider))
      return false;
    if (this.buildTargetChangedProvider == null) {
      if (other.buildTargetChangedProvider != null)
        return false;
    } else if (!this.buildTargetChangedProvider.equals(other.buildTargetChangedProvider))
      return false;
    if (this.jvmRunEnvironmentProvider == null) {
      if (other.jvmRunEnvironmentProvider != null)
        return false;
    } else if (!this.jvmRunEnvironmentProvider.equals(other.jvmRunEnvironmentProvider))
      return false;
    if (this.jvmTestEnvironmentProvider == null) {
      if (other.jvmTestEnvironmentProvider != null)
        return false;
    } else if (!this.jvmTestEnvironmentProvider.equals(other.jvmTestEnvironmentProvider))
      return false;
    if (this.cargoFeaturesProvider == null) {
      if (other.cargoFeaturesProvider != null)
        return false;
    } else if (!this.cargoFeaturesProvider.equals(other.cargoFeaturesProvider))
      return false;
    if (this.canReload == null) {
      if (other.canReload != null)
        return false;
    } else if (!this.canReload.equals(other.canReload))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.compileProvider== null) ? 0 : this.compileProvider.hashCode());
    result = prime * result + ((this.testProvider== null) ? 0 : this.testProvider.hashCode());
    result = prime * result + ((this.runProvider== null) ? 0 : this.runProvider.hashCode());
    result = prime * result + ((this.debugProvider== null) ? 0 : this.debugProvider.hashCode());
    result = prime * result + ((this.inverseSourcesProvider== null) ? 0 : this.inverseSourcesProvider.hashCode());
    result = prime * result + ((this.dependencySourcesProvider== null) ? 0 : this.dependencySourcesProvider.hashCode());
    result = prime * result + ((this.dependencyModulesProvider== null) ? 0 : this.dependencyModulesProvider.hashCode());
    result = prime * result + ((this.resourcesProvider== null) ? 0 : this.resourcesProvider.hashCode());
    result = prime * result + ((this.outputPathsProvider== null) ? 0 : this.outputPathsProvider.hashCode());
    result = prime * result + ((this.buildTargetChangedProvider== null) ? 0 : this.buildTargetChangedProvider.hashCode());
    result = prime * result + ((this.jvmRunEnvironmentProvider== null) ? 0 : this.jvmRunEnvironmentProvider.hashCode());
    result = prime * result + ((this.jvmTestEnvironmentProvider== null) ? 0 : this.jvmTestEnvironmentProvider.hashCode());
    result = prime * result + ((this.cargoFeaturesProvider== null) ? 0 : this.cargoFeaturesProvider.hashCode());
    return prime * result + ((this.canReload== null) ? 0 : this.canReload.hashCode());
  }
}
