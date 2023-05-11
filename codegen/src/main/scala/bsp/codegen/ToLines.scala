package bsp.codegen

trait ToLines[A] {
  def lines(value: A): List[String]
}

object ToLines {

  def apply[A](value: A)(implicit ev: ToLines[A]): List[String] = ev.lines(value)

  implicit val stringToLines: ToLines[String] = (a: String) => List(a)

  implicit def flattenedLines[A: ToLines]: ToLines[List[A]] = (a: List[A]) => a.flatMap(ToLines(_))

  implicit def linesToLInes: ToLines[Lines] = (a: Lines) => a.get

}
