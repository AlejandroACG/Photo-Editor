package com.svalero.editor.filters;
import java.awt.*;

public class InvertColorsFilter {
    public static Color apply(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        int invertedRed = 255 - red;
        int invertedGreen = 255 - green;
        int invertedBlue = 255 - blue;

        return new Color(invertedRed, invertedGreen, invertedBlue);
    }
}
