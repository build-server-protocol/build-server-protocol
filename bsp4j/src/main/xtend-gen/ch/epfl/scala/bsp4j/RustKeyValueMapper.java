package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustKeyValueMapper {
  @NonNull
  private String key;

  @NonNull
  private List<String> value;

  public RustKeyValueMapper(@NonNull final String key, @NonNull final List<String> value) {
    this.key = key;
    this.value = value;
  }

  @Pure
  @NonNull
  public String getKey() {
    return this.key;
  }

  public void setKey(@NonNull final String key) {
    this.key = Preconditions.checkNotNull(key, "key");
  }

  @Pure
  @NonNull
  public List<String> getValue() {
    return this.value;
  }

  public void setValue(@NonNull final List<String> value) {
    this.value = Preconditions.checkNotNull(value, "value");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("key", this.key);
    b.add("value", this.value);
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
    RustKeyValueMapper other = (RustKeyValueMapper) obj;
    if (this.key == null) {
      if (other.key != null)
        return false;
    } else if (!this.key.equals(other.key))
      return false;
    if (this.value == null) {
      if (other.value != null)
        return false;
    } else if (!this.value.equals(other.value))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.key== null) ? 0 : this.key.hashCode());
    return prime * result + ((this.value== null) ? 0 : this.value.hashCode());
  }
}
