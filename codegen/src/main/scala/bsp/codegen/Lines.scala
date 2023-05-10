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

  def block(l: Lines*): Lines = {
    val current = self.get
    val openBlock: List[String] =
      current.lastOption.map { line =>
        line.lastOption match {
          case None      => "{"
          case Some('}') => line + "{"
          case Some(')') => line + "{"
          case Some(_)   => line + " {"
        }
      } match {
        case Some(value) => current.dropRight(1) :+ value
        case None        => current
      }

    Lines(openBlock) ++ Lines(l: _*).indent ++ Lines("}")
  }
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
