package ch.epfl.scala.bsp4j;

/**
 * Task finish notifications may contain an arbitrary interface in their `data` field. The kind of
 * interface that is contained in a notification must be specified in the `dataKind` field.
 *
 * <p>There are predefined kinds of objects for compile and test tasks, as described in
 * [[bsp#BuildTargetCompile]] and [[bsp#BuildTargetTest]]
 */
public class TaskFinishDataKind {
  public static final String COMPILE_REPORT = "compile-report";
  public static final String TEST_FINISH = "test-finish";
  public static final String TEST_REPORT = "test-report";
}
