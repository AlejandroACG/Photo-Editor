package com.svalero.editor.filters;
import java.awt.*;
import static com.svalero.editor.utils.Constants.BLUR_INTENSITY;

public class BlurFilter {
    public static Color apply(Color[][] surroundingColors) {
        int totalRed = 0, totalGreen = 0, totalBlue = 0, count = 0;

        for (int y = 0; y < BLUR_INTENSITY * 2 + 1; y++) {
            for (int x = 0; x < BLUR_INTENSITY * 2 + 1; x++) {
                Color color = surroundingColors[y][x];
                totalRed += color.getRed();
                totalGreen += color.getGreen();
                totalBlue += color.getBlue();
                count++;
            }
        }

        int avgRed = totalRed / count;
        int avgGreen = totalGreen / count;
        int avgBlue = totalBlue / count;

        return new Color(avgRed, avgGreen, avgBlue);
    }
}
