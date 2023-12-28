package com.svalero.editor.task;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class EditTask extends Task<ArrayList<BufferedImage>> {
    private final File initialFile;
    private final ArrayList<String> selectedFilters;
    private final ArrayList<BufferedImage> imageVersions = new ArrayList<>();
    private Integer imageVersionsPosition;
    private final ProgressBar pbProgress;
    private final Label lblProgressStatus;
    private final File historyFile = new File("History.txt");
    private final StackPane spEditedContainer;
    private final ImageView ivEditedImage;

    public EditTask(File initialFile, ArrayList<String> selectedFilters, ProgressBar pbProgress,
                    Label lblProgressStatus, StackPane spEditedContainer, ImageView ivEditedImage)
            throws IOException, InterruptedException {
        this.initialFile = initialFile;
        this.selectedFilters = selectedFilters;
        this.imageVersions.add(ImageIO.read(initialFile));
        this.imageVersionsPosition = 0;
        this.pbProgress = pbProgress;
        this.lblProgressStatus = lblProgressStatus;
        this.spEditedContainer = spEditedContainer;
        this.ivEditedImage = ivEditedImage;
    }

    @Override
    protected ArrayList<BufferedImage> call() throws Exception {
        updateMessage("Applying Filters...");

        for (String selectedFilter : selectedFilters) {
            Task<BufferedImage> filterTask;
            if (selectedFilter.equals("Grayscale")) {
                filterTask = new GrayscaleTask(imageVersions.get(imageVersionsPosition));
            } else if (selectedFilter.equals("Invert Colors")) {
                filterTask = new InvertColorsTask(imageVersions.get(imageVersionsPosition));
            } else if (selectedFilter.equals("Increase Brightness")) {
                filterTask = new IncreaseBrightnessTask(imageVersions.get(imageVersionsPosition));
            } else {
                filterTask = new BlurTask(imageVersions.get(imageVersionsPosition));
            }

            Platform.runLater(() -> pbProgress.progressProperty().bind(filterTask.progressProperty()));
            filterTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));

            filterTask.setOnSucceeded(workerStateEvent -> {
                imageVersions.add(filterTask.getValue());
                imageVersionsPosition++;
                Image imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition), null);
                Platform.runLater(() -> this.ivEditedImage.setImage(imageToShow));
            });

            Thread filterThread = new Thread(filterTask);
            filterThread.start();
            filterThread.join();

            spEditedContainer.setVisible(true);
        }
        updateMessage("Filters applied successfully");

        if (!historyFile.exists()) {
            try {
                historyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String selectedFiltersString = "";
        for (String selectedFilter : selectedFilters) {
            selectedFiltersString = selectedFiltersString + " -> " + selectedFilter;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        try (FileWriter writer = new FileWriter("History.txt", true)) {
            writer.write(formattedDateTime + ": " + initialFile.getName() + selectedFiltersString + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageVersions;
    }
}
