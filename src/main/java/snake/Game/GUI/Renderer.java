package snake.Game.GUI;

import snake.Game.Position;
import snake.client.ClientGame;

import javax.swing.*;
import java.awt.*;

public class Renderer extends JPanel {
    private final ClientGame clientGame;
    public Renderer(ClientGame clientGame) {
        this.clientGame = clientGame;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        double dimX = 20; // (double) getWidth() / (double) snakeGame.getBoard().length;
        double dimY = 20; // (double) getHeight() / (double) snakeGame.getBoard()[0].length;

        double dim = Math.min(dimX, dimY);

        g2.setFont(new Font("Calibri", Font.PLAIN, /*(int)dim*/20));
        g2.setColor(Color.LIGHT_GRAY);

        for (int y = 0; y < clientGame.getHeight(); y++) {
            for (int x = 0; x < clientGame.getWidth(); x++) {
                g2.setColor(clientGame.getColorAtPosition(new Position(x, y)));
                g2.drawString(clientGame.getValueAtPosition(new Position(x,y)).getValue(),
                        (int)(x*dim) + (int)(dim*0.3), (int)(y*dim) + (int)(dim*1.2));
            }
        }
    }
}
