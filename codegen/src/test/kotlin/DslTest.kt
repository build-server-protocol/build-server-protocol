import org.jetbrains.bsp.generators.dsl.CodeBlock
import org.jetbrains.bsp.generators.dsl.code
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DslTest {
    @Test
    fun empty() {
        val input = code { }
        assertEquals("", input.toString())
    }

    @Test
    fun oneNewline() {
        val input = code {
            newline()
        }
        assertEquals("\n", input.toString())
    }

    @Test
    fun blockDoesntAddNewline() {
        val input = code {
            block("class Costam") {
                -"1"
                -"2"
            }
        }

        val expected = """
            class Costam {
              1
              2
            }
            
        """.trimIndent()

        assertEquals(expected, input.toString())
    }

    @Test
    fun simpleTest() {
        val input = code {
            -"val x = 5"
            newline()
            block("class Costam") {
                -"1"
                -"2"
                newline()
                paren("fun foo") {
                    -"3"
                    -"4"
                }
                newline()
                paren("fun bar") {
                    -"5"
                    -"6"
                }
            }
        }

        val expected = """
            val x = 5

            class Costam {
              1
              2

              fun foo(
                3
                4
              )

              fun bar(
                5
                6
              )
            }
            
        """.trimIndent()

        assertEquals(expected, input.toString())
    }

    @Test
    fun multiLines() {
        val input = code {
            lines(listOf("a", "b", "c"))
            lines(listOf("4", "5", "6"), join = " + ", end = " = 15")
        }

        val expected = """
            a
            b
            c
            4 + 
            5 + 
            6 = 15
            
        """.trimIndent()

        assertEquals(expected, input.toString())
    }

    @Test
    fun multiLinesBlock() {
        val input = code {
            lines(listOf("a", "b", "c"))
            block("fun foo") {
                lines(listOf("4", "5", "6"), join = ",")
            }
        }

        val expected = """
            a
            b
            c
            fun foo {
              4,
              5,
              6
            }
            
        """.trimIndent()

        assertEquals(expected, input.toString())
    }

    @Test
    fun optionalString() {
        val input = code {
            -"val x = 5"
            -null
            -"val y = 6"
        }

        val expected = """
            val x = 5
            val y = 6
            
        """.trimIndent()

        assertEquals(expected, input.toString())
    }

    @Test
    fun removeNewline() {
        val actual = code {
            -"val x = 5"
            -"val y = 6"
            removeNewline()
            -" = 11"
        }

        val expected = """
            val x = 5
            val y = 6 = 11
            
        """.trimIndent()

        assertEquals(expected, actual.toString())
    }

    @Test
    fun removeNewlines() {
        val els = listOf("a", "b", "c")
        fun render(input: String): CodeBlock {
            return code {
                -"blah blah"
                -input
            }
        }
        val actual = code {
            lines(els.map {render(it).toString()}, join = ";")
        }

        val expected = """
            blah blah
            a;
            blah blah
            b;
            blah blah
            c
            
        """.trimIndent()

        assertEquals(expected, actual.toString())
    }

    @Test
    fun indentedLines() {
        val els = listOf("a", "b", "c")
        val actual = code {
            block("fun foo") {
                lines(els, join = ";")
            }
        }

        val expected = """
            fun foo {
              a;
              b;
              c
            }
            
        """.trimIndent()

        assertEquals(expected, actual.toString())
    }

    @Test
    fun indentedInclude() {
        val nested = code {
            -"val a = 1"
            -"val b = 2"
        }

        val input = code {
            -"val x = 5"
            block("fun foo") {
                include(nested)
            }
            -"val y = 6"
        }

        val expected = """
            val x = 5
            fun foo {
              val a = 1
              val b = 2
            }
            val y = 6
            
        """.trimIndent()

        assertEquals(expected, input.toString())
    }

    @Test
    fun nestedCode() {
        val nested = code {
            -"val a = 1"
            -"val b = 2"
        }

        val input = code {
            include(nested)
            -"val x = 5"
            code {
                -"val y = 6"
                -"val z = 7"
            }
        }

        val expected = """
            val a = 1
            val b = 2
            val x = 5
            val y = 6
            val z = 7
            
        """.trimIndent()

        assertEquals(expected, input.toString())
    }
}