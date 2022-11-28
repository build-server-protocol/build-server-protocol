package ch.epfl.scala.bsp4j;

import org.checkerframework.dataflow.qual.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;


@SuppressWarnings("all")
public class RustBuildTarget {
    
    private String compiler;
    
    private String edition;
    
    public RustBuildTarget(final String edition, final String compiler) {
        this.compiler = compiler;
        this.edition = edition;
    }
    
    @Pure
    public String getCompiler() {
        return compiler;
    }
    
    public void setCompiler(final String compiler) {
        this.compiler = compiler;
    }
    
    @Pure
    public String getEdition() {
        return edition;
    }
    
    public void setEdition(final String edition) {
        this.edition = edition;
    }

    @Override
    @Pure
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this);
        b.add("compiler", this.compiler);
        b.add("edition", this.edition);
        return b.toString();
    }

    
    @Override
    @Pure
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RustBuildTarget other = (RustBuildTarget) obj;
        if (this.compiler == null) {
            if (other.compiler != null)
                return false;
        } else if (!this.compiler.equals(other.compiler))
            return false;
        if (this.edition == null) {
            if (other.edition != null)
                return false;
        } else if (!this.edition.equals(other.edition))
            return false;
        return true;
        
    }

    @Override
    @Pure
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.compiler== null) ? 0 : this.compiler.hashCode());
        return prime * result + ((this.edition== null) ? 0 : this.edition.hashCode());
    }
}
