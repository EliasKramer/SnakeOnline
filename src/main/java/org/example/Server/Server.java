package org.example.Server;

import org.example.Game.*;
import org.example.Networking.GamePackage;
import org.example.client.ClientWindow;
import org.example.Networking.ServerPackage.AddUserPackage;
import org.example.Networking.ServerPackage.InputPackage;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class Server extends Thread {
    private String _name;
    private SnakeGame _game;
    private boolean _running = true;
    //new game state every 1000ms
    private final float _stateUpdateCycle = 1000f;

    ServerSocket _server;

    private List<Socket> _clients = new LinkedList<>();

    private List<ObjectOutputStream> _oos = new LinkedList<>();

    private int _port;

    public Server(String givenName, int givenPort) {
        _name = givenName;
        _port = givenPort;
        _game = new SnakeGame(10, 10);
        try {
            _server = new ServerSocket(6969);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Thread t = new Thread(() -> {
            while(_running) {
                try {
                    Socket client = _server.accept();
                    System.out.println("Client connected");
                    _clients.add(client);
                    try{
                        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
                        _oos.add(oos);
                        oos.writeInt(_game.getHeight());
                        oos.writeInt(_game.getWidth());
                        oos.flush();

                        oos.writeObject(_game.fullGamePackageBoard());
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    public void run() {
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
                //_game.processNextUpdate();
                List<GamePackage> gamePackages = _game.getPositionChangesForNewUpdate();
                for(ObjectOutputStream oos : _oos) {
                    try{
                        oos.writeObject(gamePackages);
                        oos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
