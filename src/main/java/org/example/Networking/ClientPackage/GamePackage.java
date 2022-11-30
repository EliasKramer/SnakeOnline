package org.example.Networking.ClientPackage;

import org.example.Game.Colors;
import org.example.Game.FieldValue;
import org.example.Game.Position;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class GamePackage implements Serializable {
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

    @Override
    public String toString() {
        return "GamePackage{" +
                "_position=" + _position +
                ", _color=" + _color +
                ", _fieldValue=" + _fieldValue +
                '}';
    }
}
