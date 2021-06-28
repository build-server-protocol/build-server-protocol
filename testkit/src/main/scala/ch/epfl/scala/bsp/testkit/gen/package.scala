package ch.epfl.scala.bsp.testkit

package object gen {

  object utils extends UtilGenerators with UtilShrinkers
  object bsp4jArbitrary
      extends Bsp4jArbitrary
      with Bsp4jShrinkers
      with UtilGenerators
      with UtilShrinkers
}
