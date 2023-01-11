package bsp.codegen

object dsl {

  def newline: Lines = Lines("")

  def block(line: String)(lines: Lines*): Lines =
    Lines(line).block(lines: _*)

}
