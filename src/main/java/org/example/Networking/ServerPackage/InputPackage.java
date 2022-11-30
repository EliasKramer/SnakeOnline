package org.example.Networking.ServerPackage;

import org.example.Game.Direction;
import org.example.Server.Server;

import java.io.Serializable;

public class InputPackage implements ServerPackage, Serializable {
    private final String _userId;
    private final Direction _direction;

    public InputPackage(String userId, Direction direction) {
        _userId = userId;
        _direction = direction;
    }

    @Override
    public void executeOnServer(Server server) {
    }

    public String getUserId() {
        return _userId;
    }

    public Direction getDirection() {
        return _direction;
    }
}
