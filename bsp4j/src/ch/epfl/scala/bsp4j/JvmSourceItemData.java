package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/** `JvmSourceItemData` contains JVM-specific metadata for a source item. */
@SuppressWarnings("all")
public class JvmSourceItemData {
  private String packageName;

  public JvmSourceItemData() {}

  public String getPackageName() {
    return this.packageName;
  }

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("packageName", this.packageName);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JvmSourceItemData other = (JvmSourceItemData) obj;
    if (this.packageName == null) {
      if (other.packageName != null) return false;
    } else if (!this.packageName.equals(other.packageName)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31 * 1 + ((this.packageName == null) ? 0 : this.packageName.hashCode());
  }
}
