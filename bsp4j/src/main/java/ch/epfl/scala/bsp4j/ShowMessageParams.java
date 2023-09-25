package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ShowMessageParams {
  @NonNull
  private MessageType type;

  private TaskId task;

  private String originId;

  @NonNull
  private String message;

  public ShowMessageParams(@NonNull final MessageType type, @NonNull final String message) {
    this.type = type;
    this.message = message;
  }

  @Pure
  @NonNull
  public MessageType getType() {
    return this.type;
  }

  public void setType(@NonNull final MessageType type) {
    this.type = Preconditions.checkNotNull(type, "type");
  }

  @Pure
  public TaskId getTask() {
    return this.task;
  }

  public void setTask(final TaskId task) {
    this.task = task;
  }

  @Pure
  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(final String originId) {
    this.originId = originId;
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
    b.add("type", this.type);
    b.add("task", this.task);
    b.add("originId", this.originId);
    b.add("message", this.message);
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
    ShowMessageParams other = (ShowMessageParams) obj;
    if (this.type == null) {
      if (other.type != null)
        return false;
    } else if (!this.type.equals(other.type))
      return false;
    if (this.task == null) {
      if (other.task != null)
        return false;
    } else if (!this.task.equals(other.task))
      return false;
    if (this.originId == null) {
      if (other.originId != null)
        return false;
    } else if (!this.originId.equals(other.originId))
      return false;
    if (this.message == null) {
      if (other.message != null)
        return false;
    } else if (!this.message.equals(other.message))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.type== null) ? 0 : this.type.hashCode());
    result = prime * result + ((this.task== null) ? 0 : this.task.hashCode());
    result = prime * result + ((this.originId== null) ? 0 : this.originId.hashCode());
    return prime * result + ((this.message== null) ? 0 : this.message.hashCode());
  }
}
