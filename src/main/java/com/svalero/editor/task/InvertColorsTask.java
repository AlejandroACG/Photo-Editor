package com.svalero.editor.task;
import javafx.concurrent.Task;
import java.awt.*;
import java.awt.image.BufferedImage;

public class InvertColorsTask extends Task<BufferedImage> {
    private final BufferedImage image;

    public InvertColorsTask(BufferedImage image) {
        this.image = image;
    }

    @Override
    protected BufferedImage call() throws Exception {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int invertedRed = 255 - red;
                int invertedGreen = 255 - green;
                int invertedBlue = 255 - blue;

                Color newColor = new Color(invertedRed, invertedGreen, invertedBlue);
                image.setRGB(x, y, newColor.getRGB());
            }
        }
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100);
            updateProgress(i, 100);
            updateMessage("Inverting Colors... " + i + "%");
        }

        return image;
    }
}
