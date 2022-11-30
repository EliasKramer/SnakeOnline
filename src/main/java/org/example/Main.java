package org.example;

import org.example.Game.Input.InputListener;
import org.example.Networking.NetworkSettings;
import org.example.Server.Server;
import org.example.client.Client;

public class Main {
    public static void main(String[] args) {
        Server server = new Server("ONLY SERVER", NetworkSettings.PORT);
        server.start();
        Client c = new Client("CLIENT 1", server);
        InputListener inputListener = new InputListener(c);

    }
}
