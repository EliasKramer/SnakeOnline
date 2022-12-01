package org.example.Game;

import org.example.Networking.ClientPackage.GamePackage;
import org.example.Networking.ClientPackage.MovePackage;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Snake {
    private final Color _color;
    private final String _name;
    private Direction _direction;
    private int _savedFood = 0;
    private LinkedList<Position> _body;
    private String _id;
    public Snake(String id, Color color, String name, Direction direction, Position head) {
        _id = id;
        _color = color;
        _name = name;
        _direction = direction;
        _body = new LinkedList<>();
        _body.addFirst(head);
    }

    public Position getHead() {
        return _body.getFirst();
    }

    public String getId() {
        return _id;
    }

    public Color getColor() {
        return _color;
    }

    //this method does not update the next position of the snake
    public MovePackage getNextMovePackage() {
        //moving is always a process of moving the head in the direction
        GamePackage add = new GamePackage(
                Position.AddPositions(_body.getFirst(), _direction.getDirection()),
                _color,
                FieldValue.SNAKE);

        //if the snake has eaten food, it will not remove the tail
        if(_savedFood > 0){
            return new MovePackage(add);
        }

        //if the snake has not eaten food, it will remove the tail
        GamePackage remove = GamePackage.getEmptyPackage(_body.getLast());

        return new MovePackage(add, remove);
    }
    public void move()
    {
        _body.addFirst(Position.AddPositions(_body.getFirst(), _direction.getDirection()));
        if(_savedFood > 0)
        {
            _savedFood--;
        }
        else
        {
            _body.removeLast();
        }
    }
    public void eatFood() {
        //increase food. The next move will not remove the tail
        _savedFood++;
    }
    public List<GamePackage> deleteSnake() {
        //remove the snake from the board
        LinkedList<GamePackage> gamePackages = new LinkedList<>();
        for (Position position : _body) {
            gamePackages.add(GamePackage.getEmptyPackage(position));
        }
        return gamePackages;
    }
    public void setDirection(Direction direction) {

        _direction = direction;
    }
    public boolean pointOverlapsWithBody(Position point) {
        //check if the point is in the body of the snake
        for (Position bodyPoint : _body) {
            if (bodyPoint.equals(point)) {
                return true;
            }
        }
        return false;
    }
}
