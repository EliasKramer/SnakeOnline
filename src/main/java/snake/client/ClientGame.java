package snake.client;

import snake.Game.Direction;
import snake.Game.FieldValue;
import snake.Game.Input.InputListener;
import snake.Networking.ClientPackage.GamePackage;
import snake.Networking.NetworkSettings;
import snake.Networking.ServerPackage.InputPackage;
import snake.Game.Position;
import snake.Game.SnakeGame;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ClientGame {
    private final SnakeGame _snakeGame;
    private Socket _client;
    private ClientWindow _window;
    private ObjectInputStream _ois;
    private ObjectOutputStream _oos;
    private int _width;
    private int _height;

    private Position _snakeHeadPosition;

    public ClientGame(int width, int height) {
        _width = width;
        _height = height;
        //Scanner scanner = new Scanner(System.in);
        //System.out.print("Enter the server IP: ");
        String ip = "localhost";// scanner.nextLine();
        try {
            _client = new Socket(ip, NetworkSettings.PORT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not connect to server");
        }

        try {
            _oos = new ObjectOutputStream(new BufferedOutputStream(_client.getOutputStream()));
            _ois = new ObjectInputStream(new BufferedInputStream(_client.getInputStream()));

            height = _ois.readInt();
            width = _ois.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("initializing game with width: " + width + " and height: " + height);
        _snakeGame = new SnakeGame(width,height);
        /*
        _board = new FieldValue[width][height];
        _colorBoard = new Colors[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                _board[i][j] = FieldValue.EMPTY;
                _colorBoard[i][j] = Colors.RESET;
            }
        }
        */
        Thread t = new Thread(() -> {
            while (true) {
                processNextUpdate();
                if (_window != null) {
                    _window.repaint();
                }
            }
        });
        t.start();

        InputListener listener = new InputListener(this);
    }

    public void setWindow(ClientWindow window) {
        _window = window;
    }
    public SnakeGame getGame()
    {
        return _snakeGame;
    }

    public void processNextUpdate() {
        try {
            Object[] objects = (Object[]) _ois.readObject();
            int posX = _ois.readInt();
            int posY = _ois.readInt();
            _snakeHeadPosition = new Position(posX, posY);
            List<GamePackage> gamePackages = new LinkedList<>();
            for (Object o : objects) {
                //System.out.println(o);
                gamePackages.add((GamePackage) o);
            }
            _snakeGame.processGamePackages(gamePackages);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /*
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
        */
    public void sendInput(Direction direction) {
        try {
            System.out.println("Sending input");
            InputPackage inputPackage = new InputPackage(_client.getInetAddress().toString(), direction);
            System.out.println("input package " + inputPackage);
            _oos.writeObject(new InputPackage(_client.getInetAddress().toString(), direction));
            _oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRestart() {

    }

    public SnakeGame getSnakeGame() {
        return _snakeGame;
    }

    public int getHeight() {
        return _height;
    }

    public int getWidth() {
        return _width;
    }

    public Color getColorAtPosition(Position position) {
        if(_snakeHeadPosition == null) return Color.BLACK;
        Position actualPosition = getClippedPosition(position, _snakeHeadPosition);
        return _snakeGame.getColorAtPosition(actualPosition);
    }

    private Position getClippedPosition(Position position, Position snakeHeadPosition) {
        return Position.Add(getTopLeft(snakeHeadPosition), position);
    }

    private Position getTopLeft(Position snakeHeadPosition) {
        int x = Math.min(Math.max(snakeHeadPosition.getX(), _width/2), _snakeGame.getWidth() - _width/2);
        int y = Math.min(Math.max(snakeHeadPosition.getY(), _height/2), _snakeGame.getHeight() - _height/2);
        x -= _width/2;
        y -= _height/2;
        return new Position(x, y);
    }

    public FieldValue getValueAtPosition(Position position) {
        if(_snakeHeadPosition == null) return FieldValue.EMPTY;
        Position actualPosition = getClippedPosition(position, _snakeHeadPosition);
        return _snakeGame.getValueAtPosition(actualPosition);
    }
}
