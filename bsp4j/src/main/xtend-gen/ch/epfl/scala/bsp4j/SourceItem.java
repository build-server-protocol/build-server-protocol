package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class SourceItem {
  @NonNull
  private String uri;
  
  @NonNull
  private Boolean generated;
  
  private Boolean isDirectory;
  
  public SourceItem(@NonNull final String uri, final Boolean isDirectory, @NonNull final Boolean generated) {
    this.uri = uri;
    this.isDirectory = isDirectory;
    this.generated = generated;
  }
  
  @Pure
  @NonNull
  public String getUri() {
    return this.uri;
  }
  
  public void setUri(@NonNull final String uri) {
    this.uri = uri;
  }
  
  @Pure
  @NonNull
  public Boolean getGenerated() {
    return this.generated;
  }
  
  public void setGenerated(@NonNull final Boolean generated) {
    this.generated = generated;
  }
  
  @Pure
  public Boolean getIsDirectory() {
    return this.isDirectory;
  }
  
  public void setIsDirectory(final Boolean isDirectory) {
    this.isDirectory = isDirectory;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("uri", this.uri);
    b.add("generated", this.generated);
    b.add("isDirectory", this.isDirectory);
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
    SourceItem other = (SourceItem) obj;
    if (this.uri == null) {
      if (other.uri != null)
        return false;
    } else if (!this.uri.equals(other.uri))
      return false;
    if (this.generated == null) {
      if (other.generated != null)
        return false;
    } else if (!this.generated.equals(other.generated))
      return false;
    if (this.isDirectory == null) {
      if (other.isDirectory != null)
        return false;
    } else if (!this.isDirectory.equals(other.isDirectory))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.uri== null) ? 0 : this.uri.hashCode());
    result = prime * result + ((this.generated== null) ? 0 : this.generated.hashCode());
    return prime * result + ((this.isDirectory== null) ? 0 : this.isDirectory.hashCode());
  }
}
