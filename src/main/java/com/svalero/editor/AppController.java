package com.svalero.editor;
import static com.svalero.editor.Utils.isImage;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.tpEdits.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        ObservableList<String> choiceBoxOptions = FXCollections.observableArrayList("None", "Grayscale", "Invert Colors", "Increase Brightness", "Blur");
        cb1.setItems(choiceBoxOptions);
        cb2.setItems(choiceBoxOptions);
        cb3.setItems(choiceBoxOptions);
        cb4.setItems(choiceBoxOptions);

        cb2.setDisable(true);
        cb3.setDisable(true);
        cb4.setDisable(true);

        cb1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("None".equals(newValue)) { cb1.setValue(null); }
            if (newValue == null) {
                cb2.setValue(null);
                cb2.setDisable(true);
            };
        });

        cb2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("None".equals(newValue)) { cb2.setValue(null); }
            if (newValue == null) {
                cb3.setValue(null);
                cb3.setDisable(true);
            };
        });

        cb3.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("None".equals(newValue)) { cb3.setValue(null); }
            if (newValue == null) {
                cb4.setValue(null);
                cb4.setDisable(true);
            };
        });

        cb4.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("None".equals(newValue)) { cb4.setValue(null); }
        });
    }

    @FXML
    private void launchEdit(ActionEvent event) {
        // TODO ¿Debería todo esto ser concurrente también? Probablemente hasta los browsers deberían serlo.
        String originPath = tfOrigin.getText();
        String destinationPath = tfDestination.getText();
        // TODO Code the actual filters.
        if (originPath.isEmpty() || destinationPath.isEmpty() || cb1 == null) {
            // TODO Create an alert. Could split the error in three.
            return;
        }

        ArrayList<String> selectedFilters = new ArrayList<>();
        selectedFilters.add(cb1.getValue());
        if (cb2.getValue() != null) selectedFilters.add(cb2.getValue());
        if (cb3.getValue() != null) selectedFilters.add(cb3.getValue());
        if (cb4.getValue() != null) selectedFilters.add(cb4.getValue());

        File initialFile = new File(originPath);
        File destinationFolder = new File(destinationPath);

        if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
            // TODO Create an alert.
            return;
        }

        if (isImage(initialFile)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("tabpane.fxml"));
                // TODO Will need to feed the new controller something more.
                loader.setController(new EditController(initialFile, destinationFolder, selectedFilters));
                // TODO Customize tab names or take them out entirely.
                tpEdits.getTabs().add(new Tab("Tab Name", loader.load()));

            } catch (IOException e) {
                // TODO Create Alert.
            }
        } else if (initialFile.isDirectory()) {
            File[] filesInDirectory = initialFile.listFiles(Utils::isImage);
            if (filesInDirectory != null) {
                for (File file : filesInDirectory) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("tabpane.fxml"));
                        EditController editController = new EditController(file, destinationFolder, selectedFilters);
                        loader.setController(editController);
                        // TODO Customize tab names or take them out entirely.
                        tpEdits.getTabs().add(new Tab("Tab Name", loader.load()));
                    } catch (IOException e) {
                        // TODO Create Alert.
                    }
                }
            }
        } else {
            // TODO Create an Alert: the initial file was neither an image nor a folder.
        }
    }

    @FXML
    private void viewHistory(ActionEvent event) {}

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

                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", ".jpeg", "*.gif", "*.bmp");
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
}
