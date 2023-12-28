package com.svalero.editor.task;
import com.svalero.editor.util.Utils;
import javafx.concurrent.Task;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.svalero.editor.util.Constants.SLEEP_TIME;

public class GrayscaleTask extends Task<BufferedImage> {
    private final BufferedImage image;

    public GrayscaleTask(BufferedImage image) {
        this.image = Utils.copyBufferedImage(image);
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
            Thread.sleep(SLEEP_TIME);
            updateProgress(y, image.getHeight());
            updateMessage("Converting to Grayscale... ");
        }

        return image;
    }
}
