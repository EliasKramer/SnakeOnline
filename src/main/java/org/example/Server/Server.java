package org.example.Server;

import org.example.Game.*;
import org.example.Networking.ServerPackage.AddUserPackage;
import org.example.Networking.ServerPackage.InputPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Server extends Thread {
    private String _name;
    private final SnakeGame _game;
    private boolean _running = true;
    //new game state every 1000ms
    private final float _stateUpdateCycle = 1000f;
    //UDP
    private DatagramSocket _socket;
    private final int _port;
    private Map<String, InputPackage> _clientInputMap;

    public Server(String givenName, int givenPort) {
        _name = givenName;
        _port = givenPort;
        _game = new SnakeGame(10, 10);
        _clientInputMap = new HashMap<String, InputPackage>();
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
        _game.updateFood();
        _game.printBoard();
        while (_running) {
            final long currTime = getCurrentTimeInMs();
            final long timeSinceLastUpdate = currTime - lastTimeUpdated;

            if (timeSinceLastUpdate >= _stateUpdateCycle) {
                for (InputPackage
                        inputPackage : _clientInputMap.values()) {
                    if(inputPackage != null) {
                        _game.setSnakeDirection(inputPackage.getUserId(), inputPackage.getDirection());
                    }
                }

                System.out.println("game update. iterationsBetween: " + iterationsBetweenUpdate);
                _game.updateFood();
                _game.processNextUpdate();
                _game.printBoard();
                lastTimeUpdated = currTime;
                iterationsBetweenUpdate = 0;
                _clientInputMap.clear();
            } else {
                //do nothing
                //inputs are handled in a separate thread
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
        if(givenPackage == null) {
            throw new IllegalArgumentException("givenPackage cannot be null");
        }
        if(_clientInputMap.containsKey(givenPackage.getUserId())) {
            throw new IllegalArgumentException(givenPackage.getUserId()+" already exists and cannot be added again");
        }
        _clientInputMap.put(givenPackage.getUserId(), null);
        //TODO add color
        _game.addSnake(givenPackage.getUserId(), Colors.GREEN, "Snake name");
    }

    public void handleInputPackage(InputPackage givenPackage) {
        if(givenPackage == null) {
            throw new IllegalArgumentException("givenPackage cannot be null");
        }
        System.out.println("received input package from " + givenPackage.getUserId() + " with direction " + givenPackage.getDirection());

        InputPackage currentSavedInput = null;
        if (_clientInputMap.containsKey(givenPackage.getUserId())) {
            currentSavedInput = _clientInputMap.get(givenPackage.getUserId());
        } else {
            System.out.println("A user with the id " + givenPackage.getUserId() +
                    " tried to send an input package but was not found in the server's input map.");
        }
        if(currentSavedInput == null)
        {
            System.out.println("the saved input was null");
        }
        else{
            System.out.println("timestamp new: " + givenPackage.getTimestamp() + " saved: " + currentSavedInput.getTimestamp());
            System.out.println("new one is greater: " + (givenPackage.getTimestamp() > currentSavedInput.getTimestamp()));
        }
        //if a recent package exists for this user, replace it (only if it is more recent)
        if (currentSavedInput != null && currentSavedInput.getTimestamp() < givenPackage.getTimestamp()) {
            System.out.println("updating input package for " + givenPackage.getUserId());
            _clientInputMap.put(givenPackage.getUserId(), givenPackage);
        }
        //if no recent package exists for this user, add it
        else if (currentSavedInput == null) {
            System.out.println("adding input package for " + givenPackage.getUserId());
            _clientInputMap.put(givenPackage.getUserId(), givenPackage);
        }
        //else: the current package is more recent than the saved one, so do nothing
    }
    private long getCurrentTimeInMs() {
        return Instant.now().toEpochMilli();
    }
}