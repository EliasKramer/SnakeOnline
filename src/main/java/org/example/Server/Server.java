package org.example.Server;

import org.example.Game.*;
import org.example.Networking.ClientPackage.GamePackage;
import org.example.Networking.NetworkSettings;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class Server extends Thread {
    private String _name;
    private final SnakeGame _game;
    private boolean _running = true;
    //the update time in milliseconds. a
    private final long _stateUpdateCycle = 300;
    ServerSocket _server;
    private final Map<String, ServerClient> _clientMap;
    private final List<GamePackage> _removedSnakesPackages = new LinkedList<>();

    public Server(String givenName) {
        _name = givenName;
        _game = new SnakeGame(40, 20);
        try {
            _server = new ServerSocket(NetworkSettings.PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        _clientMap = new HashMap<>();

        startHandleUsersThread();
    }
    private void startHandleUsersThread() {
        //this is a thread that runs in the background and handles all the users that want to join
        Thread t = new Thread(() -> {
            while (_running) {
                try {
                    Socket client = _server.accept();
                    System.out.println("Client connected");

                    addClient(client);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
    private void addClient(Socket socket) {
        Snake addedSnake = _game.addSnake("Snake name");

        String id = socket.getInetAddress().toString();
        //add a new client to the server
        //this client controls the snake and manages the in and output
        _clientMap.put(id, new ServerClient(id, socket, this, _game, addedSnake));
    }

    public void removeClient(ServerClient serverClient) {
        //remove the snake from the client
        _clientMap.remove(serverClient.getId());
        //remove get the update packages after the snake is removed
        _removedSnakesPackages.addAll(serverClient.getDeletedSnakePackages());
    }
    @Override
    public void run() {
        long lastTimeUpdated = getCurrentTimeInMs();
        while (_running) {
            final long currTime = getCurrentTimeInMs();
            final long timeSinceLastUpdate = currTime - lastTimeUpdated;

            //if the time since the last update is greater than the update cycle, update the game state
            if (timeSinceLastUpdate >= _stateUpdateCycle) {
                //the game calculates the next update and returns a list of packages
                //these packages indicate what has changed in the game
                List<GamePackage> gamePackages = _game.nextUpdate();
                //if a user dies or disconnects a bunch of positions have to be updated
                //these are added here
                gamePackages.addAll(_removedSnakesPackages);

                //send the packages to all clients
                for (ServerClient client : _clientMap.values()) {
                    client.sendGamePackages(gamePackages);
                }
                lastTimeUpdated = currTime;
                //clear the list of removed snakes
                _removedSnakesPackages.clear();
            }
        }
    }
    private long getCurrentTimeInMs() {
        return Instant.now().toEpochMilli();
    }
}