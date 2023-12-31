package com.svalero.editor.filters;
import java.awt.*;
import static com.svalero.editor.utils.Constants.BRIGHTNESS_FACTOR;

public class IncreaseBrightnessFilter {
    public static Color apply(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        int brighterRed = Math.min(255, red + BRIGHTNESS_FACTOR);
        int brighterGreen = Math.min(255, green + BRIGHTNESS_FACTOR);
        int brighterBlue = Math.min(255, blue + BRIGHTNESS_FACTOR);

        return new Color(brighterRed, brighterGreen, brighterBlue);
    }
}
