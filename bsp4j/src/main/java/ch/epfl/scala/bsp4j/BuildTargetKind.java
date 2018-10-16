package ch.epfl.scala.bsp4j;

public enum BuildTargetKind {

    LIBRARY(1),
    TEST(2),
    APP(3),
    INTEGRATION_TEST(4),
    BENCH(5);

    private final int value;

    BuildTargetKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static BuildTargetKind forValue(int value) {
        BuildTargetKind[] allValues = BuildTargetKind.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
