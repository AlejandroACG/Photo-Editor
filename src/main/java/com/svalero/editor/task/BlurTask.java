package com.svalero.editor.task;
import com.svalero.editor.util.Utils;
import javafx.concurrent.Task;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.svalero.editor.util.Constants.BLUR_INTENSITY;
import static com.svalero.editor.util.Constants.SLEEP_TIME;

public class BlurTask extends Task<BufferedImage> {
    private final BufferedImage image;

    public BlurTask(BufferedImage image) {
        this.image = Utils.copyBufferedImage(image);
    }

    @Override
    protected BufferedImage call() throws Exception {

        for (int y = BLUR_INTENSITY; y < image.getHeight() - BLUR_INTENSITY; y++) {
            for (int x = BLUR_INTENSITY; x < image.getWidth() - BLUR_INTENSITY; x++) {
                int totalRed = 0, totalGreen = 0, totalBlue = 0;
                int count = 0;

                for (int dy = -BLUR_INTENSITY; dy <= BLUR_INTENSITY; dy++) {
                    for (int dx = -BLUR_INTENSITY; dx <= BLUR_INTENSITY; dx++) {
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
