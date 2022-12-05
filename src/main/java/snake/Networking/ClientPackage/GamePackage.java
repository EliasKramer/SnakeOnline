package snake.Networking.ClientPackage;

import snake.Game.ColorManager;
import snake.Game.FieldValue;
import snake.Game.Position;

import java.awt.*;
import java.io.Serializable;

public class GamePackage implements Serializable {
    private final Position _position;
    private final Color _color;
    private final FieldValue _fieldValue;

    public GamePackage(Position position, Color color, FieldValue fieldValue) {
        _position = position;
        _color = color;
        _fieldValue = fieldValue;
    }
    public static GamePackage getEmptyPackage(Position position) {
        return new GamePackage(position, ColorManager.getInstance().getEnvironmentColor(), FieldValue.EMPTY);
    }
    public Position getPosition() {
        return _position;
    }

    public Color getColor() {
        return _color;
    }

    public FieldValue getFieldValue() {
        return _fieldValue;
    }

    @Override
    public String toString() {
        return "GamePackage{" +
                "_position=" + _position.toString() +
                ", _color=" + _color +
                ", _fieldValue=" + _fieldValue +
                '}';
    }
}
