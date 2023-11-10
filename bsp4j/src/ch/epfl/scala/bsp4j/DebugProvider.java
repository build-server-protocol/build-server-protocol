package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class DebugProvider {
  @NonNull private List<String> languageIds;

  public DebugProvider(@NonNull final List<String> languageIds) {
    this.languageIds = languageIds;
  }

  @Pure
  @NonNull
  public List<String> getLanguageIds() {
    return this.languageIds;
  }

  public void setLanguageIds(@NonNull final List<String> languageIds) {
    this.languageIds = Preconditions.checkNotNull(languageIds, "languageIds");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("languageIds", this.languageIds);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DebugProvider other = (DebugProvider) obj;
    if (this.languageIds == null) {
      if (other.languageIds != null) return false;
    } else if (!this.languageIds.equals(other.languageIds)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.languageIds == null) ? 0 : this.languageIds.hashCode());
  }
}
