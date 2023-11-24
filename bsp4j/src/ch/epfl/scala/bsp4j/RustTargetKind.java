package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum RustTargetKind {

    LIB(1),
    BIN(2),
    TEST(3),
    EXAMPLE(4),
    BENCH(5),
    CUSTOM_BUILD(6),
    UNKNOWN(7);

    private final int value;

    RustTargetKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RustTargetKind forValue(int value) {
        RustTargetKind[] allValues = RustTargetKind.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }
}
