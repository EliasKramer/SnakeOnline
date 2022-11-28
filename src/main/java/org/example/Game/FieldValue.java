package org.example.Game;

public enum FieldValue {
    EMPTY("."),
    SNAKE("S"),
    FOOD("*");
    private final String _value;
    FieldValue(String value) {
        _value = value;
    }
}
