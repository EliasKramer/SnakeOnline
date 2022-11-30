package org.example.client;

import org.example.Game.*;
import org.example.Networking.GamePackage;
import org.example.Networking.MovePackage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ClientGame {
    private FieldValue[][] _board;
    private Colors[][] _colorBoard;

    private Socket _client;

    private ClientWindow _window;

    private ObjectInputStream _ois;
    public ClientGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the server IP: ");
        String ip = scanner.nextLine();
        try {
            _client = new Socket(ip, 6969);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not connect to server");
        }

        int height;
        int width;
        try{
            _ois = new ObjectInputStream(new BufferedInputStream(_client.getInputStream()));
            height = _ois.readInt();
            width = _ois.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        _board = new FieldValue[width][height];
        _colorBoard = new Colors[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                _board[i][j] = FieldValue.EMPTY;
                _colorBoard[i][j] = Colors.RESET;
            }
        }

        Thread t = new Thread(() -> {
            while(true) {
                processNextUpdate();
                if(_window != null) {
                    _window.repaint();
                }
                printBoard();
            }
        });
        t.start();
    }

    public void setWindow(ClientWindow window) {
        _window = window;
    }

    public void processNextUpdate() {
        try{
            List<GamePackage> gamePackages = (List<GamePackage>) _ois.readObject();
            for (GamePackage curr : gamePackages) {
                processGamePackage(curr);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void processGamePackage(GamePackage gamePackage) {
        setValueAtPosition(gamePackage);
    }

    private List<Position> getEmptyFields() {
        List<Position> emptyFields = new LinkedList<>();
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                if (_board[i][j] == FieldValue.EMPTY) {
                    emptyFields.add(new Position(i, j));
                }
            }
        }
        return emptyFields;
    }

    private void setValueAtPosition(GamePackage gamePackage) {
        if (positionIsOnBoard(gamePackage.getPosition())) {
            _board
                    [gamePackage.getPosition().getX()]
                    [gamePackage.getPosition().getY()] =
                    gamePackage.getFieldValue();
            _colorBoard
                    [gamePackage.getPosition().getX()]
                    [gamePackage.getPosition().getY()] =
                    gamePackage.getColor();
        } else {
            throw new IllegalArgumentException("Position is not on board");
        }
    }

    private boolean positionIsOnBoard(Position position) {
        return position.getX() >= 0 && position.getX() < _board.length &&
                position.getY() >= 0 && position.getY() < _board[0].length;
    }

    public FieldValue[][] getBoard() {
        return _board;
    }

    public void printBoard() {
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                System.out.print(_board[i][j].getValue());
            }
            System.out.println();
        }
    }
}
