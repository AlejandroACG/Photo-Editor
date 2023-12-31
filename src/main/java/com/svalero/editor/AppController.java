package com.svalero.editor;
import static com.svalero.editor.utils.Utils.isImage;
import com.svalero.editor.utils.Alerts;
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
    private TextField tfSource;
    @FXML
    private Button btnBrowseSource;
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
        maxTabs = Integer.parseInt(tfMaxTabs.getText());
        Utils.resultsDirectoryExists();

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
        File sourceFile = new File (tfSource.getText());
        File destinationDirectory = new File(tfDestination.getText());

        if (!destinationDirectory.exists() || !destinationDirectory.isDirectory()) {
            Alerts.invalidDestinationPath();
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
                Alerts.tooManyTabs();
            }
        } else if (sourceFile.isDirectory()) {
            File[] filesInDirectory = sourceFile.listFiles(Utils::isImage);
            if (filesInDirectory != null) {
                if (tpEdits.getTabs().size() + filesInDirectory.length <= maxTabs) {
                    for (File file : filesInDirectory) {
                        createTab(file, destinationDirectory, selectedFilters);
                    }
                } else {
                    Alerts.tooManyTabs();
                }
            } else {
                Alerts.emptySourceDirectory();
            }
        } else {
            Alerts.invalidSourcePath();
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
            Alerts.errorOpeningHistory();
            e.printStackTrace();
        }
    }

    @FXML
    private void browseSource(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Selection Required");
        alert.setHeaderText("Choose an Option");

        ButtonType buttonTypeFile = new ButtonType("Select File");
        ButtonType buttonTypeDirectory = new ButtonType("Select Directory");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeFile, buttonTypeDirectory, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            Stage stage = (Stage) btnBrowseSource.getScene().getWindow();
            if (result.get() == buttonTypeFile) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select File");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

                FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", ".jpeg", "*.bmp");
                fileChooser.getExtensionFilters().add(filter);

                File selectedFile = fileChooser.showOpenDialog(stage);

                if (selectedFile != null && isImage(selectedFile)) {
                    tfSource.setText(selectedFile.getAbsolutePath());
                }
            } else if (result.get() == buttonTypeDirectory) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Directory");
                directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

                File selectedDirectory = directoryChooser.showDialog(stage);
                if (selectedDirectory != null) {
                    tfSource.setText(selectedDirectory.getAbsolutePath());
                }
            }
        }
    }

    @FXML
    private void browseDestination(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Save Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        Stage stage = (Stage) btnBrowseDestination.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            tfDestination.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void changeMaxTabs(ActionEvent event) {
        if (tfMaxTabs.getText().matches("\\d+") && 1 <= maxTabs && maxTabs <= 99) {
            maxTabs = Integer.parseInt(tfMaxTabs.getText());
        } else {
            Alerts.invalidTabNumber();
        }
    }

    private void createTab(File sourceFile, File destinationDirectory, ArrayList<String> selectedFilters) {
        try {
            Tab newTab = new Tab(sourceFile.getName());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit.fxml"));
            loader.setController(new EditController(sourceFile, destinationDirectory, selectedFilters, newTab));
            newTab.setContent(loader.load());
            tpEdits.getTabs().add(newTab);
        } catch (IOException e) {
            Alerts.errorOpeningTab();
            e.printStackTrace();
        }
    }
}
