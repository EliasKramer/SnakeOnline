package org.example.client;

import org.example.Game.GUI.Renderer;
import org.example.Game.SnakeGame;

import javax.swing.*;
import java.awt.*;

public class ClientWindow extends JFrame {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ClientWindow m = new ClientWindow();
            m.setVisible(true);
        });
    }

    public ClientWindow() {
        initUI();
    }
    private void initUI() {
        add(new Renderer(new SnakeGame(10)));
        setSize(600, 600);
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}