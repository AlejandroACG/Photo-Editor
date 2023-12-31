package com.svalero.editor;
import com.svalero.editor.tasks.*;
import com.svalero.editor.utils.Alerts;
import com.svalero.editor.utils.Utils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

public class EditController implements Initializable {
    private ArrayList<BufferedImage> imageVersions = new ArrayList<>();
    private ArrayList<String> selectedFilters;
    private final String sourceName;
    private final IntegerProperty imageVersionsPosition = new SimpleIntegerProperty(0);
    private final Tab myTab;
    private final File historyFile = new File("History.txt");
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
    private ChoiceBox<String> cbTab1;
    @FXML
    private ChoiceBox<String> cbTab2;
    @FXML
    private ChoiceBox<String> cbTab3;
    @FXML
    private ChoiceBox<String> cbTab4;
    @FXML
    private Button btnEditTab;
    @FXML
    private Button btnSave;

    public EditController(File sourceFile, ArrayList<String> selectedFilters, Tab myTab) throws IOException {
        this.imageVersions.add(ImageIO.read(sourceFile));
        this.sourceName = sourceFile.getName();
        this.selectedFilters = selectedFilters;
        this.myTab = myTab;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        disableButtons();

        Utils.choiceBoxSetUp(cbTab1, cbTab2, cbTab3, cbTab4);
        spEditedContainer.setVisible(false);

        imageVersionsPosition.addListener((observable, oldValue, newValue) -> {
            btnUndo.setDisable(imageVersionsPosition.get() == 0);
            btnRedo.setDisable(imageVersionsPosition.get() == imageVersions.size() - 1);
        });
        myTab.selectedProperty().addListener((observable, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                myTab.getStyleClass().remove("tab-edited");
            }
        });
        pbProgress.progressProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.doubleValue() >= 1) {
                pbProgress.getStyleClass().remove("progress-bar");
                pbProgress.getStyleClass().add("progress-bar-complete");
            } else {
                pbProgress.getStyleClass().remove("progress-bar-complete");
                pbProgress.getStyleClass().add("progress-bar");
            }
        });

        ivInitialImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(0), null));

        launchEditTask();
    }

    @FXML
    private void editAgain(ActionEvent event) {
        if (!(imageVersionsPosition.get() == imageVersions.size() - 1)) {
            if (!confirmOverwrite()) {
                return;
            }
        }
        disableButtons();

        selectedFilters = new ArrayList<>();
        Utils.selectedFiltersFill(selectedFilters, cbTab1, cbTab2, cbTab3, cbTab4);

        launchEditTask();
    }

    private void launchEditTask() {
        try {
            EditTask editTask = new EditTask(imageVersions, selectedFilters,
                    spEditedContainer, ivEditedImage, sourceName);
            editTask.setOnFailed(workerStateEvent -> {
                Alerts.filtersFailure(sourceName);
                lblProgressStatus.setText("Filters couldn't be applied");
                pbProgress.setProgress(0);
            });
            editTask.setOnSucceeded(workerStateEvent -> {
                imageVersions = editTask.getValue();
                imageVersionsPosition.set(imageVersions.size() - 1);

                if (!myTab.isSelected()) {
                    myTab.getStyleClass().add("tab-edited");
                }
                enableButtons();
                Alerts.filtersSuccess(sourceName);
            });
            pbProgress.progressProperty().bind(editTask.progressProperty());
            editTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
            new Thread(editTask).start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Alerts.filtersFailure(sourceName);
        }
    }

    @FXML
    private void undoEdit(ActionEvent event) {
        imageVersionsPosition.set(imageVersionsPosition.get() - 1);
        ivEditedImage.setImage(null);
        ivEditedImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null));
    }

    @FXML
    private void redoEdit(ActionEvent event) {
        imageVersionsPosition.set(imageVersionsPosition.get() + 1);
        ivEditedImage.setImage(null);
        ivEditedImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null));
    }

    @FXML
    private void saveImage(ActionEvent event) {
        String extension = sourceName.substring(sourceName.lastIndexOf('.') + 1);
        String uniqueFileName = UUID.randomUUID() + "." + extension;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        Utils.resultsDirectoryExists();
        fileChooser.setInitialDirectory(new File("Results"));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Source image extension", extension);
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialFileName(uniqueFileName);
        Stage stage = (Stage) this.btnSave.getScene().getWindow();
        File saveFile = fileChooser.showSaveDialog(stage);

        String fileName = saveFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String currentExtension = (dotIndex > 0) ? fileName.substring(dotIndex + 1) : "";
        String baseName = (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;
        if (!currentExtension.equals(extension) || dotIndex < 0) {
            saveFile = Paths.get(saveFile.getParent(), baseName + "." + extension).toFile();
        }

        try {
            ImageIO.write(imageVersions.get(imageVersionsPosition.get()), extension, saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.saveFailure(sourceName);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Utils.historyFileExists(historyFile);
        try (FileWriter writer = new FileWriter(historyFile, true)) {
            writer.write(formattedDateTime + ": " + sourceName + " -> " + fileName + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.errorWritingHistory();
        }
    }

    private boolean confirmOverwrite() {
        Alert alertOverwrite = new Alert(Alert.AlertType.CONFIRMATION);
        alertOverwrite.setTitle("Warning");
        alertOverwrite.setHeaderText("This isn't the latest iteration of the image.\n" +
                "You'll overwrite the latests edits if you proceed.");
        alertOverwrite.setContentText("Do you wish to proceed?");

        Optional<ButtonType> result = alertOverwrite.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            Utils.historyFileExists(historyFile);
            try (FileWriter writer = new FileWriter(historyFile, true)) {
                writer.write(formattedDateTime + ": " + sourceName +
                        " -> Undone edits: " + (imageVersions.size() - imageVersionsPosition.get() - 1) + "\n");
            } catch (IOException e) {
                Alerts.errorWritingHistory();
                e.printStackTrace();
            }

            imageVersions.subList(imageVersionsPosition.get() + 1, imageVersions.size()).clear();
            return true;
        }
        return false;
    }

    private void disableButtons() {
        btnUndo.setDisable(true);
        btnRedo.setDisable(true);
        btnEditTab.setDisable(true);
        btnSave.setDisable(true);
    }

    private void enableButtons() {
        btnEditTab.setDisable(false);
        btnSave.setDisable(false);
    }
}
