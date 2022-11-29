package org.example.Server;

import org.example.Game.*;

import java.io.Console;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public class Server extends Thread{
    private SnakeGame _game;
    private boolean _running = true;

    //new game state every 1000ms
    private final float _stateUpdateCycle = 1000f;
    public Server ()
    {
        _game = new SnakeGame(10, 10);
    }

    @Override
    public void run() {
        long lastTimeUpdated = getCurrentTimeInMs();
        long iterationsBetweenUpdate = 0;
        _game.addSnake(new Snake(Colors.BLUE, "blue_snake", Direction.RIGHT, new Position(0, 0)));
        _game.printBoard();
        while(_running)
        {
            final long currTime = getCurrentTimeInMs();
            final long timeSinceLastUpdate = currTime - lastTimeUpdated;

            if(timeSinceLastUpdate >= _stateUpdateCycle)
            {
                //System.out.println("game update. iterationsBetween: "+ iterationsBetweenUpdate);
                _game.processNextUpdate();
                _game.printBoard();
                lastTimeUpdated = currTime;
                iterationsBetweenUpdate = 0;
            }
            else{


                iterationsBetweenUpdate++;
            }
        }
    }

    public long getCurrentTimeInMs()
    {
        return Instant.now().toEpochMilli();
    }
}