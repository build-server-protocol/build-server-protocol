package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EnumTypeAdapter;

import java.util.Arrays;
import java.util.Optional;

@JsonAdapter(EnumTypeAdapter.Factory.class)
public enum CppPlatform {

    CPP93("C++93"),
    CPP03("C++03"),
    CPP11("C++11"),
    CPP14("C++14"),
    CPP17("C++17"),
    CPP20("C++20");

    private final String value;

    CppPlatform(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CppPlatform forValue(String value) {
        Optional<CppPlatform> currentPlatform = Arrays.stream(CppPlatform.values()).filter(platform -> platform.value.equals(value)).findFirst();
        return currentPlatform.orElseThrow(() -> new IllegalArgumentException("Illegal enum value: " + value));
    }
}
