package com.svalero.editor.tasks;
import com.svalero.editor.filters.BlurFilter;
import com.svalero.editor.filters.GrayscaleFilter;
import com.svalero.editor.filters.IncreaseBrightnessFilter;
import com.svalero.editor.filters.InvertColorsFilter;
import com.svalero.editor.utils.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import static com.svalero.editor.utils.Constants.BLUR_INTENSITY;
import static com.svalero.editor.utils.Constants.SLEEP_TIME;

public class EditTask extends Task<ArrayList<BufferedImage>> {
    private final ArrayList<String> selectedFilters;
    private ArrayList<BufferedImage> imageVersions = new ArrayList<>();
    private Integer imageVersionsPosition;
    private final File historyFile = new File("History.txt");
    private final StackPane spEditedContainer;
    private final ImageView ivEditedImage;
    private final String filter;
    private final String sourceName;

    public EditTask(ArrayList<BufferedImage> imageVersions, ArrayList<String> selectedFilters,
                    StackPane spEditedContainer, ImageView ivEditedImage, String sourceName)
            throws IOException, InterruptedException {
        this.imageVersions = imageVersions;
        this.selectedFilters = selectedFilters;
        this.imageVersionsPosition = 0;
        this.spEditedContainer = spEditedContainer;
        this.ivEditedImage = ivEditedImage;
        this.sourceName = sourceName;
        this.filter = null;
    }

    public EditTask(ArrayList<BufferedImage> imageVersions, String filter) {
        this.imageVersions = imageVersions;
        this.filter = filter;
        this.selectedFilters = null;
        this.spEditedContainer = null;
        this.ivEditedImage = null;
        this.sourceName = null;
    }

    @Override
    protected ArrayList<BufferedImage> call() throws InterruptedException {
        if (!historyFile.exists()) {
            try {
                historyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        updateMessage("Applying Filters...");
        // TODO Try to code % again.

        if (selectedFilters != null) {
            String selectedFiltersString = "";
            for (String selectedFilter : selectedFilters) {
                BufferedImage newImage = Utils.copyBufferedImage(imageVersions.get(imageVersionsPosition));
                imageVersions.add(applyFilters(newImage, selectedFilter));
                selectedFiltersString = selectedFiltersString + " -> " + selectedFilter;

                imageVersionsPosition++;
            }
            updateMessage("Filters applied successfully");

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            try (FileWriter writer = new FileWriter("History.txt", true)) {
                writer.write(formattedDateTime + ": " + sourceName + selectedFiltersString + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            BufferedImage newImage = Utils.copyBufferedImage(imageVersions.get(imageVersions.size()-1));
            imageVersions.add(applyFilters(newImage, filter));
            updateMessage("Filter applied successfully");

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            try (FileWriter writer = new FileWriter("History.txt", true)) {
                writer.write(formattedDateTime + ": " + sourceName + " -> " + filter + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageVersions;
    }

    private BufferedImage applyFilters(BufferedImage image, String filter) throws InterruptedException {
        for (int y = 0; y < image.getHeight(); y++) {
            Thread.sleep(SLEEP_TIME);
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                if (filter.equals("Grayscale")) {
                    updateMessage("Applying Grayscale...");
                    image.setRGB(x, y, GrayscaleFilter.apply(color).getRGB());
                } else if (filter.equals("Invert Colors")) {
                    updateMessage("Inverting Colors...");
                    image.setRGB(x, y, InvertColorsFilter.apply(color).getRGB());
                } else if (filter.equals("Increase Brightness")) {
                    updateMessage("Increasing Brightness...");
                    image.setRGB(x, y, IncreaseBrightnessFilter.apply(color).getRGB());
                } else if (filter.equals("Blur") && y - BLUR_INTENSITY >= 0 && y + BLUR_INTENSITY < image.getHeight()
                        && x - BLUR_INTENSITY >= 0 && x + BLUR_INTENSITY < image.getWidth()) {
                    updateMessage("Applying Blur...");
                    int row = 0;
                    int column;
                    Color[][] surroundingColors = new Color[BLUR_INTENSITY * 2 + 1][BLUR_INTENSITY * 2 + 1];
                    for (int dy = -BLUR_INTENSITY; dy <= BLUR_INTENSITY; dy++) {
                        column = 0;
                        for (int dx = -BLUR_INTENSITY; dx <= BLUR_INTENSITY; dx++) {
                            surroundingColors[row][column] = new Color(image.getRGB(x + dx, y + dy));
                            column++;
                        }
                        row++;
                    }
                    image.setRGB(x, y, BlurFilter.apply(surroundingColors).getRGB());
                }
            }
            updateProgress(y, image.getHeight());
        }
        return image;
    }
}
