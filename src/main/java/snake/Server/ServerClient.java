package snake.Server;

import snake.Game.Snake;
import snake.Game.SnakeGame;
import snake.Networking.ClientPackage.GamePackage;
import snake.Networking.ServerPackage.InputPackage;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;


public class ServerClient {
    private final String _id;
    private final Server _server;
    private ObjectOutputStream _oos;
    private final Socket _socket;
    private Snake _snake;
    public ServerClient(String id, Socket socket, Server server, SnakeGame game, Snake snake) {
        _id = id;
        _socket = socket;
        _server = server;
        _snake = snake;
        createOutputStream();
        createInputStream();

        sendFirstPackage(game);
    }

    private void createInputStream() {
        Thread clientInputThread = new Thread(() -> {
            try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(_socket.getInputStream()))) {
                while(true) {
                    System.out.println("Listening for packages...");
                    InputPackage inputPackage = (InputPackage) ois.readObject();
                    System.out.println("Received package: " + inputPackage);
                    _snake.setDirection(inputPackage.getDirection());
                }
            }catch(SocketException e) {
                System.out.println("Client disconnected");
                removeSelf();
                //_game.removeSnake(client.getInetAddress().toString());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        clientInputThread.start();
    }

    private void createOutputStream() {
        try {
            _oos = new ObjectOutputStream(new BufferedOutputStream(_socket.getOutputStream()));
            _oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void sendFirstPackage(SnakeGame game) {
        if(game == null)
        {
            throw new IllegalArgumentException("Game cannot be null");
        }
        try {
            _oos.writeInt(game.getHeight());
            _oos.writeInt(game.getWidth());
            _oos.flush();
            _oos.writeObject(game.getBoardInGamePackages().toArray());
            _oos.writeInt(_snake.getHead().getX());
            _oos.writeInt(_snake.getHead().getY());
            _oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void removeSelf()
    {
        _server.removeClient(this);
    }
    public String getId()
    {
        return _id;
    }
    public void sendGamePackages(List<GamePackage> gamePackages)
    {
        try {
            _oos.writeObject(gamePackages.toArray());
            _oos.writeInt(_snake.getHead().getX());
            _oos.writeInt(_snake.getHead().getY());
            _oos.flush();
        } catch (SocketException e) {
            System.out.println("Client disconnected");
            removeSelf();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<GamePackage> getDeletedSnakePackages()
    {
        return _snake.deleteSnake();
    }
}