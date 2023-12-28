package com.svalero.editor;
import com.svalero.editor.task.EditTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;

public class EditController implements Initializable {
    private final File initialFile;
    private final File destinationFolder;
    private final ArrayList<String> selectedFilters;
    // TODO This has to receive the data from EditTask.
    private ArrayList<BufferedImage> imageVersions;
    private Integer imageVersionsPosition;
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

    // TODO It'll need to get the filters in the right order.
    public EditController(File initialFile, File destinationFolder, ArrayList<String> selectedFilters) {
        this.initialFile = initialFile;
        this.destinationFolder = destinationFolder;
        this.selectedFilters = selectedFilters;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spEditedContainer.setVisible(false);
        try {
            EditTask editTask = new EditTask(this.initialFile, this.selectedFilters, this.ivInitialImage,
                    this.ivEditedImage, this.pbProgress, this.lblProgressStatus, this.spEditedContainer);
            editTask.setOnSucceeded(workerStateEvent -> {
                // TODO Maybe change tab color when completed yet unfocused.
                this.imageVersions = editTask.getValue();
                this.imageVersionsPosition = imageVersions.size() - 1;
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

    }

    @FXML
    private void launchInvertColors(ActionEvent event) {

    }

    @FXML
    private void launchIncreaseBrightness(ActionEvent event) {

    }

    @FXML
    private void launchBlur(ActionEvent event) {

    }

    @FXML
    private void undoEdit(ActionEvent event) {

    }

    @FXML
    private void redoEdit(ActionEvent event) {

    }

    @FXML
    private void saveImage(ActionEvent event) {
        String extension = initialFile.getName().substring(initialFile.getName().lastIndexOf('.') + 1);
        File saveFile;
        do {
            String uniqueFileName = UUID.randomUUID() + "." + extension;
            saveFile = new File(destinationFolder, uniqueFileName);
        } while (saveFile.exists());
        try {
            ImageIO.write(imageVersions.get(imageVersionsPosition), extension, saveFile);
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
}
