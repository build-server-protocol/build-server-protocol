package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class JavacOptionsItem {
  @NonNull
  private BuildTargetIdentifier target;

  @NonNull
  private List<String> options;

  @NonNull
  private List<String> classpath;

  @NonNull
  private String classDirectory;

  public JavacOptionsItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> options, @NonNull final List<String> classpath, @NonNull final String classDirectory) {
    this.target = target;
    this.options = options;
    this.classpath = classpath;
    this.classDirectory = classDirectory;
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
  public List<String> getOptions() {
    return this.options;
  }

  public void setOptions(@NonNull final List<String> options) {
    this.options = Preconditions.checkNotNull(options, "options");
  }

  @Pure
  @NonNull
  public List<String> getClasspath() {
    return this.classpath;
  }

  public void setClasspath(@NonNull final List<String> classpath) {
    this.classpath = Preconditions.checkNotNull(classpath, "classpath");
  }

  @Pure
  @NonNull
  public String getClassDirectory() {
    return this.classDirectory;
  }

  public void setClassDirectory(@NonNull final String classDirectory) {
    this.classDirectory = Preconditions.checkNotNull(classDirectory, "classDirectory");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("options", this.options);
    b.add("classpath", this.classpath);
    b.add("classDirectory", this.classDirectory);
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
    JavacOptionsItem other = (JavacOptionsItem) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.options == null) {
      if (other.options != null)
        return false;
    } else if (!this.options.equals(other.options))
      return false;
    if (this.classpath == null) {
      if (other.classpath != null)
        return false;
    } else if (!this.classpath.equals(other.classpath))
      return false;
    if (this.classDirectory == null) {
      if (other.classDirectory != null)
        return false;
    } else if (!this.classDirectory.equals(other.classDirectory))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.options== null) ? 0 : this.options.hashCode());
    result = prime * result + ((this.classpath== null) ? 0 : this.classpath.hashCode());
    return prime * result + ((this.classDirectory== null) ? 0 : this.classDirectory.hashCode());
  }
}
