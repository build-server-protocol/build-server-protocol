package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ScalaTestClassesItem {
  @NonNull
  private BuildTargetIdentifier target;
  
  @NonNull
  private List<String> classes;
  
  private String framework;
  
  public ScalaTestClassesItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> classes) {
    this.target = target;
    this.classes = classes;
    this.framework = null;
  }
  
  public ScalaTestClassesItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> classes, final String framework) {
    this.target = target;
    this.classes = classes;
    this.framework = framework;
  }
  
  @Pure
  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }
  
  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }
  
  @Pure
  @NonNull
  public List<String> getClasses() {
    return this.classes;
  }
  
  public void setClasses(@NonNull final List<String> classes) {
    this.classes = Preconditions.checkNotNull(classes, "classes");
  }
  
  @Pure
  public String getFramework() {
    return this.framework;
  }
  
  public void setFramework(final String framework) {
    this.framework = framework;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("classes", this.classes);
    b.add("framework", this.framework);
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
    ScalaTestClassesItem other = (ScalaTestClassesItem) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.classes == null) {
      if (other.classes != null)
        return false;
    } else if (!this.classes.equals(other.classes))
      return false;
    if (this.framework == null) {
      if (other.framework != null)
        return false;
    } else if (!this.framework.equals(other.framework))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.classes== null) ? 0 : this.classes.hashCode());
    return prime * result + ((this.framework== null) ? 0 : this.framework.hashCode());
  }
}
