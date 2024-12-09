package tests

import scala.collection.mutable.ListBuffer
import scala.util.control.NonFatal

trait Cancelable {
  def cancel(): Unit
}
class OpenCancelable extends Cancelable {
  private val toCancel = ListBuffer.empty[Cancelable]
  def add(cancelable: Cancelable): this.type = {
    toCancel += cancelable
    this
  }
  override def cancel(): Unit = Cancelable.cancelAll(toCancel)
}
object Cancelable {
  def apply(fn: () => Unit): Cancelable = new Cancelable {
    override def cancel(): Unit = fn()
  }
  val empty: Cancelable = Cancelable(() => ())
  def cancelAll(iterable: Iterable[Cancelable]): Unit = {
    var errors = ListBuffer.empty[Throwable]
    iterable.foreach { cancelable =>
      try cancelable.cancel()
      catch { case ex if NonFatal(ex) => errors += ex }
    }
    errors.toList match {
      case head :: tail =>
        tail.foreach { e =>
          if (e ne head) {
            head.addSuppressed(e)
          }
        }
        throw head
      case _ =>
    }
  }
}
