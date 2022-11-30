package org.example.Game;

import org.example.Networking.ClientPackage.GamePackage;
import org.example.Networking.ClientPackage.MovePackage;

import java.util.LinkedList;
import java.util.List;

public class SnakeGame {
    private FieldValue[][] _board;
    private Colors[][] _colorBoard;
    private LinkedList<Snake> _snakes;
    private Colors _foodColor;
    private Position _foodPosition;
    public SnakeGame(int size){
        this(size, size);
    }
    public SnakeGame(int width, int height) {
        _board = new FieldValue[width][height];
        _colorBoard = new Colors[width][height];
        _snakes = new LinkedList<>();
        _foodPosition = null;
        _foodColor = Colors.CYAN;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                _board[i][j] = FieldValue.EMPTY;
                _colorBoard[i][j] = Colors.RESET;
            }
        }
    }

    public void printBoard() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                sb.append(_colorBoard[j][i].getValue());
                sb.append(_board[j][i].getValue());
                sb.append(" ");
                sb.append(Colors.RESET.getValue());
            }
            sb.append("\n");
        }

        System.out.println(sb.toString());
    }

    public void addSnake(String id, Colors givenColor, String givenName) {
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

    public void processNextUpdate() {
        List<GamePackage> gamePackages = getPositionChangesForNewUpdate();
        for (GamePackage curr : gamePackages) {
            processGamePackage(curr);
        }
    }
    public void updateFood() {
        if (foodIsEaten()) {
            _foodPosition = getRandomEmptyPosition();
            setValueAtPosition(new GamePackage(_foodPosition, _foodColor, FieldValue.FOOD));
        }
    }
    public List<GamePackage> getBoardInGamePackages()
    {
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
    private List<GamePackage> getPositionChangesForNewUpdate() {
        LinkedList<GamePackage> gamePackages = new LinkedList<>();
        LinkedList<Snake> snakesToRemove = new LinkedList<>();
        for (Snake snake : _snakes) {
            MovePackage movePackage = snake.getNextMovePackage();

            Position nextHeadPos = movePackage.getAddPackage().getPosition();
            //next head position will be off board
            if (!positionIsOnBoard(nextHeadPos) ||
                    positionCollidesWithAnyBody(nextHeadPos)
            ) {
                //kill snake
                gamePackages.addAll(snake.deleteSnake());
                snakesToRemove.add(snake);
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

        for (Snake snake : snakesToRemove) {
            _snakes.remove(snake);
        }

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

    public List<GamePackage> fullGamePackageBoard() {
        List<GamePackage> gamePackages = new LinkedList<>();
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                gamePackages.add(new GamePackage(
                        new Position(i, j),
                        _colorBoard[i][j],
                        _board[i][j]));
            }
        }
        return gamePackages;
    }
}
