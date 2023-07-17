package bsp.codegen

import cats.kernel.Monoid

case class RenderSettings(indent: String)

object Settings {
  implicit val java: RenderSettings = RenderSettings("    ")
  implicit val typescript: RenderSettings = RenderSettings("  ")
  implicit val scala: RenderSettings = RenderSettings("  ")
}

abstract class Lines(implicit val settings: RenderSettings) { self =>
  def get: List[String]

  def render = get.mkString(System.lineSeparator())

  def map(f: String => String) = Lines(get.map(f))

  def distinct: Lines = Lines(get.distinct)

  def sorted: Lines = Lines(get.sorted)

  def ++(other: Lines): Lines = new Lines {
    def get: List[String] = self.get ++ other.get
  }

  def indent: Lines = map { str =>
    if (str.nonEmpty) settings.indent + str else str
  }

  private def wrap(open: String, close: String, l: Lines*): Lines = {
    val current = self.get
    val openBlock: List[String] =
      current.lastOption.map { line =>
        line.lastOption match {
          case None    => open
          case Some(_) => s"$line $open"
        }
      } match {
        case Some(value) => current.dropRight(1) :+ value
        case None        => current
      }

    Lines(openBlock) ++ Lines(l: _*).indent ++ Lines(close)
  }

  def block(l: Lines*): Lines = wrap("{", "}", l: _*)

  def paren(l: Lines*): Lines = wrap("(", ")", l: _*)
}

object Lines {

  def empty(implicit settings: RenderSettings): Lines = new Lines {
    def get: List[String] = Nil
  }

  implicit def monoidLines(implicit settings: RenderSettings): Monoid[Lines] =
    Monoid.instance(empty, _ ++ _)

  def apply(lines: List[String])(implicit settings: RenderSettings): Lines = new Lines {
    def get: List[String] = lines
  }

  def make[A](lines: List[A])(implicit toLines: ToLines[A], settings: RenderSettings): Lines =
    new Lines {
      def get: List[String] = lines.flatMap(toLines.lines)
    }

  def apply(all: Lines*)(implicit settings: RenderSettings): Lines = new Lines {
    def get: List[String] = all.flatMap(_.get).toList
  }

  implicit def fromToLines[A: ToLines](a: A)(implicit settings: RenderSettings): Lines = new Lines {
    def get = ToLines(a)
  }

}
