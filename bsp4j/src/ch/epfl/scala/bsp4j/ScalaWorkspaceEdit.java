package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/** A workspace edit represents changes to many resources managed in the workspace. */
@SuppressWarnings("all")
public class ScalaWorkspaceEdit {
  @NonNull private List<ScalaTextEdit> changes;

  public ScalaWorkspaceEdit(@NonNull final List<ScalaTextEdit> changes) {
    this.changes = changes;
  }

  @NonNull
  public List<ScalaTextEdit> getChanges() {
    return this.changes;
  }

  public void setChanges(@NonNull final List<ScalaTextEdit> changes) {
    this.changes = Preconditions.checkNotNull(changes, "changes");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("changes", this.changes);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ScalaWorkspaceEdit other = (ScalaWorkspaceEdit) obj;
    if (this.changes == null) {
      if (other.changes != null) return false;
    } else if (!this.changes.equals(other.changes)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31 * 1 + ((this.changes == null) ? 0 : this.changes.hashCode());
  }
}
