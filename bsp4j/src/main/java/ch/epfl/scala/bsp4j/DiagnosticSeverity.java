package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum DiagnosticSeverity {

    ERROR(1),
    WARNING(2),
    INFORMATION(3),
    HINT(4);

    private final int value;

    DiagnosticSeverity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DiagnosticSeverity forValue(int value) {
        DiagnosticSeverity[] allValues = DiagnosticSeverity.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }
}
