package org.example.Game;

public class Position {
    private int _x;
    private int _y;

    public Position(int x, int y) {
        _x = x;
        _y = y;
    }

    public static Position AddPositions(Position a, Position b) {
        return new Position(a._x + b._x, a._y + b._y);
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    public void setX(int x) {
        _x = x;
    }

    public void setY(int y) {
        _y = y;
    }
}
