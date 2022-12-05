package snake.main;

import snake.client.ClientGame;
import snake.client.ClientWindow;

import java.awt.*;

public class ClientMain {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ClientGame clientGame = new ClientGame(10, 10);
            ClientWindow m = new ClientWindow(clientGame);
            clientGame.setWindow(m);
            m.setVisible(true);
        });
    }
}
