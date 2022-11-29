package org.example.Game;

import javax.swing.*;
import java.awt.*;

public class Renderer extends JPanel {
    private final SnakeGame snakeGame;

    public Renderer(SnakeGame snakeGame) {
        this.snakeGame = snakeGame;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        double dimX = (double) getWidth() / (double) snakeGame.getBoard().length;
        double dimY = (double) getHeight() / (double) snakeGame.getBoard()[0].length;

        double dim = Math.min(dimX, dimY);

        Font f = new Font("Arial", Font.PLAIN, (int)dim);
        g2.setFont(f);

        for (int i = 0; i < snakeGame.getBoard().length; i++) {
            for (int j = 0; j < snakeGame.getBoard()[i].length; j++) {
                var field = snakeGame.getBoard()[i][j];
                g2.drawString(field.getValue(), (int)(i*dim) + (int)(dim*0.3), (int)(j*dim) + (int)dim);
            }
        }
    }
}
