package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class DebuggeeAddress {
  @NonNull
  private String originId;
  
  @NonNull
  private String uri;
  
  public DebuggeeAddress(@NonNull final String originId, @NonNull final String uri) {
    this.originId = originId;
    this.uri = uri;
  }
  
  @Pure
  @NonNull
  public String getOriginId() {
    return this.originId;
  }
  
  public void setOriginId(@NonNull final String originId) {
    this.originId = originId;
  }
  
  @Pure
  @NonNull
  public String getUri() {
    return this.uri;
  }
  
  public void setUri(@NonNull final String uri) {
    this.uri = uri;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("originId", this.originId);
    b.add("uri", this.uri);
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
    DebuggeeAddress other = (DebuggeeAddress) obj;
    if (this.originId == null) {
      if (other.originId != null)
        return false;
    } else if (!this.originId.equals(other.originId))
      return false;
    if (this.uri == null) {
      if (other.uri != null)
        return false;
    } else if (!this.uri.equals(other.uri))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.originId== null) ? 0 : this.originId.hashCode());
    return prime * result + ((this.uri== null) ? 0 : this.uri.hashCode());
  }
}
