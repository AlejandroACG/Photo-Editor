package com.svalero.editor.task;
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
import java.io.FileInputStream;
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
    private final ImageView ivInitialImage;
    private final ImageView ivEditedImage;
    private final ProgressBar pbProgress;
    private final Label lblProgressStatus;
    private final File historyFile = new File("History.txt");
    private final StackPane spEditedContainer;

    public EditTask(File initialFile, ArrayList<String> selectedFilters, ImageView ivInitialImage,
                    ImageView ivEditedImage, ProgressBar pbProgress, Label lblProgressStatus,
                    StackPane spEditedContainer) throws IOException, InterruptedException {
        this.initialFile = initialFile;
        this.selectedFilters = selectedFilters;
        this.imageVersions.add(ImageIO.read(initialFile));
        this.imageVersionsPosition = 0;
        this.ivInitialImage = ivInitialImage;
        this.ivEditedImage = ivEditedImage;
        this.pbProgress = pbProgress;
        this.lblProgressStatus = lblProgressStatus;
        this.spEditedContainer = spEditedContainer;
    }

    @Override
    protected ArrayList<BufferedImage> call() throws Exception {
        this.ivInitialImage.setImage(new Image(new FileInputStream(initialFile)));
        updateMessage("Applying Filters...");

        Image imageToShow;
        for (String selectedFilter : selectedFilters) {
            if (selectedFilter.equals("Grayscale")) {
                GrayscaleTask grayscaleTask = new GrayscaleTask(imageVersions.get(imageVersionsPosition));
                pbProgress.progressProperty().bind(grayscaleTask.progressProperty());
                grayscaleTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
                grayscaleTask.setOnSucceeded(workerStateEvent -> {
                    imageVersions.add(grayscaleTask.getValue());
                    imageVersionsPosition++;
                });
                Thread grayscaleThread = new Thread(grayscaleTask);
                grayscaleThread.start();
                grayscaleThread.join();
                imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition), null);
                this.ivEditedImage.setImage(imageToShow);

            } else if (selectedFilter.equals("Invert Colors")) {
                InvertColorsTask invertColorsTask = new InvertColorsTask(imageVersions.get(imageVersionsPosition));
                pbProgress.progressProperty().bind(invertColorsTask.progressProperty());
                invertColorsTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
                invertColorsTask.setOnSucceeded(workerStateEvent -> {
                    imageVersions.add(invertColorsTask.getValue());
                    imageVersionsPosition++;
                });
                Thread invertColorsThread = new Thread(invertColorsTask);
                invertColorsThread.start();
                invertColorsThread.join();
                imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition), null);
                this.ivEditedImage.setImage(imageToShow);

            } else if (selectedFilter.equals("Increase Brightness")) {
                IncreaseBrightnessTask increaseBrightnessTask = new IncreaseBrightnessTask(imageVersions.get(imageVersionsPosition));
                pbProgress.progressProperty().bind(increaseBrightnessTask.progressProperty());
                increaseBrightnessTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
                increaseBrightnessTask.setOnSucceeded(workerStateEvent -> {
                    imageVersions.add(increaseBrightnessTask.getValue());
                    imageVersionsPosition++;
                });
                Thread invertColorsThread = new Thread(increaseBrightnessTask);
                invertColorsThread.start();
                invertColorsThread.join();
                imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition), null);
                this.ivEditedImage.setImage(imageToShow);

            } else if (selectedFilter.equals("Blur")) {
                BlurTask blurTask = new BlurTask(imageVersions.get(imageVersionsPosition));
                pbProgress.progressProperty().bind(blurTask.progressProperty());
                blurTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
                blurTask.setOnSucceeded(workerStateEvent -> {
                    imageVersions.add(blurTask.getValue());
                    imageVersionsPosition++;
                });
                Thread invertColorsThread = new Thread(blurTask);
                invertColorsThread.start();
                invertColorsThread.join();
                imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition), null);
                this.ivEditedImage.setImage(imageToShow);
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

        // TODO Change naming convention to "_gray.png", etc.
        //  Take note of: String outputName = inputPath.substring(0, intputPath.length() - 4) + "_gray.png";

        // TODO May change the save function to only save when selected on the TabPane itself.
        updateMessage("Filters applied successfully");

        return imageVersions;
    }
}
