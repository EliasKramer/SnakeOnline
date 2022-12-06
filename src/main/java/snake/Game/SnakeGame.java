package snake.Game;

import snake.Networking.ClientPackage.GamePackage;
import snake.Networking.ClientPackage.MovePackage;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class SnakeGame {
    private final FieldValue[][] _board;
    private final Color[][] _colorBoard;
    private final LinkedList<Snake> _snakes;
    private final Color _foodColor;
    private Position _foodPosition;
    private final List<Snake> _deadSnakes;
    private final List<Snake> _snakesToRemove;

    public SnakeGame(int size) {
        this(size, size);
    }

    public SnakeGame(int x, int y) {
        _board = new FieldValue[x][y];
        _colorBoard = new Color[x][y];
        _snakes = new LinkedList<>();
        _deadSnakes = new LinkedList<>();
        _snakesToRemove = new LinkedList<>();
        _foodPosition = null;
        _foodColor = Color.CYAN;
        Color _noFieldColor = ColorManager.getInstance().getEnvironmentColor();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                setValueAtPosition(new GamePackage(new Position(i,j), _noFieldColor, FieldValue.EMPTY));
            }
        }
    }

    public void printBoard() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                //sb.append(ColorManager.getInstance().getco getColorAtPosition(new Position(i, j)));
                sb.append(getValueAtPosition(new Position(j, i)).getValue());
                sb.append(" ");
                //sb.append(ColorManager.getInstance().getResetColor());
            }
            sb.append("\n");
        }

        System.out.println(sb);
    }
    public Snake addSnake(String givenName) {
        Color color = new Color((int)(Math.random()*0x1000000));
        Position randomPos = getRandomEmptyPosition();
        Direction direction = randomPos.getX() > getWidth()/2 ? Direction.LEFT : Direction.RIGHT;

        Snake snake = new Snake(color, givenName, direction, randomPos);
        _snakes.add(snake);
        GamePackage add = new GamePackage(
                snake.getHead(),
                snake.getColor(),
                FieldValue.SNAKE);
        processGamePackage(add);
        return snake;
    }
    //calculates next update and returns all position changes
    public List<GamePackage> nextUpdate() {
        List<GamePackage> gamePackages = calculateNewUpdate();
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
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                GamePackage curr = new GamePackage(new Position(i, j), _colorBoard[i][j], _board[i][j]);
                System.out.println("curr packaging "+curr);
                gamePackages.add(curr);
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
    public FieldValue getValueAtPosition(Position position) {
        if (positionIsOnBoard(position)) {
            return _board[position.getX()][position.getY()];
        }
        throw new IllegalArgumentException("Position is not on board. Cannot get value");
    }
    public Color getColorAtPosition(Position position) {
        if(positionIsOnBoard(position)) {
            return _colorBoard[position.getX()][position.getY()];
        }
        throw new IllegalArgumentException("Position is not on board. Cannot get color");
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
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (_board[i][j] == FieldValue.EMPTY) {
                    emptyFields.add(new Position(i, j));
                }
            }
        }
        return emptyFields;
    }
    //this function is not idempotent
    public List<GamePackage> calculateNewUpdate() {
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
        return position.getX() >= 0 && position.getX() < getWidth() &&
                position.getY() >= 0 && position.getY() < getHeight();
    }
    public int getHeight() {
        return _board[0].length;
    }
    public int getWidth() {
        return _board.length;
    }
}