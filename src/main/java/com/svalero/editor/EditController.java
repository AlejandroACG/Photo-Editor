package com.svalero.editor;
import com.svalero.editor.task.EditTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditController implements Initializable {
    private final File initialFile;
    private final File destinationFolder;
    @FXML
    private StackPane spImageContainer;
    @FXML
    private Button btnUndo;
    @FXML
    private Button btnRedo;
    @FXML
    private ProgressBar pbProgress;
    @FXML
    private Label lblProgressStatus;

    // TODO It'll need to get the filters in the right order.
    public EditController(File initialFile, File destinationFolder) {
        this.initialFile = initialFile;
        this.destinationFolder = destinationFolder;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            EditTask editTask = new EditTask(this.initialFile, this.destinationFolder);
            new Thread(editTask).start();
        } catch (IOException e) {
            // TODO Create Alert.
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
