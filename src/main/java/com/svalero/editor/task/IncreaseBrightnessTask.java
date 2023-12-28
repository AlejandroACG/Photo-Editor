package com.svalero.editor.task;
import com.svalero.editor.util.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.awt.*;
import java.awt.image.BufferedImage;
import static com.svalero.editor.util.Constants.SLEEP_TIME;

public class IncreaseBrightnessTask extends Task<BufferedImage> {
    private final BufferedImage image;

    public IncreaseBrightnessTask(BufferedImage image) {
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

                int increment = 30;

                int brighterRed = Math.min(255, red + increment);
                int brighterGreen = Math.min(255, green + increment);
                int brighterBlue = Math.min(255, blue + increment);

                Color newColor = new Color(brighterRed, brighterGreen, brighterBlue);
                image.setRGB(x, y, newColor.getRGB());
            }
            Thread.sleep(SLEEP_TIME);
            int finalY = y;
            Platform.runLater(() -> {
                updateProgress(finalY, image.getHeight());
                updateMessage("Increasing Brightness... ");
            });
        }

        return image;
    }
}
