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
            if (selectedFilter.equals("Grayscale")) {
                GrayscaleTask grayscaleTask = new GrayscaleTask(imageVersions.get(imageVersionsPosition));
                pbProgress.progressProperty().bind(grayscaleTask.progressProperty());
                grayscaleTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
                grayscaleTask.setOnSucceeded(workerStateEvent -> {
                    imageVersions.add(grayscaleTask.getValue());
                    imageVersionsPosition++;
                    Image imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition), null);
                    Platform.runLater(() -> this.ivEditedImage.setImage(imageToShow));
                });
                Thread grayscaleThread = new Thread(grayscaleTask);
                grayscaleThread.start();
                grayscaleThread.join();

            } else if (selectedFilter.equals("Invert Colors")) {
                InvertColorsTask invertColorsTask = new InvertColorsTask(imageVersions.get(imageVersionsPosition));
                pbProgress.progressProperty().bind(invertColorsTask.progressProperty());
                invertColorsTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
                invertColorsTask.setOnSucceeded(workerStateEvent -> {
                    imageVersions.add(invertColorsTask.getValue());
                    imageVersionsPosition++;
                    Image imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition), null);
                    Platform.runLater(() -> this.ivEditedImage.setImage(imageToShow));
                });
                Thread invertColorsThread = new Thread(invertColorsTask);
                invertColorsThread.start();
                invertColorsThread.join();

            } else if (selectedFilter.equals("Increase Brightness")) {
                IncreaseBrightnessTask increaseBrightnessTask = new IncreaseBrightnessTask(imageVersions.get(imageVersionsPosition));
                pbProgress.progressProperty().bind(increaseBrightnessTask.progressProperty());
                increaseBrightnessTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
                increaseBrightnessTask.setOnSucceeded(workerStateEvent -> {
                    imageVersions.add(increaseBrightnessTask.getValue());
                    imageVersionsPosition++;
                    Image imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition), null);
                    Platform.runLater(() -> this.ivEditedImage.setImage(imageToShow));
                });
                Thread increaseBrightnessThread = new Thread(increaseBrightnessTask);
                increaseBrightnessThread.start();
                increaseBrightnessThread.join();

            } else if (selectedFilter.equals("Blur")) {
                BlurTask blurTask = new BlurTask(imageVersions.get(imageVersionsPosition));
                pbProgress.progressProperty().bind(blurTask.progressProperty());
                blurTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
                blurTask.setOnSucceeded(workerStateEvent -> {
                    imageVersions.add(blurTask.getValue());
                    imageVersionsPosition++;
                    Image imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition), null);
                    Platform.runLater(() -> this.ivEditedImage.setImage(imageToShow));
                });
                Thread blurThread = new Thread(blurTask);
                blurThread.start();
                blurThread.join();
            }
            spEditedContainer.setVisible(true);
        }

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
            e.printStackTrace(); // Maneja la excepción aquí
        }

        updateMessage("Filters applied successfully");

        return imageVersions;
    }
}
