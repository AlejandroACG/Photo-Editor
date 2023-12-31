package com.svalero.editor;
import com.svalero.editor.tasks.*;
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
    private final File destinationDirectory;
    private final ArrayList<String> selectedFilters;
    private final String sourceName;
    private IntegerProperty imageVersionsPosition = new SimpleIntegerProperty(0);
    Alert alertOverwrite = new Alert(Alert.AlertType.CONFIRMATION);
    private final Tab myTab;
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

    // TODO Update History with new editions and specify if some of them are deleted.
    public EditController(File sourceFile, File destinationDirectory, ArrayList<String> selectedFilters, Tab myTab) throws IOException {
        this.imageVersions.add(ImageIO.read(sourceFile));
        this.sourceName = sourceFile.getName();
        this.destinationDirectory = destinationDirectory;
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

        this.ivInitialImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(0), null));

        // TODO Update Message with "something went wrong" if something goes wrong?
        // TODO Delete all unneeded "this."
        try {
            EditTask editTask = new EditTask(imageVersions, selectedFilters,
                    spEditedContainer, ivEditedImage, sourceName);
            editTask.setOnSucceeded(workerStateEvent -> {
                imageVersions = editTask.getValue();
                imageVersionsPosition.set(imageVersions.size() - 1);
                ivEditedImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null));
                spEditedContainer.setVisible(true);
                enableButtons();
                if (!myTab.isSelected()) {
                    myTab.getStyleClass().add("tab-edited");
                }
            });
            pbProgress.progressProperty().bind(editTask.progressProperty());
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

        try (FileWriter writer = new FileWriter("History.txt", true)) {
            writer.write(formattedDateTime + ": " + sourceName + " -> " + filter +"\n");
        } catch (IOException e) {
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
            writer.write(formattedDateTime + ": " + sourceName + " -> " + saveFile.getName() + "\n");
        } catch (IOException e) {
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

            try (FileWriter writer = new FileWriter("History.txt", true)) {
                writer.write(formattedDateTime + ": " + sourceName +
                        " -> Undone edits: " + (imageVersions.size() - imageVersionsPosition.get() - 1) + "\n");
            } catch (IOException e) {
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
        editTask.setOnSucceeded(workerStateEvent -> {
            imageVersions = editTask.getValue();
            imageVersionsPosition.set(imageVersions.size() - 1);

            ivEditedImage.setImage(SwingFXUtils.toFXImage(imageVersions.get(imageVersionsPosition.get()), null));
            if (!myTab.isSelected()) {
                myTab.getStyleClass().add("tab-edited");
            }

            enableButtons();
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
