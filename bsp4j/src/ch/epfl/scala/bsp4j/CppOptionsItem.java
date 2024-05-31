package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class CppOptionsItem {
  @NonNull private BuildTargetIdentifier target;

  @NonNull private List<String> copts;

  @NonNull private List<String> defines;

  @NonNull private List<String> linkopts;

  private Boolean linkshared;

  public CppOptionsItem(
      @NonNull final BuildTargetIdentifier target,
      @NonNull final List<String> copts,
      @NonNull final List<String> defines,
      @NonNull final List<String> linkopts) {
    this.target = target;
    this.copts = copts;
    this.defines = defines;
    this.linkopts = linkopts;
  }

  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @NonNull
  public List<String> getCopts() {
    return this.copts;
  }

  public void setCopts(@NonNull final List<String> copts) {
    this.copts = Preconditions.checkNotNull(copts, "copts");
  }

  @NonNull
  public List<String> getDefines() {
    return this.defines;
  }

  public void setDefines(@NonNull final List<String> defines) {
    this.defines = Preconditions.checkNotNull(defines, "defines");
  }

  @NonNull
  public List<String> getLinkopts() {
    return this.linkopts;
  }

  public void setLinkopts(@NonNull final List<String> linkopts) {
    this.linkopts = Preconditions.checkNotNull(linkopts, "linkopts");
  }

  public Boolean getLinkshared() {
    return this.linkshared;
  }

  public void setLinkshared(final Boolean linkshared) {
    this.linkshared = linkshared;
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("copts", this.copts);
    b.add("defines", this.defines);
    b.add("linkopts", this.linkopts);
    b.add("linkshared", this.linkshared);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CppOptionsItem other = (CppOptionsItem) obj;
    if (this.target == null) {
      if (other.target != null) return false;
    } else if (!this.target.equals(other.target)) return false;
    if (this.copts == null) {
      if (other.copts != null) return false;
    } else if (!this.copts.equals(other.copts)) return false;
    if (this.defines == null) {
      if (other.defines != null) return false;
    } else if (!this.defines.equals(other.defines)) return false;
    if (this.linkopts == null) {
      if (other.linkopts != null) return false;
    } else if (!this.linkopts.equals(other.linkopts)) return false;
    if (this.linkshared == null) {
      if (other.linkshared != null) return false;
    } else if (!this.linkshared.equals(other.linkshared)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.copts == null) ? 0 : this.copts.hashCode());
    result = prime * result + ((this.defines == null) ? 0 : this.defines.hashCode());
    result = prime * result + ((this.linkopts == null) ? 0 : this.linkopts.hashCode());
    return prime * result + ((this.linkshared == null) ? 0 : this.linkshared.hashCode());
  }
}
