package org.example.Server;

import org.example.Game.*;
import org.example.Networking.ServerPackage.AddUserPackage;
import org.example.Networking.ServerPackage.InputPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.Instant;

public class Server extends Thread {
    private String _name;
    private SnakeGame _game;
    private boolean _running = true;
    //new game state every 1000ms
    private final float _stateUpdateCycle = 1000f;
    //UDP
    private DatagramSocket _socket;
    private int _port;

    public Server(String givenName, int givenPort) {
        _name = givenName;
        _port = givenPort;
        _game = new SnakeGame(10, 10);
    }

    public void initSocket(int givenPort) {
        try {
            _socket = new DatagramSocket(givenPort);
        } catch (SocketException e) {
            throw new RuntimeException("There was an exception while initializing the socket.\n" + e);
        }
    }

    @Override
    public void run() {
        initSocket(_port);
        System.out.println("Server started");
        runRequestHandler();

        long lastTimeUpdated = getCurrentTimeInMs();
        long iterationsBetweenUpdate = 0;
        _game.addSnake(Colors.BLUE, "blue_snake");
        _game.updateFood();
        _game.printBoard();
        while (_running) {
            final long currTime = getCurrentTimeInMs();
            final long timeSinceLastUpdate = currTime - lastTimeUpdated;

            if (timeSinceLastUpdate >= _stateUpdateCycle) {
                System.out.println("game update. iterationsBetween: " + iterationsBetweenUpdate);
                _game.updateFood();
                _game.processNextUpdate();
                _game.printBoard();
                lastTimeUpdated = currTime;
                iterationsBetweenUpdate = 0;
            } else {
                //get inputs

                iterationsBetweenUpdate++;
            }
        }
    }
    private void runRequestHandler() {
        Thread t = new Thread(() -> {
            while (_running) {
                //wait for requests
                DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                try {
                    _socket.receive(packet);
                } catch (Exception e) {
                    throw new RuntimeException("There was an exception while receiving a packet.\n" + e);
                }
            }
        });
        t.start();

        System.out.println("RequestHandler started");
    }

    public void handleAddUserPackage(AddUserPackage givenPackage) {
        //TODO;
    }
    public void handleInputPackage(InputPackage givenPackage) {
        //TODO;
    }
    private long getCurrentTimeInMs() {
        return Instant.now().toEpochMilli();
    }
}
