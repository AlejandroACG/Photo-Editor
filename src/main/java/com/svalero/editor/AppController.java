package com.svalero.editor;
import static com.svalero.editor.utils.Utils.isImage;
import com.svalero.editor.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    // TODO Add runLater to every place needed
    // TODO If default save path doesn't exist, create it
    // TODO Some catch hace Alerts but no proper depuration
    private int maxTabs;
    @FXML
    private TextField tfDestination;
    @FXML
    private Button btnHistory;
    @FXML
    private Label lblSavePath;
    @FXML
    private TabPane tpEdits;
    @FXML
    private Button btnEdit;
    @FXML
    private ChoiceBox<String> cb1;
    @FXML
    private ChoiceBox<String> cb2;
    @FXML
    private ChoiceBox<String> cb3;
    @FXML
    private ChoiceBox<String> cb4;
    @FXML
    private TextField tfOrigin;
    @FXML
    private Button btnBrowseOrigin;
    @FXML
    private Button btnBrowseDestination;
    @FXML
    private TextField tfMaxTabs;
    @FXML
    private Button btnMaxTabs;
    @FXML
    private Label lblMaxTabs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO Add X buttons.
        // TODO Add "No History" Alert when trying to view it but the file has been deleted.
        // TODO Move Alerts to their own method and call them from there.
        this.maxTabs = Integer.parseInt(tfMaxTabs.getText());

        ObservableList<String> choiceBoxOptions1 = FXCollections.observableArrayList("Grayscale", "Invert Colors", "Increase Brightness", "Blur");
        ObservableList<String> choiceBoxOptions2 = FXCollections.observableArrayList(" ", "Grayscale", "Invert Colors", "Increase Brightness", "Blur");
        cb1.setItems(choiceBoxOptions1);
        cb2.setItems(choiceBoxOptions2);
        cb3.setItems(choiceBoxOptions2);
        cb4.setItems(choiceBoxOptions2);

        cb1.setValue(cb1.getItems().get(0));
        cb3.setDisable(true);
        cb4.setDisable(true);

        cb2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (" ".equals(newValue) || newValue == null) {
                cb2.setValue(null);
                cb3.setValue(null);
                cb3.setDisable(true);
            } else {
                cb3.setDisable(false);
            }
        });

        cb3.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (" ".equals(newValue) || newValue == null) {
                cb3.setValue(null);
                cb4.setValue(null);
                cb4.setDisable(true);
            } else {
                cb4.setDisable(false);
            }
        });

        cb4.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (" ".equals(newValue)) cb4.setValue(null);
        });
    }

    @FXML
    private void launchEdit(ActionEvent event) {
        String originPath = tfOrigin.getText();
        File sourceFile = new File(originPath);
        File destinationDirectory = new File(tfDestination.getText());

        if (originPath.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("No Valid Path to Source File(s)");
            alert.setContentText("Please select a valid file or directory to edit.");
            alert.showAndWait();
            return;
        }

        if (!destinationDirectory.exists() || !destinationDirectory.isDirectory()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("No Valid Destination Directory");
            alert.setContentText("Please select a valid directory to save the edits.");
            alert.showAndWait();
            return;
        }

        ArrayList<String> selectedFilters = new ArrayList<>();
        selectedFilters.add(cb1.getValue());
        if (cb2.getValue() != null) selectedFilters.add(cb2.getValue());
        if (cb3.getValue() != null) selectedFilters.add(cb3.getValue());
        if (cb4.getValue() != null) selectedFilters.add(cb4.getValue());

        if (isImage(sourceFile)) {
            if (tpEdits.getTabs().size() < maxTabs) {
                createTab(sourceFile, destinationDirectory, selectedFilters);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Too Many Tabs Open");
                alert.setContentText("Close some tabs or set a bigger maximum number of processes.");
                alert.showAndWait();
            }
        } else if (sourceFile.isDirectory()) {
            File[] filesInDirectory = sourceFile.listFiles(Utils::isImage);
            if (filesInDirectory != null) {
                if (tpEdits.getTabs().size() + filesInDirectory.length <= maxTabs) {
                    for (File file : filesInDirectory) {
                        createTab(file, destinationDirectory, selectedFilters);
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Too Many Tabs Open");
                    alert.setContentText("Close some tabs or set a bigger maximum number of processes.");
                    alert.showAndWait();
                }
            } else {
                // TODO Create "No Images in Directory" Alert
            }
        } else {
            // TODO Create an Alert: the initial file was neither an image nor a folder.
        }
    }

    @FXML
    private void viewHistory(ActionEvent event) {
        Dialog<Void> dialog = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("history.fxml"));
            loader.setController(new HistoryController());

            dialog = new Dialog<>();
            dialog.setTitle("History");
            dialog.getDialogPane().setContent(loader.load());

            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.setResizable(true);
            stage.setMinWidth(615);
            stage.setMinHeight(460);
            stage.setOnCloseRequest(e -> stage.hide());

            dialog.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void browseOrigin(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Selection Required");
        alert.setHeaderText("Choose an Option");

        ButtonType buttonTypeFile = new ButtonType("Select File");
        ButtonType buttonTypeDirectory = new ButtonType("Select Directory");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeFile, buttonTypeDirectory, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            Stage stage = (Stage) this.btnBrowseOrigin.getScene().getWindow();
            if (result.get() == buttonTypeFile) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select File");
                // TODO Configure initial directory. May need to create a variable with greater scope.

                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", ".jpeg", "*.bmp");
                fileChooser.getExtensionFilters().add(filter);

                File selectedFile = fileChooser.showOpenDialog(stage);

                if (selectedFile != null && isImage(selectedFile)) {
                    tfOrigin.setText(selectedFile.getAbsolutePath());
                }
                // TODO Add failure message?
            } else if (result.get() == buttonTypeDirectory) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Directory");
                // TODO Configure initial directory. May need to create a variable with greater scope.

                File selectedDirectory = directoryChooser.showDialog(stage);
                if (selectedDirectory != null) {
                    tfOrigin.setText(selectedDirectory.getAbsolutePath());
                }
            }
        }
    }

    @FXML
    private void browseDestination(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Save Directory");
        // TODO Configure initial directory. May need to create a variable with greater scope.

        Stage stage = (Stage) this.btnBrowseDestination.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            tfDestination.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void changeMaxTabs(ActionEvent event) {
        if (tfMaxTabs.getText().matches("\\d+")) {
            int maxTabsInt = Integer.parseInt(tfMaxTabs.getText());
            if (maxTabsInt <= 99) {
                maxTabs = maxTabsInt;
            } else {
                // TODO Create "No numbers above two digits" Alert.
            }
        } else {
            // TODO Create "Not a Valid Number" Alert.
        }
    }

    private void createTab(File sourceFile, File destinationDirectory, ArrayList<String> selectedFilters) {
        try {
            // TODO Customize tab names or take them out entirely.
            Tab newTab = new Tab("Tab Name");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
            loader.setController(new EditController(sourceFile, destinationDirectory, selectedFilters, newTab));
            newTab.setContent(loader.load());
            tpEdits.getTabs().add(newTab);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error opening tab");
            alert.setContentText("New tab couldn't be opened: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();

        }
    }
}
