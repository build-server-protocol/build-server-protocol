package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum TestStatus {
  PASSED(1),
  FAILED(2),
  IGNORED(3),
  CANCELLED(4),
  SKIPPED(5);

  private final int value;

  TestStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static TestStatus forValue(int value) {
    TestStatus[] allValues = TestStatus.values();
    if (value < 1 || value > allValues.length)
      throw new IllegalArgumentException("Illegal enum value: " + value);
    return allValues[value - 1];
  }
}
