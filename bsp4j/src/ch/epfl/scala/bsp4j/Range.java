package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class Range {
  @NonNull
  private Position start;

  @NonNull
  private Position end;

  public Range(@NonNull final Position start, @NonNull final Position end) {
    this.start = start;
    this.end = end;
  }

  @Pure
  @NonNull
  public Position getStart() {
    return this.start;
  }

  public void setStart(@NonNull final Position start) {
    this.start = Preconditions.checkNotNull(start, "start");
  }

  @Pure
  @NonNull
  public Position getEnd() {
    return this.end;
  }

  public void setEnd(@NonNull final Position end) {
    this.end = Preconditions.checkNotNull(end, "end");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("start", this.start);
    b.add("end", this.end);
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
    Range other = (Range) obj;
    if (this.start == null) {
      if (other.start != null)
        return false;
    } else if (!this.start.equals(other.start))
      return false;
    if (this.end == null) {
      if (other.end != null)
        return false;
    } else if (!this.end.equals(other.end))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.start== null) ? 0 : this.start.hashCode());
    return prime * result + ((this.end== null) ? 0 : this.end.hashCode());
  }
}
