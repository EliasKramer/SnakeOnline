package org.example.client;

import org.example.Game.FieldValue;
import org.example.Game.GUI.Renderer;
import org.example.Game.Snake;
import org.example.Game.SnakeGame;

import javax.swing.*;
import java.awt.*;

public class ClientWindow extends JFrame {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ClientGame clientGame = new ClientGame();
            ClientWindow m = new ClientWindow(clientGame.getGame());
            clientGame.setWindow(m);
            m.setVisible(true);
        });
    }

    public ClientWindow(SnakeGame game) {
        initUI(game);
    }
    private void initUI(SnakeGame game) {
        setBackground(Color.BLACK);
        add(new Renderer(game));
        setSize(800, 800);
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("src/main/resources/icon.png").getImage());
    }
}