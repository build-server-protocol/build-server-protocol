package ch.epfl.scala.bsp4j;

/**
 * Task start notifications may contain an arbitrary interface in their `data`
 * field. The kind of interface that is contained in a notification must be
 * specified in the `dataKind` field.
 * 
 * There are predefined kinds of objects for compile and test tasks, as described
 * in [[bsp#BuildTargetCompile]] and [[bsp#BuildTargetTest]]
 */
public class TaskStartDataKind {
    public static final String COMPILE_TASK = "compile-task";
    public static final String TEST_START = "test-start";
    public static final String TEST_TASK = "test-task";
}
