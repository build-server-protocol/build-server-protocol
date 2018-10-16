package ch.epfl.scala.bsp4j;

public enum MessageType {

    ERROR(1),
    WARNING(2),
    INFORMATION(3),
    LOG(4);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static MessageType forValue(int value) {
        MessageType[] allValues = MessageType.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
