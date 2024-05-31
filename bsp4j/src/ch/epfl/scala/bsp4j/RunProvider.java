package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class RunProvider {
  @NonNull private List<String> languageIds;

  public RunProvider(@NonNull final List<String> languageIds) {
    this.languageIds = languageIds;
  }

  @NonNull
  public List<String> getLanguageIds() {
    return this.languageIds;
  }

  public void setLanguageIds(@NonNull final List<String> languageIds) {
    this.languageIds = Preconditions.checkNotNull(languageIds, "languageIds");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("languageIds", this.languageIds);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    RunProvider other = (RunProvider) obj;
    if (this.languageIds == null) {
      if (other.languageIds != null) return false;
    } else if (!this.languageIds.equals(other.languageIds)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31 * 1 + ((this.languageIds == null) ? 0 : this.languageIds.hashCode());
  }
}
