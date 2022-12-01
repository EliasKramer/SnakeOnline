package org.example.Game;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorManager {
    private static ColorManager _instance;
    private static final Map<Color, String> _colors = new HashMap<>();

    public static ColorManager getInstance() {
        if (_instance == null) {
            _instance = new ColorManager();
        }
        return _instance;
    }

    private ColorManager() {
        _colors.put(Color.RED, "\u001B[31m");
        _colors.put(Color.GREEN, "\u001B[32m");
        _colors.put(Color.YELLOW, "\u001B[33m");
        _colors.put(Color.BLUE, "\u001B[34m");
        _colors.put(Color.MAGENTA, "\u001B[35m");
        _colors.put(Color.CYAN, "\u001B[36m");
    }

    public String getResetColor() {
        return "\u001B[0m";
    }

    public String getColor(Color color) {
        var result = _colors.get(color);
        return result == null ? "" : result;
    }

    public Color getEnvironmentColor() {
        return Color.WHITE;
    }
}