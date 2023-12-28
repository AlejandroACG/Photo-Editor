package com.svalero.editor;
import com.svalero.editor.task.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

public class EditController implements Initializable {
    private final File initialFile;
    private final File destinationDirectory;
    private final ArrayList<String> selectedFilters;
    // TODO This has to receive the data from EditTask.
    private ArrayList<BufferedImage> imageVersions;
    private IntegerProperty imageVersionsPosition = new SimpleIntegerProperty();
    Alert alertOverwrite = new Alert(Alert.AlertType.CONFIRMATION);
    @FXML
    private ImageView ivInitialImage;
    @FXML
    private StackPane spEditedContainer;
    @FXML
    private ImageView ivEditedImage;
    @FXML
    private Button btnUndo;
    @FXML
    private Button btnRedo;
    @FXML
    private ProgressBar pbProgress;
    @FXML
    private Label lblProgressStatus;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnGrayscale;
    @FXML
    private Button btnInvertColors;
    @FXML
    private Button btnIncreaseBrightness;
    @FXML
    private Button btnBlur;

    // TODO Update History with new editions and specify if some of them are deleted.
    public EditController(File initialFile, File destinationDirectory, ArrayList<String> selectedFilters) {
        this.initialFile = initialFile;
        this.destinationDirectory = destinationDirectory;
        this.selectedFilters = selectedFilters;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUndo.setDisable(true);
        btnRedo.setDisable(true);
        spEditedContainer.setVisible(false);

        imageVersionsPosition.addListener((observable, oldValue, newValue) -> {
            btnUndo.setDisable(imageVersionsPosition.get() == 0);
            btnRedo.setDisable(imageVersionsPosition.get() == imageVersions.size() - 1);
        });

        try {
            this.ivInitialImage.setImage(new Image(new FileInputStream(initialFile)));
        } catch (FileNotFoundException e) {
            // TODO Create Alert.
            throw new RuntimeException(e);
        }

        alertOverwrite.setTitle("Warning");
        alertOverwrite.setHeaderText("This isn't the latest iteration of the picture.\n" +
                "You'll overwrite the latests edits if you proceed.");
        alertOverwrite.setContentText("Do you wish to proceed?");

        try {
            EditTask editTask = new EditTask(this.initialFile, this.selectedFilters, this.pbProgress,
                    this.lblProgressStatus, this.spEditedContainer, this.ivEditedImage);
            editTask.setOnSucceeded(workerStateEvent -> {
                // TODO Maybe change tab color when completed yet unfocused.
                this.imageVersions = editTask.getValue();
                this.imageVersionsPosition.set(this.imageVersions.size() - 1);
            });
            editTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
            new Thread(editTask).start();

        } catch (IOException e) {
            // TODO Create Alert.
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void launchGrayscale(ActionEvent event) {
        if (!(imageVersionsPosition.get() == imageVersions.size() - 1)) {
            Optional<ButtonType> result = alertOverwrite.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                imageVersions.subList(imageVersionsPosition.get() + 1, imageVersions.size()).clear();
            } else {
                return;
            }
        }

        disableButtons();

        GrayscaleTask grayscaleTask = new GrayscaleTask(imageVersions.get(imageVersionsPosition.get()));
        pbProgress.progressProperty().bind(grayscaleTask.progressProperty());
        grayscaleTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
        grayscaleTask.setOnSucceeded(workerStateEvent -> {
            imageVersions.add(grayscaleTask.getValue());
            imageVersionsPosition.set(imageVersionsPosition.get() + 1);

            Image imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null);
            this.ivEditedImage.setImage(imageToShow);
            lblProgressStatus.setText("Grayscale applied successfully");

            enableButtons();
        });
        Thread grayscaleThread = new Thread(grayscaleTask);
        grayscaleThread.start();
    }

    @FXML
    private void launchInvertColors(ActionEvent event) {
        if (!(imageVersionsPosition.get() == imageVersions.size() - 1)) {
            Optional<ButtonType> result = alertOverwrite.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                imageVersions.subList(imageVersionsPosition.get() + 1, imageVersions.size()).clear();
            } else {
                return;
            }
        }

        disableButtons();

        InvertColorsTask invertColorsTask = new InvertColorsTask(imageVersions.get(imageVersionsPosition.get()));
        pbProgress.progressProperty().bind(invertColorsTask.progressProperty());
        invertColorsTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
        invertColorsTask.setOnSucceeded(workerStateEvent -> {
            imageVersions.add(invertColorsTask.getValue());
            imageVersionsPosition.set(imageVersionsPosition.get() + 1);

            Image imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null);
            this.ivEditedImage.setImage(imageToShow);
            lblProgressStatus.setText("Colors Inverted successfully");

            enableButtons();
        });
        Thread invertColorsThread = new Thread(invertColorsTask);
        invertColorsThread.start();
    }

    @FXML
    private void launchIncreaseBrightness(ActionEvent event) {
        if (!(imageVersionsPosition.get() == imageVersions.size() - 1)) {
            Optional<ButtonType> result = alertOverwrite.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                imageVersions.subList(imageVersionsPosition.get() + 1, imageVersions.size()).clear();
            } else {
                return;
            }
        }

        disableButtons();

        IncreaseBrightnessTask increaseBrightnessTask = new IncreaseBrightnessTask(imageVersions.get(imageVersionsPosition.get()));
        pbProgress.progressProperty().bind(increaseBrightnessTask.progressProperty());
        increaseBrightnessTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
        increaseBrightnessTask.setOnSucceeded(workerStateEvent -> {
            imageVersions.add(increaseBrightnessTask.getValue());
            imageVersionsPosition.set(imageVersionsPosition.get() + 1);

            Image imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null);
            this.ivEditedImage.setImage(imageToShow);
            lblProgressStatus.setText("Brightness increased successfully");

            enableButtons();
        });
        Thread increaseBrightnessThread = new Thread(increaseBrightnessTask);
        increaseBrightnessThread.start();
    }

    @FXML
    private void launchBlur(ActionEvent event) {
        if (!(imageVersionsPosition.get() == imageVersions.size() - 1)) {
            Optional<ButtonType> result = alertOverwrite.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                imageVersions.subList(imageVersionsPosition.get() + 1, imageVersions.size()).clear();
            } else {
                return;
            }
        }

        disableButtons();

        BlurTask blurTask = new BlurTask(imageVersions.get(imageVersionsPosition.get()));
        pbProgress.progressProperty().bind(blurTask.progressProperty());
        blurTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
        blurTask.setOnSucceeded(workerStateEvent -> {
            imageVersions.add(blurTask.getValue());
            imageVersionsPosition.set(imageVersionsPosition.get() + 1);

            Image imageToShow = SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null);
            this.ivEditedImage.setImage(imageToShow);
            lblProgressStatus.setText("Blur applied successfully");

            enableButtons();
        });
        Thread blurThread = new Thread(blurTask);
        blurThread.start();
    }

    @FXML
    private void undoEdit(ActionEvent event) {
        this.imageVersionsPosition.set(this.imageVersionsPosition.get() - 1);
        this.ivEditedImage.setImage(null);
        this.ivEditedImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null));
    }

    @FXML
    private void redoEdit(ActionEvent event) {
        this.imageVersionsPosition.set(this.imageVersionsPosition.get() + 1);
        this.ivEditedImage.setImage(null);
        this.ivEditedImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null));
    }

    @FXML
    private void saveImage(ActionEvent event) {
        String extension = initialFile.getName().substring(initialFile.getName().lastIndexOf('.') + 1);
        File saveFile;
        do {
            String uniqueFileName = UUID.randomUUID() + "." + extension;
            saveFile = new File(destinationDirectory, uniqueFileName);
        } while (saveFile.exists());
        try {
            ImageIO.write(imageVersions.get(imageVersionsPosition.get()), extension, saveFile);
        } catch (IOException e) {
            // TODO Alert.
            throw new RuntimeException(e);
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        try (FileWriter writer = new FileWriter("History.txt", true)) {
            writer.write(formattedDateTime + ": " + initialFile.getName() + " -> " + saveFile.getName() + "\n");
        } catch (IOException e) {
            e.printStackTrace(); // Maneja la excepción aquí
        }
    }

    private void disableButtons() {
        btnUndo.setDisable(true);
        btnRedo.setDisable(true);
        btnGrayscale.setDisable(true);
        btnInvertColors.setDisable(true);
        btnIncreaseBrightness.setDisable(true);
        btnBlur.setDisable(true);
        btnSave.setDisable(true);
    }

    private void enableButtons() {
        btnGrayscale.setDisable(false);
        btnInvertColors.setDisable(false);
        btnIncreaseBrightness.setDisable(false);
        btnBlur.setDisable(false);
        btnSave.setDisable(false);
    }
}
