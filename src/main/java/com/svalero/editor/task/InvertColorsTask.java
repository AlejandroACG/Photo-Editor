package com.svalero.editor.task;
import com.svalero.editor.util.Utils;
import javafx.concurrent.Task;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.svalero.editor.util.Constants.SLEEP_TIME;

public class InvertColorsTask extends Task<BufferedImage> {
    private final BufferedImage image;

    public InvertColorsTask(BufferedImage image) {
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

                int invertedRed = 255 - red;
                int invertedGreen = 255 - green;
                int invertedBlue = 255 - blue;

                Color newColor = new Color(invertedRed, invertedGreen, invertedBlue);
                image.setRGB(x, y, newColor.getRGB());
            }
            Thread.sleep(SLEEP_TIME);
            updateProgress(y, image.getHeight());
            updateMessage("Inverting Colors... ");
        }

        return image;
    }
}
