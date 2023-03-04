package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustCfgOptions {
  @NonNull
  private List<RustKeyValueMapper> keyValueOptions;

  @NonNull
  private List<String> nameOptions;

  public RustCfgOptions(@NonNull final List<RustKeyValueMapper> keyValueOptions, @NonNull final List<String> nameOptions) {
    this.keyValueOptions = keyValueOptions;
    this.nameOptions = nameOptions;
  }

  @Pure
  @NonNull
  public List<RustKeyValueMapper> getKeyValueOptions() {
    return this.keyValueOptions;
  }

  public void setKeyValueOptions(@NonNull final List<RustKeyValueMapper> keyValueOptions) {
    this.keyValueOptions = Preconditions.checkNotNull(keyValueOptions, "keyValueOptions");
  }

  @Pure
  @NonNull
  public List<String> getNameOptions() {
    return this.nameOptions;
  }

  public void setNameOptions(@NonNull final List<String> nameOptions) {
    this.nameOptions = Preconditions.checkNotNull(nameOptions, "nameOptions");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("keyValueOptions", this.keyValueOptions);
    b.add("nameOptions", this.nameOptions);
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
    RustCfgOptions other = (RustCfgOptions) obj;
    if (this.keyValueOptions == null) {
      if (other.keyValueOptions != null)
        return false;
    } else if (!this.keyValueOptions.equals(other.keyValueOptions))
      return false;
    if (this.nameOptions == null) {
      if (other.nameOptions != null)
        return false;
    } else if (!this.nameOptions.equals(other.nameOptions))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.keyValueOptions== null) ? 0 : this.keyValueOptions.hashCode());
    return prime * result + ((this.nameOptions== null) ? 0 : this.nameOptions.hashCode());
  }
}
