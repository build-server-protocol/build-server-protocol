package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum BuildTargetEventKind {

    CREATED(1),
    CHANGED(2),
    DELETED(3);

    private final int value;

    BuildTargetEventKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BuildTargetEventKind forValue(int value) {
        BuildTargetEventKind[] allValues = BuildTargetEventKind.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }
}
