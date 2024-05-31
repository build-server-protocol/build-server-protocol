package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * `ScalaBuildTarget` is a basic data structure that contains scala-specific metadata for compiling
 * a target containing Scala sources.
 */
@SuppressWarnings("all")
public class ScalaBuildTarget {
  @NonNull private String scalaOrganization;

  @NonNull private String scalaVersion;

  @NonNull private String scalaBinaryVersion;

  @NonNull private ScalaPlatform platform;

  @NonNull private List<String> jars;

  private JvmBuildTarget jvmBuildTarget;

  public ScalaBuildTarget(
      @NonNull final String scalaOrganization,
      @NonNull final String scalaVersion,
      @NonNull final String scalaBinaryVersion,
      @NonNull final ScalaPlatform platform,
      @NonNull final List<String> jars) {
    this.scalaOrganization = scalaOrganization;
    this.scalaVersion = scalaVersion;
    this.scalaBinaryVersion = scalaBinaryVersion;
    this.platform = platform;
    this.jars = jars;
  }

  @Pure
  @NonNull
  public String getScalaOrganization() {
    return this.scalaOrganization;
  }

  public void setScalaOrganization(@NonNull final String scalaOrganization) {
    this.scalaOrganization = Preconditions.checkNotNull(scalaOrganization, "scalaOrganization");
  }

  @Pure
  @NonNull
  public String getScalaVersion() {
    return this.scalaVersion;
  }

  public void setScalaVersion(@NonNull final String scalaVersion) {
    this.scalaVersion = Preconditions.checkNotNull(scalaVersion, "scalaVersion");
  }

  @Pure
  @NonNull
  public String getScalaBinaryVersion() {
    return this.scalaBinaryVersion;
  }

  public void setScalaBinaryVersion(@NonNull final String scalaBinaryVersion) {
    this.scalaBinaryVersion = Preconditions.checkNotNull(scalaBinaryVersion, "scalaBinaryVersion");
  }

  @Pure
  @NonNull
  public ScalaPlatform getPlatform() {
    return this.platform;
  }

  public void setPlatform(@NonNull final ScalaPlatform platform) {
    this.platform = Preconditions.checkNotNull(platform, "platform");
  }

  @Pure
  @NonNull
  public List<String> getJars() {
    return this.jars;
  }

  public void setJars(@NonNull final List<String> jars) {
    this.jars = Preconditions.checkNotNull(jars, "jars");
  }

  @Pure
  public JvmBuildTarget getJvmBuildTarget() {
    return this.jvmBuildTarget;
  }

  public void setJvmBuildTarget(final JvmBuildTarget jvmBuildTarget) {
    this.jvmBuildTarget = jvmBuildTarget;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("scalaOrganization", this.scalaOrganization);
    b.add("scalaVersion", this.scalaVersion);
    b.add("scalaBinaryVersion", this.scalaBinaryVersion);
    b.add("platform", this.platform);
    b.add("jars", this.jars);
    b.add("jvmBuildTarget", this.jvmBuildTarget);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ScalaBuildTarget other = (ScalaBuildTarget) obj;
    if (this.scalaOrganization == null) {
      if (other.scalaOrganization != null) return false;
    } else if (!this.scalaOrganization.equals(other.scalaOrganization)) return false;
    if (this.scalaVersion == null) {
      if (other.scalaVersion != null) return false;
    } else if (!this.scalaVersion.equals(other.scalaVersion)) return false;
    if (this.scalaBinaryVersion == null) {
      if (other.scalaBinaryVersion != null) return false;
    } else if (!this.scalaBinaryVersion.equals(other.scalaBinaryVersion)) return false;
    if (this.platform == null) {
      if (other.platform != null) return false;
    } else if (!this.platform.equals(other.platform)) return false;
    if (this.jars == null) {
      if (other.jars != null) return false;
    } else if (!this.jars.equals(other.jars)) return false;
    if (this.jvmBuildTarget == null) {
      if (other.jvmBuildTarget != null) return false;
    } else if (!this.jvmBuildTarget.equals(other.jvmBuildTarget)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((this.scalaOrganization == null) ? 0 : this.scalaOrganization.hashCode());
    result = prime * result + ((this.scalaVersion == null) ? 0 : this.scalaVersion.hashCode());
    result =
        prime * result
            + ((this.scalaBinaryVersion == null) ? 0 : this.scalaBinaryVersion.hashCode());
    result = prime * result + ((this.platform == null) ? 0 : this.platform.hashCode());
    result = prime * result + ((this.jars == null) ? 0 : this.jars.hashCode());
    return prime * result + ((this.jvmBuildTarget == null) ? 0 : this.jvmBuildTarget.hashCode());
  }
}
