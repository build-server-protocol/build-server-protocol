package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustOptionsItem {
    @NonNull
    private BuildTargetIdentifier target;

    //TODO add Rust-specific fields

    public RustOptionsItem(@NonNull final BuildTargetIdentifier target) {
        this.target = target;
    }

    @Pure
    @NonNull
    public BuildTargetIdentifier getTarget() {
        return this.target;
    }

    public void setTarget(@NonNull final BuildTargetIdentifier target) {
        this.target = Preconditions.checkNotNull(target, "target");
    }


    @Override
    @Pure
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this);
        b.add("target", this.target);
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
        RustOptionsItem other = (RustOptionsItem) obj;
        if (this.target == null) {
            if (other.target != null)
                return false;
        } else if (!this.target.equals(other.target))
            return false;
        return true;
    }

    @Override
    @Pure
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        return prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    }
}
