package ch.epfl.scala.bsp.testkit

package object gen {

  object bsp4jArbitrary extends Bsp4jArbitrary with Bsp4jShrinkers with UtilShrinkers
}
