package ch.epfl.scala.bsp.testkit.client

import ch.epfl.scala.bsp4j.{Diagnostic, DiagnosticSeverity, Range}

case class ExpectedDiagnostic(range: Range, severity: DiagnosticSeverity, code: String) {
  def isEqual(obj: Any): Boolean = {
    if (obj == null)
      false
    else
      obj match {
        case diagnostic: Diagnostic =>
          range == diagnostic.getRange && severity == diagnostic.getSeverity && code == diagnostic.getCode
        case expectedDiagnostic: ExpectedDiagnostic =>
          range == expectedDiagnostic.range && severity == expectedDiagnostic.severity && code == expectedDiagnostic.code
        case _ => false
      }
  }
}
