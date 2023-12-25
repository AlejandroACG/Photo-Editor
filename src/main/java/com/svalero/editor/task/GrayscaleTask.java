package com.svalero.editor.task;
import javafx.concurrent.Task;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GrayscaleTask extends Task<BufferedImage> {
    private final BufferedImage image;

    public GrayscaleTask(BufferedImage image) {
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

                int gray = (red + green + blue) / 3;

                Color newColor = new Color(gray, gray, gray);
                image.setRGB(x, y, newColor.getRGB());
            }
        }
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100);
            updateProgress(i, 100);
            updateMessage("Converting to Grayscale... " + i + "%");
        }

        return image;
    }
}
