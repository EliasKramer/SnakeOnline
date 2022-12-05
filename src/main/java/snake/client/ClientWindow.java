package snake.client;

import snake.Game.GUI.Renderer;

import javax.swing.*;
import java.awt.*;

public class ClientWindow extends JFrame {
    public ClientWindow(ClientGame game) {
        initUI(game);
    }
    private void initUI(ClientGame game) {
        setBackground(Color.BLACK);
        add(new Renderer(game));
        setSize(800, 800);
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("src/main/resources/icon.png").getImage());
    }
}