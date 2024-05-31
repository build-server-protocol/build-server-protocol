package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

/** Included in notifications of tasks or requests to signal the completion state. */
@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum StatusCode {
  OK(1),
  ERROR(2),
  CANCELLED(3);

  private final int value;

  StatusCode(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static StatusCode forValue(int value) {
    StatusCode[] allValues = StatusCode.values();
    if (value < 1 || value > allValues.length)
      throw new IllegalArgumentException("Illegal enum value: " + value);
    return allValues[value - 1];
  }
}
