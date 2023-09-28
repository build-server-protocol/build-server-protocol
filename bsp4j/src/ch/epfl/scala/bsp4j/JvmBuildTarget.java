package ch.epfl.scala.bsp4j;

import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * `JvmBuildTarget` is a basic data structure that contains jvm-specific metadata, specifically JDK
 * reference.
 */
@SuppressWarnings("all")
public class JvmBuildTarget {
  private String javaHome;

  private String javaVersion;

  public JvmBuildTarget() {}

  @Pure
  public String getJavaHome() {
    return this.javaHome;
  }

  public void setJavaHome(final String javaHome) {
    this.javaHome = javaHome;
  }

  @Pure
  public String getJavaVersion() {
    return this.javaVersion;
  }

  public void setJavaVersion(final String javaVersion) {
    this.javaVersion = javaVersion;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("javaHome", this.javaHome);
    b.add("javaVersion", this.javaVersion);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JvmBuildTarget other = (JvmBuildTarget) obj;
    if (this.javaHome == null) {
      if (other.javaHome != null) return false;
    } else if (!this.javaHome.equals(other.javaHome)) return false;
    if (this.javaVersion == null) {
      if (other.javaVersion != null) return false;
    } else if (!this.javaVersion.equals(other.javaVersion)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.javaHome == null) ? 0 : this.javaHome.hashCode());
    return prime * result + ((this.javaVersion == null) ? 0 : this.javaVersion.hashCode());
  }
}
