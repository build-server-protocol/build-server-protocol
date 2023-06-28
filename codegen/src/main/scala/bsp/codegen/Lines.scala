package bsp.codegen

import cats.kernel.Monoid

trait Lines {
  self =>
  def get: List[String]

  def render = get.mkString(System.lineSeparator())

  def map(f: String => String) = Lines(get.map(f))

  def distinct: Lines = Lines(get.distinct)

  def sorted: Lines = Lines(get.sorted)

  def ++(other: Lines): Lines = new Lines {
    def get: List[String] = self.get ++ other.get
  }

  def indent: Lines = map { str =>
    if (str.nonEmpty) "  " + str else str
  }

  private def wrap(open: String, close: String, l: Lines*): Lines = {
    val current = self.get
    val openBlock: List[String] =
      current.lastOption.map { line =>
        line.lastOption match {
          case None      => open
          case Some('}') => line + open
          case Some(')') => line + open
          case Some(_)   => line + s" $open"
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

  object empty extends Lines {
    def get: List[String] = Nil
  }

  implicit val monoidLines: Monoid[Lines] = Monoid.instance(empty, _ ++ _)

  def apply(lines: List[String]): Lines = new Lines {
    def get: List[String] = lines
  }

  def make[A](lines: List[A])(implicit toLines: ToLines[A]): Lines = new Lines {
    def get: List[String] = lines.flatMap(toLines.lines(_))
  }

  def apply(all: Lines*): Lines = new Lines {
    def get: List[String] = all.flatMap(_.get).toList
  }

  implicit def fromToLines[A: ToLines](a: A): Lines = new Lines {
    def get = ToLines(a)
  }

}
