package org.example.Game.GUI;

import org.example.Game.FieldValue;
import org.example.Game.Position;
import org.example.Game.Snake;
import org.example.Game.SnakeGame;

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
        double dimX = 20; // (double) getWidth() / (double) snakeGame.getBoard().length;
        double dimY = 20; // (double) getHeight() / (double) snakeGame.getBoard()[0].length;

        double dim = Math.min(dimX, dimY);

        g2.setFont(new Font("Calibri", Font.PLAIN, /*(int)dim*/20));
        g2.setColor(Color.LIGHT_GRAY);

        for (int y = 0; y < snakeGame.getHeight(); y++) {
            for (int x = 0; x < snakeGame.getWidth(); x++) {
                g2.setColor(snakeGame.getColorAtPosition(new Position(x, y)));
                g2.drawString(snakeGame.getValueAtPosition(new Position(x,y)).getValue(),
                        (int)(x*dim) + (int)(dim*0.3), (int)(y*dim) + (int)(dim*1.2));
            }
        }
    }
}
