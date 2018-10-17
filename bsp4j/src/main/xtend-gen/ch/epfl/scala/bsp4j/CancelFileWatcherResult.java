package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CancelFileWatcherResult {
  @NonNull
  private Boolean cancelled;
  
  public CancelFileWatcherResult(@NonNull final Boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  @Pure
  @NonNull
  public Boolean getCancelled() {
    return this.cancelled;
  }
  
  public void setCancelled(@NonNull final Boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("cancelled", this.cancelled);
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
    CancelFileWatcherResult other = (CancelFileWatcherResult) obj;
    if (this.cancelled == null) {
      if (other.cancelled != null)
        return false;
    } else if (!this.cancelled.equals(other.cancelled))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.cancelled== null) ? 0 : this.cancelled.hashCode());
  }
}
