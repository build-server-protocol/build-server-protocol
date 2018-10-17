package ch.epfl.scala.bsp4j;

public enum WatchKind {

    CREATE(1),
    CHANGE(2),
    DELETE(3);

    private final int value;

    WatchKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static WatchKind forValue(int value) {
        WatchKind[] allValues = WatchKind.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
