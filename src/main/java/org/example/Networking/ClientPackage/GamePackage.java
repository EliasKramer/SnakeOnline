package org.example.Networking.ClientPackage;

import org.example.Game.Colors;
import org.example.Game.FieldValue;
import org.example.Game.Position;

public class GamePackage {
    private final Position _position;
    private final Colors _color;
    private final FieldValue _fieldValue;

    public GamePackage(Position position, Colors color, FieldValue fieldValue) {
        _position = position;
        _color = color;
        _fieldValue = fieldValue;
    }

    public Position getPosition() {
        return _position;
    }

    public Colors getColor() {
        return _color;
    }

    public FieldValue getFieldValue() {
        return _fieldValue;
    }
}
