package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.CompileProvider;
import ch.epfl.scala.bsp4j.RunProvider;
import ch.epfl.scala.bsp4j.TestProvider;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class BuildServerCapabilities {
  @NonNull
  private CompileProvider compileProvider;
  
  @NonNull
  private TestProvider testProvider;
  
  @NonNull
  private RunProvider runProvider;
  
  @NonNull
  private Boolean inverseSourcesProvider;
  
  @NonNull
  private Boolean dependencySourcesProvider;
  
  @NonNull
  private Boolean resourcesProvider;
  
  @NonNull
  private Boolean buildTargetChangedProvider;
  
  public BuildServerCapabilities(@NonNull final CompileProvider compileProvider, @NonNull final TestProvider testProvider, @NonNull final RunProvider runProvider, @NonNull final Boolean inverseSourcesProvider, @NonNull final Boolean dependencySourcesProvider, @NonNull final Boolean resourcesProvider, @NonNull final Boolean buildTargetChangedProvider) {
    this.compileProvider = compileProvider;
    this.testProvider = testProvider;
    this.runProvider = runProvider;
    this.inverseSourcesProvider = inverseSourcesProvider;
    this.dependencySourcesProvider = dependencySourcesProvider;
    this.resourcesProvider = resourcesProvider;
    this.buildTargetChangedProvider = buildTargetChangedProvider;
  }
  
  @Pure
  @NonNull
  public CompileProvider getCompileProvider() {
    return this.compileProvider;
  }
  
  public void setCompileProvider(@NonNull final CompileProvider compileProvider) {
    this.compileProvider = compileProvider;
  }
  
  @Pure
  @NonNull
  public TestProvider getTestProvider() {
    return this.testProvider;
  }
  
  public void setTestProvider(@NonNull final TestProvider testProvider) {
    this.testProvider = testProvider;
  }
  
  @Pure
  @NonNull
  public RunProvider getRunProvider() {
    return this.runProvider;
  }
  
  public void setRunProvider(@NonNull final RunProvider runProvider) {
    this.runProvider = runProvider;
  }
  
  @Pure
  @NonNull
  public Boolean getInverseSourcesProvider() {
    return this.inverseSourcesProvider;
  }
  
  public void setInverseSourcesProvider(@NonNull final Boolean inverseSourcesProvider) {
    this.inverseSourcesProvider = inverseSourcesProvider;
  }
  
  @Pure
  @NonNull
  public Boolean getDependencySourcesProvider() {
    return this.dependencySourcesProvider;
  }
  
  public void setDependencySourcesProvider(@NonNull final Boolean dependencySourcesProvider) {
    this.dependencySourcesProvider = dependencySourcesProvider;
  }
  
  @Pure
  @NonNull
  public Boolean getResourcesProvider() {
    return this.resourcesProvider;
  }
  
  public void setResourcesProvider(@NonNull final Boolean resourcesProvider) {
    this.resourcesProvider = resourcesProvider;
  }
  
  @Pure
  @NonNull
  public Boolean getBuildTargetChangedProvider() {
    return this.buildTargetChangedProvider;
  }
  
  public void setBuildTargetChangedProvider(@NonNull final Boolean buildTargetChangedProvider) {
    this.buildTargetChangedProvider = buildTargetChangedProvider;
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
    return prime * result + ((this.buildTargetChangedProvider== null) ? 0 : this.buildTargetChangedProvider.hashCode());
  }
}
