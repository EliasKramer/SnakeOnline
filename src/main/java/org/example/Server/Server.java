package org.example.Server;

import org.example.Game.*;
import org.example.Networking.ClientPackage.GamePackage;
import org.example.Networking.ServerPackage.AddUserPackage;
import org.example.Networking.ServerPackage.InputPackage;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
    private final long _stateUpdateCycle = 500;

    ServerSocket _server;

    private List<Socket> _clients = new LinkedList<>();

    private List<ObjectOutputStream> _oos = new LinkedList<>();

    private int _port;
    private Map<String, Direction> _clientInputMap;

    public Server(String givenName, int givenPort) {
        _name = givenName;
        _port = givenPort;
        _game = new SnakeGame(10, 10);
        try {
            _server = new ServerSocket(6969);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        _clientInputMap = new HashMap<>();


        startHandleUsersThread();
    }

    private void startHandleUsersThread() {
        Thread t = new Thread(() -> {
            while(_running) {
                try {
                    Socket client = _server.accept();
                    System.out.println("Client connected");
                    _clients.add(client);

                    handleAddUserPackage(new AddUserPackage(client.getInetAddress().toString()));

                    Thread clientInputThread = new Thread(() -> {
                        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()))) {
                            while(true) {
                                System.out.println("Listening for packages...");
                                InputPackage inputPackage = (InputPackage) ois.readObject();
                                _clientInputMap.put(client.getInetAddress().toString(), inputPackage.getDirection());
                            }
                        }catch(SocketException e) {
                            System.out.println("Client disconnected");
                            _clients.remove(client);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
                    clientInputThread.start();

                    try{
                        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
                        oos.flush();
                        _oos.add(oos);
                        oos.writeInt(_game.getHeight());
                        oos.writeInt(_game.getWidth());
                        oos.flush();

                        oos.writeObject(_game.getBoardInGamePackages());
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
        long lastTimeUpdated = getCurrentTimeInMs();
        long iterationsBetweenUpdate = 0;
        _game.printBoard();
        while (_running) {
            final long currTime = getCurrentTimeInMs();
            final long timeSinceLastUpdate = currTime - lastTimeUpdated;

            if (timeSinceLastUpdate >= _stateUpdateCycle) {
                for(String userId : _clientInputMap.keySet()) {
                    if(_clientInputMap.get(userId) != null) {
                        _game.setSnakeDirection(userId, _clientInputMap.get(userId));
                    }
                }

                System.out.println("game update. iterationsBetween: " + iterationsBetweenUpdate);

                List<GamePackage> gamePackages = _game.nextUpdate();
                for(ObjectOutputStream oos : _oos) {
                    try{
                        oos.writeObject(gamePackages);
                        oos.flush();
                    } catch(SocketException e) {
                        System.out.println("Client disconnected");
                        _oos.remove(oos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //_game.printBoard();
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


    public void handleAddUserPackage(AddUserPackage givenPackage) {
        if(givenPackage == null) {
            throw new IllegalArgumentException("givenPackage cannot be null");
        }
        if(_clientInputMap.containsKey(givenPackage.getUserId())) {
            throw new IllegalArgumentException(givenPackage.getUserId()+" already exists and cannot be added again");
        }
        _clientInputMap.put(givenPackage.getUserId(), null);

        //random color
        Color color = new Color((int)(Math.random()*0x1000000));
        _game.addSnake(givenPackage.getUserId(),color, "Snake name");
    }

    private long getCurrentTimeInMs() {
        return Instant.now().toEpochMilli();
    }
}
