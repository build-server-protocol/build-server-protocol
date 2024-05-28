package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CleanCacheResult {
  private String message;

  @NonNull private Boolean cleaned;

  public CleanCacheResult(@NonNull final Boolean cleaned) {
    this.cleaned = cleaned;
  }

  @Pure
  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  @Pure
  @NonNull
  public Boolean getCleaned() {
    return this.cleaned;
  }

  public void setCleaned(@NonNull final Boolean cleaned) {
    this.cleaned = Preconditions.checkNotNull(cleaned, "cleaned");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("message", this.message);
    b.add("cleaned", this.cleaned);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CleanCacheResult other = (CleanCacheResult) obj;
    if (this.message == null) {
      if (other.message != null) return false;
    } else if (!this.message.equals(other.message)) return false;
    if (this.cleaned == null) {
      if (other.cleaned != null) return false;
    } else if (!this.cleaned.equals(other.cleaned)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
    return prime * result + ((this.cleaned == null) ? 0 : this.cleaned.hashCode());
  }
}
