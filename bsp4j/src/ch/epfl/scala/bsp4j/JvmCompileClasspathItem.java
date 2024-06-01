package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class JvmCompileClasspathItem {
  @NonNull private BuildTargetIdentifier target;

  @NonNull private List<String> classpath;

  public JvmCompileClasspathItem(
      @NonNull final BuildTargetIdentifier target, @NonNull final List<String> classpath) {
    this.target = target;
    this.classpath = classpath;
  }

  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @NonNull
  public List<String> getClasspath() {
    return this.classpath;
  }

  public void setClasspath(@NonNull final List<String> classpath) {
    this.classpath = Preconditions.checkNotNull(classpath, "classpath");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("classpath", this.classpath);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JvmCompileClasspathItem other = (JvmCompileClasspathItem) obj;
    if (this.target == null) {
      if (other.target != null) return false;
    } else if (!this.target.equals(other.target)) return false;
    if (this.classpath == null) {
      if (other.classpath != null) return false;
    } else if (!this.classpath.equals(other.classpath)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
    return prime * result + ((this.classpath == null) ? 0 : this.classpath.hashCode());
  }
}
