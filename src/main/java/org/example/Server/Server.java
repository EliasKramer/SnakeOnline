package org.example.Server;

import org.example.Game.*;
import org.example.Networking.ClientPackage.GamePackage;
import org.example.Networking.ServerPackage.InputPackage;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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

    private int _port;
    private Map<String, ServerClient> _clientMap;
    private List<GamePackage> _removedSnakes = new LinkedList<>();

    public Server(String givenName, int givenPort) {
        _name = givenName;
        _port = givenPort;
        _game = new SnakeGame(40, 20);
        try {
            _server = new ServerSocket(6969);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        _clientMap = new HashMap<>();

        startHandleUsersThread();
    }

    private void startHandleUsersThread() {
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

    @Override
    public void run() {
        long lastTimeUpdated = getCurrentTimeInMs();
        long iterationsBetweenUpdate = 0;
        long updateCounter = 0;
        _game.printBoard();
        System.out.println("height: " + _game.getHeight() + " width: " + _game.getWidth());
        while (_running) {
            final long currTime = getCurrentTimeInMs();
            final long timeSinceLastUpdate = currTime - lastTimeUpdated;

            if (timeSinceLastUpdate >= _stateUpdateCycle) {
                List<GamePackage> gamePackages = _game.nextUpdate();
                gamePackages.addAll(_removedSnakes);

                for (ServerClient client : _clientMap.values()) {
                    client.sendGamePackages(gamePackages);
                }
                //_game.printBoard();
                lastTimeUpdated = currTime;
                iterationsBetweenUpdate = 0;
                _removedSnakes.clear();
                if (updateCounter % 2 == 0) {
                    _game.printBoard();
                }
                updateCounter++;
            } else {
                //do nothing
                //inputs are handled in a separate thread
                iterationsBetweenUpdate++;
            }
        }
    }

    /*
        public void handleAddUserPackage(AddUserPackage givenPackage) {
            if(givenPackage == null) {
                throw new IllegalArgumentException("givenPackage cannot be null");
            }
            if(_clientInputMap.containsKey(givenPackage.getUserId())) {
                throw new IllegalArgumentException(givenPackage.getUserId()+" already exists and cannot be added again");
            }
            _clientInputMap.put(givenPackage.getUserId(), null);

            //random color
        }*/
    private void addClient(Socket socket) {
        Snake addedSnake = _game.addSnake("Snake name");

        String id = socket.getInetAddress().toString();
        _clientMap.put(id, new ServerClient(id, socket, this, _game, addedSnake));
    }

    private long getCurrentTimeInMs() {
        return Instant.now().toEpochMilli();
    }

    public void removeClient(ServerClient serverClient) {
        _clientMap.remove(serverClient.getId());
        _removedSnakes.addAll(serverClient.getDeletedSnakePackages());
    }
}