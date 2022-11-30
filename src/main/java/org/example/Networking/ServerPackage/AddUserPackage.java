package org.example.Networking.ServerPackage;

import org.example.Server.Server;

public class AddUserPackage implements ServerPackage {
    private final String _userId;

    public AddUserPackage(String userId) {
        _userId = userId;
    }

    public String getUserId() {
        return _userId;
    }

    @Override
    public void executeOnServer(Server server) {
        server.handleAddUserPackage(this);
    }
}