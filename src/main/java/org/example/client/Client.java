package org.example.client;

import org.example.Game.Direction;
import org.example.Networking.ServerPackage.AddUserPackage;
import org.example.Networking.ServerPackage.InputPackage;
import org.example.Server.Server;

public class Client {
    private final String _id;
    private final Server _testServer;

    public Client(String givenId, Server givenServer) {
        _id = givenId;
        _testServer = givenServer;
        _testServer.handleAddUserPackage(new AddUserPackage(_id));
    }

    public String getId() {
        return _id;
    }

    public void sendInput(Direction direction) {
        _testServer.handleInputPackage(new InputPackage(_id, direction, System.currentTimeMillis()));
    }
}
