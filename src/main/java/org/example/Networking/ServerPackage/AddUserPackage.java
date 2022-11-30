package org.example.Networking.ServerPackage;

import org.example.Server.Server;

public class AddUserPackage implements ServerPackage {
    private final int _userId;

    public AddUserPackage(int userId) {
        _userId = userId;
    }

    public int getUserId() {
        return _userId;
    }

    @Override
    public void executeOnServer(Server server) {
        server.handleAddUserPackage(this);
    }
}