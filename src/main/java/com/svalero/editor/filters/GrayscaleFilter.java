package com.svalero.editor.filters;
import java.awt.*;

public class GrayscaleFilter {
    public static Color apply(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        int gray = (red + green + blue) / 3;

        return new Color(gray, gray, gray);
    }
}
