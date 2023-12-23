package com.svalero.editor;
import static com.svalero.editor.Utils.isImage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

public class AppController implements Initializable {
    @FXML
    private TextField tfDestiny;
    @FXML
    private Button btnHistory;
    @FXML
    private Label lblSavePath;
    @FXML
    private TabPane tpEdits;
    @FXML
    private Button btnEdit;
    @FXML
    private ChoiceBox cb1;
    @FXML
    private ChoiceBox cb2;
    @FXML
    private ChoiceBox cb3;
    @FXML
    private ChoiceBox cb4;
    @FXML
    private TextField tfOrigin;
    @FXML
    private Button btnBrowseOrigin;
    @FXML
    private Button btnBrowseDestiny;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    private void launchEdit(ActionEvent event) {
        // TODO Change all instances of destiny to destination.
        String originPath = tfOrigin.getText();
        String destinyPath = tfDestiny.getText();
        // TODO Code the actual filters.
        if (originPath.isEmpty() || destinyPath.isEmpty()) {
            // TODO Create an alert.
            return;
        }

        File initialFile = new File(originPath);
        File destinationFolder = new File(destinyPath);

        if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
            // TODO Create an alert.
            return;
        }

        if (isImage(initialFile)) {
            // TODO Move code into a processImage method, but only after establishing what variables it'll need to recieve beforehand.
            String extension = initialFile.getName().substring(initialFile.getName().lastIndexOf('.'));

            File destinationFile;
            do {
                String uniqueFileName = UUID.randomUUID() + extension;
                destinationFile = new File(destinationFolder, uniqueFileName);
            } while (destinationFile.exists());

            try {
                // TODO This line won't work after the filters are implemented.
                Files.copy(initialFile.toPath(), destinationFile.toPath());
                // TODO Here goes the new TabPane instance.
                // TODO May change the save function to only save when selected on the TabPane itself.
            } catch (IOException e) {
                // TODO Create Alert.
            }
        } else if (initialFile.isDirectory()) {
            // TODO Code alternative for when a folder is selected instead of a file.
        } else {
            // TODO Create an Alert: the initial file was neither an image nor a folder.
        }
    }

    @FXML
    private void viewHistory(ActionEvent event) {}

    @FXML
    private void browseOrigin(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choose");
        alert.setContentText("What Are You Looking For?");

        ButtonType buttonTypeFile = new ButtonType("File");
        ButtonType buttonTypeDirectory = new ButtonType("Directory");
        ButtonType buttonTypeCancel = new ButtonType("Nothing", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeFile, buttonTypeDirectory, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonTypeFile) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select File");
                // TODO Configure initial directory. May need to create a variable with greater scope.

                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", ".jpeg", "*.gif", "*.bmp");
                fileChooser.getExtensionFilters().add(filter);

                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null && isImage(selectedFile)) {
                    tfOrigin.setText(selectedFile.getAbsolutePath());
                }
            } else if (result.get() == buttonTypeDirectory) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Directory");
                // TODO Configure initial directory. May need to create a variable with greater scope.

                File selectedDirectory = directoryChooser.showDialog(null);
                if (selectedDirectory != null) {
                    tfOrigin.setText(selectedDirectory.getAbsolutePath());
                }
            }
        }
    }

    @FXML
    private void browseDestiny(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Save Directory");

        // TODO Configure initial directory. May need to create a variable with greater scope.
        // directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            tfDestiny.setText(selectedDirectory.getAbsolutePath());
        }
    }
}
