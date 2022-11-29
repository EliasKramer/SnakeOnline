package org.example.Networking.ServerPackage;

import org.example.Server.Server;

public class InputPackage implements ServerPackage {
    private final int _userId;
    private final int _direction;
    private final long _timeStamp;

    public InputPackage(int userId, int direction, long timeStamp) {
        _userId = userId;
        _direction = direction;
        _timeStamp = timeStamp;
    }

    @Override
    public void executeOnServer(Server server) {
        server.handleInputPackage(this);
    }
}
