package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class ScalaMainClassesItem {
  @NonNull private BuildTargetIdentifier target;

  @NonNull private List<ScalaMainClass> classes;

  public ScalaMainClassesItem(
      @NonNull final BuildTargetIdentifier target, @NonNull final List<ScalaMainClass> classes) {
    this.target = target;
    this.classes = classes;
  }

  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @NonNull
  public List<ScalaMainClass> getClasses() {
    return this.classes;
  }

  public void setClasses(@NonNull final List<ScalaMainClass> classes) {
    this.classes = Preconditions.checkNotNull(classes, "classes");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("classes", this.classes);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ScalaMainClassesItem other = (ScalaMainClassesItem) obj;
    if (this.target == null) {
      if (other.target != null) return false;
    } else if (!this.target.equals(other.target)) return false;
    if (this.classes == null) {
      if (other.classes != null) return false;
    } else if (!this.classes.equals(other.classes)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
    return prime * result + ((this.classes == null) ? 0 : this.classes.hashCode());
  }
}
