package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.JvmEnvironmentItem;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class JvmEnvironmentResult {
  @NonNull
  private List<JvmEnvironmentItem> entries;
  
  public JvmEnvironmentResult(@NonNull final List<JvmEnvironmentItem> entries) {
    this.entries = entries;
  }
  
  @Pure
  @NonNull
  public List<JvmEnvironmentItem> getEntries() {
    return this.entries;
  }
  
  public void setEntries(@NonNull final List<JvmEnvironmentItem> entries) {
    this.entries = entries;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("entries", this.entries);
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
    JvmEnvironmentResult other = (JvmEnvironmentResult) obj;
    if (this.entries == null) {
      if (other.entries != null)
        return false;
    } else if (!this.entries.equals(other.entries))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.entries== null) ? 0 : this.entries.hashCode());
  }
}
