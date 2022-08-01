package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum OutputPathItemKind {

    FILE(1),
    DIRECTORY(2);

    private final int value;

    OutputPathItemKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static OutputPathItemKind forValue(int value) {
        OutputPathItemKind[] allValues = OutputPathItemKind.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }
}
