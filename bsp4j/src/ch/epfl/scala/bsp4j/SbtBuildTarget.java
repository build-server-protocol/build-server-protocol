package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * `SbtBuildTarget` is a basic data structure that contains sbt-specific metadata
 * for providing editor support for sbt build files.
 * 
 * For example, say we have a project in `/foo/bar` defining projects `A` and `B`
 * and two meta builds `M1` (defined in `/foo/bar/project`) and `M2` (defined in
 * `/foo/bar/project/project`).
 * 
 * The sbt build target for `M1` will have `A` and `B` as the defined targets and
 * `M2` as the parent. Similarly, the sbt build target for `M2` will have `M1` as
 * the defined target and no parent.
 * 
 * Clients can use this information to reconstruct the tree of sbt meta builds. The
 * `parent` information can be defined from `children` but it's provided by the
 * server to simplify the data processing on the client side.
 */
@SuppressWarnings("all")
public class SbtBuildTarget {
  @NonNull
  private String sbtVersion;

  @NonNull
  private List<String> autoImports;

  @NonNull
  private ScalaBuildTarget scalaBuildTarget;

  private BuildTargetIdentifier parent;

  @NonNull
  private List<BuildTargetIdentifier> children;

  public SbtBuildTarget(@NonNull final String sbtVersion, @NonNull final List<String> autoImports, @NonNull final ScalaBuildTarget scalaBuildTarget, @NonNull final List<BuildTargetIdentifier> children) {
    this.sbtVersion = sbtVersion;
    this.autoImports = autoImports;
    this.scalaBuildTarget = scalaBuildTarget;
    this.children = children;
  }

  @Pure
  @NonNull
  public String getSbtVersion() {
    return this.sbtVersion;
  }

  public void setSbtVersion(@NonNull final String sbtVersion) {
    this.sbtVersion = Preconditions.checkNotNull(sbtVersion, "sbtVersion");
  }

  @Pure
  @NonNull
  public List<String> getAutoImports() {
    return this.autoImports;
  }

  public void setAutoImports(@NonNull final List<String> autoImports) {
    this.autoImports = Preconditions.checkNotNull(autoImports, "autoImports");
  }

  @Pure
  @NonNull
  public ScalaBuildTarget getScalaBuildTarget() {
    return this.scalaBuildTarget;
  }

  public void setScalaBuildTarget(@NonNull final ScalaBuildTarget scalaBuildTarget) {
    this.scalaBuildTarget = Preconditions.checkNotNull(scalaBuildTarget, "scalaBuildTarget");
  }

  @Pure
  public BuildTargetIdentifier getParent() {
    return this.parent;
  }

  public void setParent(final BuildTargetIdentifier parent) {
    this.parent = parent;
  }

  @Pure
  @NonNull
  public List<BuildTargetIdentifier> getChildren() {
    return this.children;
  }

  public void setChildren(@NonNull final List<BuildTargetIdentifier> children) {
    this.children = Preconditions.checkNotNull(children, "children");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("sbtVersion", this.sbtVersion);
    b.add("autoImports", this.autoImports);
    b.add("scalaBuildTarget", this.scalaBuildTarget);
    b.add("parent", this.parent);
    b.add("children", this.children);
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
    SbtBuildTarget other = (SbtBuildTarget) obj;
    if (this.sbtVersion == null) {
      if (other.sbtVersion != null)
        return false;
    } else if (!this.sbtVersion.equals(other.sbtVersion))
      return false;
    if (this.autoImports == null) {
      if (other.autoImports != null)
        return false;
    } else if (!this.autoImports.equals(other.autoImports))
      return false;
    if (this.scalaBuildTarget == null) {
      if (other.scalaBuildTarget != null)
        return false;
    } else if (!this.scalaBuildTarget.equals(other.scalaBuildTarget))
      return false;
    if (this.parent == null) {
      if (other.parent != null)
        return false;
    } else if (!this.parent.equals(other.parent))
      return false;
    if (this.children == null) {
      if (other.children != null)
        return false;
    } else if (!this.children.equals(other.children))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.sbtVersion== null) ? 0 : this.sbtVersion.hashCode());
    result = prime * result + ((this.autoImports== null) ? 0 : this.autoImports.hashCode());
    result = prime * result + ((this.scalaBuildTarget== null) ? 0 : this.scalaBuildTarget.hashCode());
    result = prime * result + ((this.parent== null) ? 0 : this.parent.hashCode());
    return prime * result + ((this.children== null) ? 0 : this.children.hashCode());
  }
}
