package org.example.Game.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("keyTyped "+ e.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("pressed "+ e.getKeyChar());

    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("released "+ e.getKeyChar());
    }
}