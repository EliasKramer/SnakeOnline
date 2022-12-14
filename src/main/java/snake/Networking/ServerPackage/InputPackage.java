package snake.Networking.ServerPackage;

import snake.Game.Direction;

import java.io.Serializable;

public class InputPackage implements Serializable {
    private final String _userId;
    private final Direction _direction;

    public InputPackage(String userId, Direction direction) {
        _userId = userId;
        _direction = direction;
    }

    public String getUserId() {
        return _userId;
    }

    public Direction getDirection() {
        return _direction;
    }

    @Override
    public String toString() {
        return "InputPackage{" +
                "_userId='" + _userId + '\'' +
                ", _direction=" + _direction +
                '}';
    }
}
