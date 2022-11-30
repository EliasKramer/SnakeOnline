package org.example.Game.GUI;

import org.example.Game.FieldValue;
import org.example.Game.SnakeGame;

import javax.swing.*;
import java.awt.*;

public class Renderer extends JPanel {
    private final FieldValue[][] snakeGame;

    public Renderer(FieldValue[][] snakeGame) {
        this.snakeGame = snakeGame;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        double dimX = 20; // (double) getWidth() / (double) snakeGame.getBoard().length;
        double dimY = 20; // (double) getHeight() / (double) snakeGame.getBoard()[0].length;

        double dim = Math.min(dimX, dimY);

        Font f = new Font("Calibri", Font.PLAIN, /*(int)dim*/20);
        g2.setFont(f);
        g2.setColor(Color.LIGHT_GRAY);

        for (int i = 0; i < snakeGame.length; i++) {
            for (int j = 0; j < snakeGame[i].length; j++) {
                var field = snakeGame[i][j];
                g2.drawString(field.getValue(), (int)(i*dim) + (int)(dim*0.3), (int)(j*dim) + (int)(dim*1.2));
            }
        }
    }
}
