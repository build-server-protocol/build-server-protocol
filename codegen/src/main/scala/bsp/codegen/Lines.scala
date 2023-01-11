package bsp.codegen

trait Lines { self =>
  def get: List[String]
  def ++(other: Lines): Lines = new Lines {
    def get: List[String] = self.get ++ other.get
  }
  def indent: Lines = new Lines {
    def get: List[String] = self.get.map { str =>
      if (str.nonEmpty) "  " + str else str
    }
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

  def apply(lines: List[String]): Lines = new Lines {
    def get: List[String] = lines
  }

  def apply(all: Lines*): Lines = new Lines {
    def get: List[String] = all.flatMap(_.get).toList
  }

  implicit def fromToLines[A: ToLines](a: A): Lines = new Lines { def get = ToLines(a) }

}
