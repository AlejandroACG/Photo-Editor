package com.svalero.editor;
import com.svalero.editor.tasks.*;
import com.svalero.editor.utils.Alerts;
import com.svalero.editor.utils.Utils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private ArrayList<BufferedImage> imageVersions = new ArrayList<>();
    private final ArrayList<String> selectedFilters;
    private final String sourceName;
    private final IntegerProperty imageVersionsPosition = new SimpleIntegerProperty(0);
    Alert alertOverwrite = new Alert(Alert.AlertType.CONFIRMATION);
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
    private ChoiceBox<String> cbTab;
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
        ObservableList<String> choiceBoxOptions = FXCollections.observableArrayList("Grayscale", "Invert Colors", "Increase Brightness", "Blur");
        cbTab.setItems(choiceBoxOptions);
        cbTab.setValue(cbTab.getItems().get(0));
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

        try {
            EditTask editTask = new EditTask(imageVersions, selectedFilters,
                    spEditedContainer, ivEditedImage, sourceName);
            editTask.setOnFailed(workerStateEvent -> {
                Alerts.filtersFailure(sourceName);
                myTab.getTabPane().getTabs().remove(myTab);
            });
            editTask.setOnSucceeded(workerStateEvent -> {
                imageVersions = editTask.getValue();
                imageVersionsPosition.set(imageVersions.size() - 1);
                ivEditedImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null));
                spEditedContainer.setVisible(true);

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
            Alerts.filtersFailure(sourceName);
            e.printStackTrace();
            myTab.getTabPane().getTabs().remove(myTab);
        }
    }

    @FXML
    private void launchEditTab(ActionEvent event) {
        if (!(imageVersionsPosition.get() == imageVersions.size() - 1)) {
            if (!confirmOverwrite()) {
                return;
            }
        }

        String filter = cbTab.getValue();

        applyFilter(filter);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Utils.historyFileExists(historyFile);
        try (FileWriter writer = new FileWriter(historyFile, true)) {
            writer.write(formattedDateTime + ": " + sourceName + " -> " + filter +"\n");
        } catch (IOException e) {
            Alerts.errorWritingHistory();
            e.printStackTrace();
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

        if (!saveFile.getName().substring(saveFile.getName().lastIndexOf('.') + 1).equals(extension)) {
            Alerts.saveFailure(sourceName);
            return;
        }

        try {
            ImageIO.write(imageVersions.get(imageVersionsPosition.get()), extension, saveFile);
        } catch (IOException e) {
            Alerts.saveFailure(sourceName);
            e.printStackTrace();
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Utils.historyFileExists(historyFile);
        try (FileWriter writer = new FileWriter(historyFile, true)) {
            writer.write(formattedDateTime + ": " + sourceName + " -> " + saveFile.getName() + "\n");
        } catch (IOException e) {
            Alerts.errorWritingHistory();
            e.printStackTrace();
        }
    }

    private boolean confirmOverwrite() {
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

    private void applyFilter(String filter) {
        disableButtons();

        EditTask editTask = new EditTask(imageVersions, filter);

        pbProgress.progressProperty().bind(editTask.progressProperty());
        editTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
        editTask.setOnFailed(workerStateEvent -> {
            lblProgressStatus.setText("Filter couldn't be applied");
            pbProgress.setProgress(0);
            Alerts.filterFailure(sourceName, filter);
            enableButtons();
        });
        editTask.setOnSucceeded(workerStateEvent -> {
            imageVersions = editTask.getValue();
            imageVersionsPosition.set(imageVersions.size() - 1);

            ivEditedImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null));

            if (!myTab.isSelected()) {
                myTab.getStyleClass().add("tab-edited");
            }

            enableButtons();
            Alerts.filterSuccess(sourceName, filter);
        });
        pbProgress.progressProperty().bind(editTask.progressProperty());
        editTask.messageProperty().addListener((observable, oldValue, newValue) -> lblProgressStatus.setText(newValue));
        new Thread(editTask).start();
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
