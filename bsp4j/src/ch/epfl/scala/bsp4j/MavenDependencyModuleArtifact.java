package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class MavenDependencyModuleArtifact {
  @NonNull private String uri;

  private String classifier;

  public MavenDependencyModuleArtifact(@NonNull final String uri) {
    this.uri = uri;
  }

  @Pure
  @NonNull
  public String getUri() {
    return this.uri;
  }

  public void setUri(@NonNull final String uri) {
    this.uri = Preconditions.checkNotNull(uri, "uri");
  }

  @Pure
  public String getClassifier() {
    return this.classifier;
  }

  public void setClassifier(final String classifier) {
    this.classifier = classifier;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("uri", this.uri);
    b.add("classifier", this.classifier);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    MavenDependencyModuleArtifact other = (MavenDependencyModuleArtifact) obj;
    if (this.uri == null) {
      if (other.uri != null) return false;
    } else if (!this.uri.equals(other.uri)) return false;
    if (this.classifier == null) {
      if (other.classifier != null) return false;
    } else if (!this.classifier.equals(other.classifier)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.uri == null) ? 0 : this.uri.hashCode());
    return prime * result + ((this.classifier == null) ? 0 : this.classifier.hashCode());
  }
}
