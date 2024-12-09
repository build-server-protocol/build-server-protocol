package ch.epfl.scala.bsp.testkit.client.mock

trait BspError extends Exception

case class BspConfigurationError(message: String) extends Exception(message) with BspError
