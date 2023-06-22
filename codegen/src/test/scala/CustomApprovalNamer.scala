import com.spun.util.{ObjectUtils, StringUtils}
import com.spun.util.tests.{StackTraceReflectionResult, TestUtils}
import org.approvaltests.core.Options
import org.approvaltests.namer.{
  ApprovalNamer,
  AttributeStackSelector,
  NamerFactory,
  StackTraceNamer
}
import org.approvaltests.writers.Writer

import java.io.File

/// Approval test names must be unique across the class they're defined in.
class CustomApprovalNamer(name: String) extends ApprovalNamer {
  private val info = TestUtils.getCurrentFileForMethod(new AttributeStackSelector)

  override def getApprovalName: String = info.getClassName + "." + name

  override def getSourceFilePath: String = {
    getBaseDirectory
  }

  def getBaseDirectory: String = {
    info.getSourceFile.getParentFile.toPath.resolve("resources").toAbsolutePath.toString
  }

  override def getReceivedFile(extensionWithDot: String) = new File(
    getSourceFilePath + "/" + getApprovalName + Writer.received + extensionWithDot
  )

  override def getApprovedFile(extensionWithDot: String) = new File(
    getSourceFilePath + "/" + getApprovalName + Writer.approved + extensionWithDot
  )

  override def addAdditionalInformation(additionalInformation: String) = new CustomApprovalNamer(
    name + "." + additionalInformation
  )
}

object CustomApprovalNamer {
  def optionsFromName(name: String): Options = {
    new Options().forFile().withNamer(new CustomApprovalNamer(name))
  }
}
