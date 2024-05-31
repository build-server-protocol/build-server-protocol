package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

/**
 * Crate types (`lib`, `rlib`, `dylib`, `cdylib`, `staticlib`) are listed for `lib` and `example`
 * target kinds. For other target kinds `bin` crate type is listed.
 */
@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum RustCrateType {
  BIN(1),
  LIB(2),
  RLIB(3),
  DYLIB(4),
  CDYLIB(5),
  STATICLIB(6),
  PROC_MACRO(7),
  UNKNOWN(8);

  private final int value;

  RustCrateType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static RustCrateType forValue(int value) {
    RustCrateType[] allValues = RustCrateType.values();
    if (value < 1 || value > allValues.length)
      throw new IllegalArgumentException("Illegal enum value: " + value);
    return allValues[value - 1];
  }
}
