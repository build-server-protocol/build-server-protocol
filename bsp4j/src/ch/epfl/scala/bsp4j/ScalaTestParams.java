package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * `ScalaTestParams` contains scala-specific metadata for testing Scala targets.
 */
@SuppressWarnings("all")
public class ScalaTestParams {
  private List<ScalaTestClassesItem> testClasses;

  private List<String> jvmOptions;

  public ScalaTestParams() {
  }

  @Pure
  public List<ScalaTestClassesItem> getTestClasses() {
    return this.testClasses;
  }

  public void setTestClasses(final List<ScalaTestClassesItem> testClasses) {
    this.testClasses = testClasses;
  }

  @Pure
  public List<String> getJvmOptions() {
    return this.jvmOptions;
  }

  public void setJvmOptions(final List<String> jvmOptions) {
    this.jvmOptions = jvmOptions;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("testClasses", this.testClasses);
    b.add("jvmOptions", this.jvmOptions);
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
    ScalaTestParams other = (ScalaTestParams) obj;
    if (this.testClasses == null) {
      if (other.testClasses != null)
        return false;
    } else if (!this.testClasses.equals(other.testClasses))
      return false;
    if (this.jvmOptions == null) {
      if (other.jvmOptions != null)
        return false;
    } else if (!this.jvmOptions.equals(other.jvmOptions))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.testClasses== null) ? 0 : this.testClasses.hashCode());
    return prime * result + ((this.jvmOptions== null) ? 0 : this.jvmOptions.hashCode());
  }
}
