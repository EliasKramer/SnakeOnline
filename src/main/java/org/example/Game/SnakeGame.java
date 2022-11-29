package org.example.Game;

import org.example.Networking.GamePackage;
import org.example.Networking.MovePackage;

import java.util.LinkedList;
import java.util.List;

public class SnakeGame {
    private FieldValue[][] _board;
    private Colors[][] _colorBoard;
    private LinkedList<Snake> _snakes;
    public SnakeGame(int width, int height) {
        _board = new FieldValue[width][height];
        _colorBoard = new Colors[width][height];
        _snakes = new LinkedList<>();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                _board[i][j] = FieldValue.EMPTY;
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
    public void addSnake(Snake snake) {
        _snakes.add(snake);
        GamePackage add = new GamePackage(
                snake.getHead(),
                snake.getColor(),
                FieldValue.SNAKE);
        processGamePackage(add);
    }

    public void processNextUpdate()
    {
        List<GamePackage> gamePackages = getPositionChangesForNewUpdate();
        for(GamePackage curr: gamePackages)
        {
            processGamePackage(curr);
        }
    }

    private void processGamePackage(GamePackage gamePackage) {
        setValueAtPosition(gamePackage);
    }


    private List<GamePackage> getPositionChangesForNewUpdate(){
        LinkedList<GamePackage> gamePackages = new LinkedList<>();
        LinkedList<Snake> snakesToRemove = new LinkedList<>();
        for (Snake snake : _snakes) {
            MovePackage movePackage =  snake.getNextMovePackage();

            Position nextHeadPos = movePackage.getAddPackage().getPosition();
            //next head position will be off board
            if(!positionIsOnBoard(nextHeadPos) ||
                positionCollidesWithAnyBody(nextHeadPos)
            ){
                //kill snake
                gamePackages.addAll(snake.deleteSnake());
                snakesToRemove.add(snake);
            }
            else if(positionCollidesWithFood(nextHeadPos)){
                //eat food
                snake.eatFood();
                gamePackages.add(movePackage.getAddPackage());
                snake.move();
            }
            else{
                //move snake
                gamePackages.add(movePackage.getAddPackage());
                gamePackages.add(movePackage.getRemovePackage());
                snake.move();
            }
        }

        for(Snake snake: snakesToRemove){
            _snakes.remove(snake);
        }

        return gamePackages;
    }

    private boolean positionCollidesWithFood(Position nextHeadPos) {
        return getValueAtPosition(nextHeadPos) == FieldValue.FOOD;
    }
    private FieldValue getValueAtPosition(Position position){
        if(positionIsOnBoard(position)){
            return _board[position.getX()][position.getY()];
        }
        throw new IllegalArgumentException("Position is not on board");
    }
    private void setValueAtPosition(GamePackage gamePackage){
        if(positionIsOnBoard(gamePackage.getPosition())){
            _board
                    [gamePackage.getPosition().getX()]
                    [gamePackage.getPosition().getY()] =
                    gamePackage.getFieldValue();
            _colorBoard
                    [gamePackage.getPosition().getX()]
                    [gamePackage.getPosition().getY()] =
                    gamePackage.getColor();
        }
        else{
            throw new IllegalArgumentException("Position is not on board");
        }
    }
    private boolean positionCollidesWithAnyBody(Position position){
        for (Snake curr : _snakes) {
            if(curr.pointOverlapsWithBody(position)){
                return true;
            }
        }
        return false;
    }
    private boolean positionIsOnBoard(Position position) {
        return position.getX() >= 0 && position.getX() < _board.length &&
                position.getY() >= 0 && position.getY() < _board[0].length;
    }

    public FieldValue[][] getBoard() {
        return _board;
    }

    public void printBoard() {
        System.out.println(getBoardString());
    }
}
