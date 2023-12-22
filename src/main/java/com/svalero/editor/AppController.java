package com.svalero.editor;
import static com.svalero.editor.Utils.isImage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    @FXML
    private Button btnApply;
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
    private void launchEdit(ActionEvent event) {}

    @FXML
    private void changeDestiny(ActionEvent event) {}

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
