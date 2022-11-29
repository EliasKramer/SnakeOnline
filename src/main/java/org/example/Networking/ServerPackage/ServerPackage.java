package org.example.Networking.ServerPackage;

import org.example.Server.Server;

//this package can only be received and executed by the server
public interface ServerPackage {
    public void executeOnServer(Server server);
}
