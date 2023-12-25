package com.svalero.editor;
import com.svalero.editor.task.EditTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditController implements Initializable {
    private final File initialFile;
    private final File destinationFolder;
    private File resultFile;
    private ArrayList<String> selectedFilters;
    @FXML
    private StackPane spInitialContainer;
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

    // TODO It'll need to get the filters in the right order.
    public EditController(File initialFile, File destinationFolder, ArrayList<String> selectedFilters) {
        this.initialFile = initialFile;
        this.destinationFolder = destinationFolder;
        this.selectedFilters = selectedFilters;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            EditTask editTask = new EditTask(this.initialFile, this.destinationFolder, this.selectedFilters, this.ivInitialImage, this.ivEditedImage);
            pbProgress.progressProperty().bind(editTask.progressProperty());
            editTask.setOnSucceeded(workerStateEvent -> {
                // TODO Maybe change tab color when completed yet unfocused.
            });
            editTask.messageProperty().addListener((observable, oldValue, newValue) -> {
                lblProgressStatus.setText(newValue);
            });

            new Thread(editTask).start();

        } catch (IOException e) {
            // TODO Create Alert.
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
