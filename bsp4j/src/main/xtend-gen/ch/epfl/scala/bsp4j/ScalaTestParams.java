package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.ScalaTestClassesItem;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ScalaTestParams {
  private List<ScalaTestClassesItem> testClasses;
  
  @Pure
  public List<ScalaTestClassesItem> getTestClasses() {
    return this.testClasses;
  }
  
  public void setTestClasses(final List<ScalaTestClassesItem> testClasses) {
    this.testClasses = testClasses;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("testClasses", this.testClasses);
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
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.testClasses== null) ? 0 : this.testClasses.hashCode());
  }
}
