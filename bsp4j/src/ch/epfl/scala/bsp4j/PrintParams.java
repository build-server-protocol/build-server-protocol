package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class PrintParams {
  @NonNull private String originId;

  private TaskId task;

  @NonNull private String message;

  public PrintParams(@NonNull final String originId, @NonNull final String message) {
    this.originId = originId;
    this.message = message;
  }

  @Pure
  @NonNull
  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(@NonNull final String originId) {
    this.originId = Preconditions.checkNotNull(originId, "originId");
  }

  @Pure
  public TaskId getTask() {
    return this.task;
  }

  public void setTask(final TaskId task) {
    this.task = task;
  }

  @Pure
  @NonNull
  public String getMessage() {
    return this.message;
  }

  public void setMessage(@NonNull final String message) {
    this.message = Preconditions.checkNotNull(message, "message");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("originId", this.originId);
    b.add("task", this.task);
    b.add("message", this.message);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PrintParams other = (PrintParams) obj;
    if (this.originId == null) {
      if (other.originId != null) return false;
    } else if (!this.originId.equals(other.originId)) return false;
    if (this.task == null) {
      if (other.task != null) return false;
    } else if (!this.task.equals(other.task)) return false;
    if (this.message == null) {
      if (other.message != null) return false;
    } else if (!this.message.equals(other.message)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.originId == null) ? 0 : this.originId.hashCode());
    result = prime * result + ((this.task == null) ? 0 : this.task.hashCode());
    return prime * result + ((this.message == null) ? 0 : this.message.hashCode());
  }
}
