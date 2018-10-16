package ch.epfl.scala.bsp4j;

public enum StatusCode {

    OK(1),
    ERROR(2),
    CANCELLED(3);

    private final int value;

    StatusCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static StatusCode forValue(int value) {
        StatusCode[] allValues = StatusCode.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
