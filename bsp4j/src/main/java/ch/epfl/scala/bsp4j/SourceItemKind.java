package ch.epfl.scala.bsp4j;

public enum SourceItemKind {

    SOURCE(1),
    RESOURCE(2),
    EXCLUDED(3);

    private final int value;

    SourceItemKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static SourceItemKind forValue(int value) {
        SourceItemKind[] allValues = SourceItemKind.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
