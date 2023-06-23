package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class BuildTargetCapabilities {
  @NonNull
  private Boolean canCompile;

  @NonNull
  private Boolean canTest;

  @NonNull
  private Boolean canRun;

  @NonNull
  private Boolean canDebug;

  public BuildTargetCapabilities(@NonNull final Boolean canCompile, @NonNull final Boolean canTest, @NonNull final Boolean canRun, @NonNull final Boolean canDebug) {
    this.canCompile = canCompile;
    this.canTest = canTest;
    this.canRun = canRun;
    this.canDebug = canDebug;
  }

  @Pure
  @NonNull
  public Boolean getCanCompile() {
    return this.canCompile;
  }

  public void setCanCompile(@NonNull final Boolean canCompile) {
    this.canCompile = Preconditions.checkNotNull(canCompile, "canCompile");
  }

  @Pure
  @NonNull
  public Boolean getCanTest() {
    return this.canTest;
  }

  public void setCanTest(@NonNull final Boolean canTest) {
    this.canTest = Preconditions.checkNotNull(canTest, "canTest");
  }

  @Pure
  @NonNull
  public Boolean getCanRun() {
    return this.canRun;
  }

  public void setCanRun(@NonNull final Boolean canRun) {
    this.canRun = Preconditions.checkNotNull(canRun, "canRun");
  }

  @Pure
  @NonNull
  public Boolean getCanDebug() {
    return this.canDebug;
  }

  public void setCanDebug(@NonNull final Boolean canDebug) {
    this.canDebug = Preconditions.checkNotNull(canDebug, "canDebug");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("canCompile", this.canCompile);
    b.add("canTest", this.canTest);
    b.add("canRun", this.canRun);
    b.add("canDebug", this.canDebug);
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
    BuildTargetCapabilities other = (BuildTargetCapabilities) obj;
    if (this.canCompile == null) {
      if (other.canCompile != null)
        return false;
    } else if (!this.canCompile.equals(other.canCompile))
      return false;
    if (this.canTest == null) {
      if (other.canTest != null)
        return false;
    } else if (!this.canTest.equals(other.canTest))
      return false;
    if (this.canRun == null) {
      if (other.canRun != null)
        return false;
    } else if (!this.canRun.equals(other.canRun))
      return false;
    if (this.canDebug == null) {
      if (other.canDebug != null)
        return false;
    } else if (!this.canDebug.equals(other.canDebug))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.canCompile== null) ? 0 : this.canCompile.hashCode());
    result = prime * result + ((this.canTest== null) ? 0 : this.canTest.hashCode());
    result = prime * result + ((this.canRun== null) ? 0 : this.canRun.hashCode());
    return prime * result + ((this.canDebug== null) ? 0 : this.canDebug.hashCode());
  }
}
