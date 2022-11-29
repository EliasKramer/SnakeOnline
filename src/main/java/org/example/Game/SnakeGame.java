package org.example.Game;

public class SnakeGame {
    private FieldValue[][] _board;

    public SnakeGame(int width, int height) {
        _board = new FieldValue[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                _board[i][j] = FieldValue.SNAKE;
            }
        }
    }

    public SnakeGame(int size) {
        this(size, size);
    }

    public String getBoardString() {
        StringBuilder sb = new StringBuilder();
        for (FieldValue[] fieldValues : _board) {
            for (FieldValue fieldValue : fieldValues) {
                sb.append(fieldValue.getValue());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public FieldValue[][] getBoard() {
        return _board;
    }
}
