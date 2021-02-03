package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum CppPlatform {

    CPP93(1),
    CPP03(2),
    CPP11(3),
    CPP14(4),
    CPP17(5),
    CPP20(6);

    private final int value;

    CppPlatform(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static CppPlatform forValue(int value) {
        CppPlatform[] allValues = CppPlatform.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
