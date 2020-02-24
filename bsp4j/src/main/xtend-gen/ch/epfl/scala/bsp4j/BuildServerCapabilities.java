package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.CompileProvider;
import ch.epfl.scala.bsp4j.RunProvider;
import ch.epfl.scala.bsp4j.TestProvider;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class BuildServerCapabilities {
  private CompileProvider compileProvider;
  
  private TestProvider testProvider;
  
  private RunProvider runProvider;
  
  private Boolean inverseSourcesProvider;
  
  private Boolean dependencySourcesProvider;
  
  private Boolean resourcesProvider;
  
  private Boolean buildTargetChangedProvider;
  
  private Boolean jvmTestEnvironmentProvider;
  
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
  public Boolean getResourcesProvider() {
    return this.resourcesProvider;
  }
  
  public void setResourcesProvider(final Boolean resourcesProvider) {
    this.resourcesProvider = resourcesProvider;
  }
  
  @Pure
  public Boolean getBuildTargetChangedProvider() {
    return this.buildTargetChangedProvider;
  }
  
  public void setBuildTargetChangedProvider(final Boolean buildTargetChangedProvider) {
    this.buildTargetChangedProvider = buildTargetChangedProvider;
  }
  
  @Pure
  public Boolean getJvmTestEnvironmentProvider() {
    return this.jvmTestEnvironmentProvider;
  }
  
  public void setJvmTestEnvironmentProvider(final Boolean jvmTestEnvironmentProvider) {
    this.jvmTestEnvironmentProvider = jvmTestEnvironmentProvider;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("compileProvider", this.compileProvider);
    b.add("testProvider", this.testProvider);
    b.add("runProvider", this.runProvider);
    b.add("inverseSourcesProvider", this.inverseSourcesProvider);
    b.add("dependencySourcesProvider", this.dependencySourcesProvider);
    b.add("resourcesProvider", this.resourcesProvider);
    b.add("buildTargetChangedProvider", this.buildTargetChangedProvider);
    b.add("jvmTestEnvironmentProvider", this.jvmTestEnvironmentProvider);
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
    if (this.resourcesProvider == null) {
      if (other.resourcesProvider != null)
        return false;
    } else if (!this.resourcesProvider.equals(other.resourcesProvider))
      return false;
    if (this.buildTargetChangedProvider == null) {
      if (other.buildTargetChangedProvider != null)
        return false;
    } else if (!this.buildTargetChangedProvider.equals(other.buildTargetChangedProvider))
      return false;
    if (this.jvmTestEnvironmentProvider == null) {
      if (other.jvmTestEnvironmentProvider != null)
        return false;
    } else if (!this.jvmTestEnvironmentProvider.equals(other.jvmTestEnvironmentProvider))
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
    result = prime * result + ((this.inverseSourcesProvider== null) ? 0 : this.inverseSourcesProvider.hashCode());
    result = prime * result + ((this.dependencySourcesProvider== null) ? 0 : this.dependencySourcesProvider.hashCode());
    result = prime * result + ((this.resourcesProvider== null) ? 0 : this.resourcesProvider.hashCode());
    result = prime * result + ((this.buildTargetChangedProvider== null) ? 0 : this.buildTargetChangedProvider.hashCode());
    return prime * result + ((this.jvmTestEnvironmentProvider== null) ? 0 : this.jvmTestEnvironmentProvider.hashCode());
  }
}
