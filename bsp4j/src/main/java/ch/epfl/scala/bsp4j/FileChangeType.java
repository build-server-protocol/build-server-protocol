package ch.epfl.scala.bsp4j;

public enum FileChangeType {

    CREATED(1),
    CHANGED(2),
    DELETED(3);

    private final int value;

    FileChangeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static FileChangeType forValue(int value) {
        FileChangeType[] allValues = FileChangeType.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
