package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface ScalaBuildServer {
  /**
   * The build target scalac options request is sent from the client to the server to query for the
   * list of compiler options necessary to compile in a given list of targets.
   */
  @JsonRequest("buildTarget/scalacOptions")
  CompletableFuture<ScalacOptionsResult> buildTargetScalacOptions(ScalacOptionsParams params);

  /**
   * The Scala build target test classes request is sent from the client to the server to query for
   * the list of fully qualified names of test classes in a given list of targets.
   *
   * <p>This method can for example be used by a client to:
   *
   * <p>- Show a list of the discovered classes that can be tested. - Attach a "Run test suite"
   * button above the definition of a test suite via `textDocument/codeLens`.
   *
   * <p>(To render the code lens, the language server needs to map the fully qualified names of the
   * test targets to the defining source file via `textDocument/definition`. Then, once users click
   * on the button, the language server can pass the fully qualified name of the test class as an
   * argument to the `buildTarget/test` request.)
   *
   * <p>This request may trigger a compilation on the selected build targets. The server is free to
   * send any number of `build/task*`, `build/publishDiagnostics` and `build/logMessage`
   * notifications during compilation before completing the response.
   *
   * <p>The client will get a `originId` field in `ScalaTestClassesResult` if the `originId` field
   * in the `ScalaTestClassesParams` is defined.
   */
  @Deprecated
  @JsonRequest("buildTarget/scalaTestClasses")
  CompletableFuture<ScalaTestClassesResult> buildTargetScalaTestClasses(
      ScalaTestClassesParams params);

  /**
   * The build target main classes request is sent from the client to the server to query for the
   * list of main classes that can be fed as arguments to `buildTarget/run`. This method can be used
   * for the same use cases than the [Scala Test Classes Request](#scala-test-classes-request)
   * enables. This request may trigger a compilation on the selected build targets. The server is
   * free to send any number of `build/taskStart`, `build/taskProgress`, `build/taskFinish`,
   * `build/publishDiagnostics` and `build/logMessage` notifications during compilation before
   * completing the response. The client will get a `originId` field in `ScalaMainClassesResult` if
   * the `originId` field in the `ScalaMainClassesParams` is defined.
   */
  @Deprecated
  @JsonRequest("buildTarget/scalaMainClasses")
  CompletableFuture<ScalaMainClassesResult> buildTargetScalaMainClasses(
      ScalaMainClassesParams params);
}
