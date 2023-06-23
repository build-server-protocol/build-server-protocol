package ch.epfl.scala.bsp4j;

import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class PythonBuildTarget {
  private String version;

  private String interpreter;

  public PythonBuildTarget() {
  }

  @Pure
  public String getVersion() {
    return this.version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

  @Pure
  public String getInterpreter() {
    return this.interpreter;
  }

  public void setInterpreter(final String interpreter) {
    this.interpreter = interpreter;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("version", this.version);
    b.add("interpreter", this.interpreter);
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
    PythonBuildTarget other = (PythonBuildTarget) obj;
    if (this.version == null) {
      if (other.version != null)
        return false;
    } else if (!this.version.equals(other.version))
      return false;
    if (this.interpreter == null) {
      if (other.interpreter != null)
        return false;
    } else if (!this.interpreter.equals(other.interpreter))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.version== null) ? 0 : this.version.hashCode());
    return prime * result + ((this.interpreter== null) ? 0 : this.interpreter.hashCode());
  }
}
