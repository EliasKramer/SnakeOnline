package snake.Game;

public enum Direction {
    UP(new Position(0,-1)),
    DOWN(new Position(0, 1)),
    LEFT(new Position(-1,0)),
    RIGHT(new Position(1, 0));

    private final Position _direction;
    Direction(Position direction) {
        _direction = direction;
    }

    public Position getDirection() {
        return _direction;
    }
}