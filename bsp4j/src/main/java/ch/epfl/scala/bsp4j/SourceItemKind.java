package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum SourceItemKind {

    FILE(1),
    DIRECTORY(2);

    private final int value;

    SourceItemKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SourceItemKind forValue(int value) {
        SourceItemKind[] allValues = SourceItemKind.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }
}
