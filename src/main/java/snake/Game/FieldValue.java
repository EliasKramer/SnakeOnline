package snake.Game;

import java.io.Serializable;

public enum FieldValue implements Serializable {
    EMPTY("."),
    SNAKE("S"),
    FOOD("*");
    private final String _value;
    FieldValue(String value) {
        _value = value;
    }

    public String getValue()
    {
        return _value;
    }
}
