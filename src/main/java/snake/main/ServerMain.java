package snake.main;

import snake.Server.Server;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server("ONLY SERVER");
        server.start();
    }
}