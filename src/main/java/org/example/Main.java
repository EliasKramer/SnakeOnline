package org.example;

import org.example.Game.SnakeGame;
import org.example.Server.Server;

import java.awt.*;
import java.io.Console;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server s = new Server();
        s.start();
    }
}