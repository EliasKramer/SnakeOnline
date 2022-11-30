package org.example.Networking.ServerPackage;

import org.example.Game.Direction;
import org.example.Server.Server;

public class InputPackage implements ServerPackage {
    private final String _userId;
    private final Direction _direction;
    private final long _timeStamp;

    public InputPackage(String userId, Direction direction, long timeStamp) {
        _userId = userId;
        _direction = direction;
        _timeStamp = timeStamp;
    }

    @Override
    public void executeOnServer(Server server) {
        server.handleInputPackage(this);
    }

    public String getUserId() {
        return _userId;
    }

    public Direction getDirection() {
        return _direction;
    }

    public long getTimestamp() {
        return _timeStamp;
    }
}
