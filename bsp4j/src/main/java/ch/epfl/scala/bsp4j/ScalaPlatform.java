package ch.epfl.scala.bsp4j;

public enum ScalaPlatform {

    JVM(1),
    JS(2),
    NATIVE(3);

    private final int value;

    ScalaPlatform(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static ScalaPlatform forValue(int value) {
        ScalaPlatform[] allValues = ScalaPlatform.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
