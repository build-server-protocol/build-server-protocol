package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum RustDepKind {

    UNCLASSIFIED(1),
    NORMAL(2),
    DEV(3),
    BUILD(4);

    private final int value;

    RustDepKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RustDepKind forValue(int value) {
        RustDepKind[] allValues = RustDepKind.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }
}
