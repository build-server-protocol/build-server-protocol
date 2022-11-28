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

    private List<String> compilerOptions;
    public RustOptionsItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> compilerOptions) {
        this.target = target;
        this.compilerOptions = compilerOptions;
    }

    @Pure
    @NonNull
    public BuildTargetIdentifier getTarget() {
        return this.target;
    }

    public void setTarget(@NonNull final BuildTargetIdentifier target) {
        this.target = Preconditions.checkNotNull(target, "target");
    }

    @Pure
    @NonNull
    public List<String> getCompilerOptions() {
        return this.compilerOptions;
    }

    public void setCompilerOptions(@NonNull final List<String> compilerOptions) {
        this.compilerOptions = Preconditions.checkNotNull(compilerOptions, "compilerOptions");
    }

    
    @Override
    @Pure
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this);
        b.add("target", this.target);
        b.add("compilerOptions", this.compilerOptions);
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
        if (this.compilerOptions == null) {
            if (other.compilerOptions != null)
                return false;
        } else if (!this.compilerOptions.equals(other.compilerOptions))
            return false;
        return true;
    }

    @Override
    @Pure
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
        return prime * result + ((this.compilerOptions== null) ? 0 : this.compilerOptions.hashCode());
    }
}
