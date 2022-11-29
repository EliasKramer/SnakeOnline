package org.example;

import org.example.Game.Renderer;
import org.example.Game.SnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class Main extends JFrame {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Main m = new Main();
            m.setVisible(true);
        });
    }

    public Main() {
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