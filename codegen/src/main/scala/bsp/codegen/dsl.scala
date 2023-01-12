package bsp.codegen

object dsl {

  def empty: Lines = Lines.empty

  def lines(lines: Lines*): Lines = Lines(lines: _*)

  def newline: Lines = Lines("")

  def block(line: String)(lines: Lines*): Lines =
    Lines(line).block(lines: _*)

}
