package bsp.codegen

object dsl {

  def empty(implicit settings: RenderSettings): Lines = Lines.empty

  def lines(lines: Lines*)(implicit settings: RenderSettings): Lines = Lines(lines: _*)

  def newline(implicit settings: RenderSettings): Lines = Lines("")

  def block(line: String)(lines: Lines*)(implicit settings: RenderSettings): Lines =
    Lines(line).block(lines: _*)

  def paren(line: String)(lines: Lines*)(implicit settings: RenderSettings): Lines =
    Lines(line).paren(lines: _*)

}
