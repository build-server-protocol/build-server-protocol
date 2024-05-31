package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class JvmMainClass {
  @NonNull private String className;

  @NonNull private List<String> arguments;

  public JvmMainClass(@NonNull final String className, @NonNull final List<String> arguments) {
    this.className = className;
    this.arguments = arguments;
  }

  @Pure
  @NonNull
  public String getClassName() {
    return this.className;
  }

  public void setClassName(@NonNull final String className) {
    this.className = Preconditions.checkNotNull(className, "className");
  }

  @Pure
  @NonNull
  public List<String> getArguments() {
    return this.arguments;
  }

  public void setArguments(@NonNull final List<String> arguments) {
    this.arguments = Preconditions.checkNotNull(arguments, "arguments");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("className", this.className);
    b.add("arguments", this.arguments);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JvmMainClass other = (JvmMainClass) obj;
    if (this.className == null) {
      if (other.className != null) return false;
    } else if (!this.className.equals(other.className)) return false;
    if (this.arguments == null) {
      if (other.arguments != null) return false;
    } else if (!this.arguments.equals(other.arguments)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.className == null) ? 0 : this.className.hashCode());
    return prime * result + ((this.arguments == null) ? 0 : this.arguments.hashCode());
  }
}
