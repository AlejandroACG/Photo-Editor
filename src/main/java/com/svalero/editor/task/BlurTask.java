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
        for (int y = 2; y < image.getHeight() - 2; y++) {
            for (int x = 2; x < image.getWidth() - 2; x++) {
                int totalRed = 0, totalGreen = 0, totalBlue = 0;
                int count = 0;

                for (int dy = -2; dy <= 2; dy++) {
                    for (int dx = -2; dx <= 2; dx++) {
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
