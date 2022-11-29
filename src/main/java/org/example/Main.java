package org.example;

import org.example.Networking.NetworkSettings;
import org.example.Server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server("ONLY SERVER", NetworkSettings.PORT);
        server.start();
    }
}
