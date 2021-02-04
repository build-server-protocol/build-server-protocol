package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum CppCompiler {
    GCC(1),
    CLANG(2),
    VISUAL_CPP(3);

    private final int value;

    CppCompiler(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static CppCompiler forValue(int value) {
        CppCompiler[] allValues = CppCompiler.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
