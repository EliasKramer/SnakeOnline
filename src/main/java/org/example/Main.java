package org.example;

import org.example.Game.SnakeGame;

public class Main {
    public static void main(String[] args) {
        System.out.println(new SnakeGame(10,10).getBoardString());
    }
}