package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ScalaDiagnostic {
  private List<ScalaAction> actions;

  public ScalaDiagnostic() {
  }

  @Pure
  public List<ScalaAction> getActions() {
    return this.actions;
  }

  public void setActions(final List<ScalaAction> actions) {
    this.actions = actions;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("actions", this.actions);
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
    ScalaDiagnostic other = (ScalaDiagnostic) obj;
    if (this.actions == null) {
      if (other.actions != null)
        return false;
    } else if (!this.actions.equals(other.actions))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.actions== null) ? 0 : this.actions.hashCode());
  }
}
