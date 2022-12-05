package org.example.Game.Input;
import org.example.Game.Direction;
import org.example.client.ClientGame;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputListener implements NativeKeyListener {
    private final ClientGame _client;
    public InputListener(ClientGame client)
    {
        _client = client;
        registerHook();
    }
    private void registerHook()
    {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException("There was an exception while initializing the input listener. \n"+e);
        }

        GlobalScreen.addNativeKeyListener(this);
    }
    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        if (keyText.equals("W") || keyText.equals("Up")) {
            System.out.println("UP");
            _client.sendInput(Direction.UP);
        } else if (keyText.equals("A") || keyText.equals("Left")) {
            System.out.println("LEFT");
            _client.sendInput(Direction.LEFT);
        } else if (keyText.equals("S") || keyText.equals("Down")) {
            System.out.println("DOWN");
            _client.sendInput(Direction.DOWN);
        } else if (keyText.equals("D") || keyText.equals("Right")) {
            System.out.println("RIGHT");
            _client.sendInput(Direction.RIGHT);
        } else if (keyText.equals("R")) {
            System.out.println("RESTART");
            _client.sendRestart();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}