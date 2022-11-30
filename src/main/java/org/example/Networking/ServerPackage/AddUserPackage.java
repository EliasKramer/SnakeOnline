package org.example.Networking.ServerPackage;

import org.example.Server.Server;

public class AddUserPackage {
    private final String _userId;

    public AddUserPackage(String userId) {
        _userId = userId;
    }

    public String getUserId() {
        return _userId;
    }

    @Override
    public String toString() {
        return "AddUserPackage{" +
                "_userId='" + _userId + '\'' +
                '}';
    }
}