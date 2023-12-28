package com.svalero.editor.task;
import javafx.concurrent.Task;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.svalero.editor.util.Constants.SLEEP_TIME;

public class BlurTask extends Task<BufferedImage> {
    private final BufferedImage image;

    public BlurTask(BufferedImage image) { this.image = image; }

    @Override
    protected BufferedImage call() throws Exception {
        int blurIntensity = 2;

        for (int y = blurIntensity; y < image.getHeight() - blurIntensity; y++) {
            for (int x = blurIntensity; x < image.getWidth() - blurIntensity; x++) {
                int totalRed = 0, totalGreen = 0, totalBlue = 0;
                int count = 0;

                for (int dy = -blurIntensity; dy <= blurIntensity; dy++) {
                    for (int dx = -blurIntensity; dx <= blurIntensity; dx++) {
                        Color color = new Color(image.getRGB(x + dx, y + dy));
                        totalRed += color.getRed();
                        totalGreen += color.getGreen();
                        totalBlue += color.getBlue();
                        count++;
                    }
                }

                int avgRed = totalRed / count;
                int avgGreen = totalGreen / count;
                int avgBlue = totalBlue / count;

                Color newColor = new Color(avgRed, avgGreen, avgBlue);
                image.setRGB(x, y, newColor.getRGB());
            }
            Thread.sleep(SLEEP_TIME);
            updateProgress(y, image.getHeight());
            updateMessage("Blurring... ");
        }
        return image;
    }
}
