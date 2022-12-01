package org.example.Game;

import org.example.Networking.ClientPackage.GamePackage;
import org.example.Networking.ClientPackage.MovePackage;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class SnakeGame {
    private FieldValue[][] _board;
    private Color[][] _colorBoard;
    private LinkedList<Snake> _snakes;
    private Color _foodColor;
    private Position _foodPosition;
    private List<Snake> _deadSnakes;
    private List<Snake> _snakesToRemove;
    private Color _noFieldColor = Color.white;
    public SnakeGame(int size) {
        this(size, size);
    }

    public SnakeGame(int width, int height) {
        _board = new FieldValue[width][height];
        _colorBoard = new Color[width][height];
        _snakes = new LinkedList<>();
        _deadSnakes = new LinkedList<>();
        _snakesToRemove = new LinkedList<>();
        _foodPosition = null;
        _foodColor = Color.CYAN;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                _board[i][j] = FieldValue.EMPTY;
                _colorBoard[i][j] = _noFieldColor;
            }
        }
    }

    public void printBoard() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                sb.append(ColorManager.getInstance().getColor(_colorBoard[j][i]));
                sb.append(_board[j][i].getValue());
                sb.append(" ");
                sb.append(ColorManager.getInstance().getResetColor());
            }
            sb.append("\n");
        }

        System.out.println(sb.toString());
    }

    public void addSnake(String id, Color givenColor, String givenName) {
        Snake snake = new Snake(id, givenColor, givenName, Direction.RIGHT, getRandomEmptyPosition());
        //TODO add system to not face wall when spawned
        _snakes.add(snake);
        GamePackage add = new GamePackage(
                snake.getHead(),
                snake.getColor(),
                FieldValue.SNAKE);
        processGamePackage(add);
    }

    public void setSnakeDirection(String id, Direction givenDirection) {
        for (Snake snake : _snakes) {
            if (snake.getId().equals(id)) {
                System.out.println("snake found setting direction to " + givenDirection);
                snake.setDirection(givenDirection);
                break;
            }
        }
    }

    //calculates next update and returns all position changes
    public List<GamePackage> nextUpdate() {
        List<GamePackage> gamePackages = getPositionChangesForNewUpdate();
        processGamePackages(gamePackages);
        return gamePackages;
    }

    public GamePackage updateFood() {
        if (foodIsEaten()) {
            _foodPosition = getRandomEmptyPosition();
            return new GamePackage(_foodPosition, _foodColor, FieldValue.FOOD);
        }
        return null;
    }

    public List<GamePackage> getBoardInGamePackages() {
        List<GamePackage> gamePackages = new LinkedList<>();
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                gamePackages.add(new GamePackage(new Position(i, j), _colorBoard[i][j], _board[i][j]));
            }
        }
        return gamePackages;
    }

    public void processGamePackage(GamePackage gamePackage) {
        setValueAtPosition(gamePackage);
    }

    public void processGamePackages(List<GamePackage> gamePackages) {
        for (GamePackage curr : gamePackages) {
            setValueAtPosition(curr);
        }
    }
    //removes snake in the next update
    public void removeSnake(String id) {
        for (Snake snake : _snakes) {
            if (snake.getId().equals(id)) {
                _snakesToRemove.add(snake);
                break;
            }
        }
    }
    private boolean foodIsEaten() {
        return _foodPosition == null;
    }

    private Position getRandomEmptyPosition() {
        var emptyFields = getEmptyFields();
        if (emptyFields.isEmpty()) {
            throw new IllegalStateException("There are no empty fields left");
        }

        int randomIndex = (int) (Math.random() * emptyFields.size());

        return emptyFields.get(randomIndex);
    }

    private List<Position> getEmptyFields() {
        List<Position> emptyFields = new LinkedList<>();
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                if (_board[i][j] == FieldValue.EMPTY) {
                    emptyFields.add(new Position(i, j));
                }
            }
        }
        return emptyFields;
    }

    //this function is not idempotent
    public List<GamePackage> getPositionChangesForNewUpdate() {
        LinkedList<GamePackage> gamePackages = new LinkedList<>();

        GamePackage newFoodLocation = updateFood();
        if(newFoodLocation != null) {
            gamePackages.add(newFoodLocation);
        }

        for (Snake snake : _snakes) {
            MovePackage movePackage = snake.getNextMovePackage();

            Position nextHeadPos = movePackage.getAddPackage().getPosition();
            //next head position will be off board
            if (!positionIsOnBoard(nextHeadPos) ||
                    positionCollidesWithAnyBody(nextHeadPos)
            ) {
                //kill snake
                _deadSnakes.add(snake);
                _snakesToRemove.add(snake);
            } else if (positionCollidesWithFood(nextHeadPos)) {
                //eat food
                snake.eatFood();
                gamePackages.add(movePackage.getAddPackage());
                _foodPosition = null;
                snake.move();
            } else {
                //move snake
                gamePackages.add(movePackage.getAddPackage());
                gamePackages.add(movePackage.getRemovePackage());
                snake.move();
            }
        }

        for (Snake snake : _snakesToRemove) {
            _snakes.remove(snake);
            gamePackages.addAll(snake.deleteSnake());
        }
        _snakesToRemove.clear();

        return gamePackages;
    }

    private boolean positionCollidesWithFood(Position nextHeadPos) {
        return !foodIsEaten() && nextHeadPos.equals(_foodPosition);
    }

    private FieldValue getValueAtPosition(Position position) {
        if (positionIsOnBoard(position)) {
            return _board[position.getX()][position.getY()];
        }
        throw new IllegalArgumentException("Position is not on board");
    }

    private void setValueAtPosition(GamePackage gamePackage) {
        if (positionIsOnBoard(gamePackage.getPosition())) {
            _board
                    [gamePackage.getPosition().getX()]
                    [gamePackage.getPosition().getY()] =
                    gamePackage.getFieldValue();
            _colorBoard
                    [gamePackage.getPosition().getX()]
                    [gamePackage.getPosition().getY()] =
                    gamePackage.getColor();
        } else {
            throw new IllegalArgumentException("Position is not on board");
        }
    }

    private boolean positionCollidesWithAnyBody(Position position) {
        for (Snake curr : _snakes) {
            if (curr.pointOverlapsWithBody(position)) {
                return true;
            }
        }
        return false;
    }

    private boolean positionIsOnBoard(Position position) {
        return position.getX() >= 0 && position.getX() < _board.length &&
                position.getY() >= 0 && position.getY() < _board[0].length;
    }

    public int getHeight() {
        return _board[0].length;
    }

    public int getWidth() {
        return _board.length;
    }

}
