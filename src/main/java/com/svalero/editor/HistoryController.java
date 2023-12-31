package com.svalero.editor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class HistoryController implements Initializable {
    @FXML
    private javafx.scene.control.TextArea taHistory;
    @FXML
    private Button btnRefresh;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        StringBuilder contentBuilder = new StringBuilder();
        File history = new File("History.txt");
        try (Scanner scanner = new Scanner(history)) {
            while (scanner.hasNextLine()) {
                contentBuilder.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            contentBuilder.append("File couldn't be read.");
            e.printStackTrace();
        }

        taHistory.setText(contentBuilder.toString());
    }

    @FXML
    private void refreshHistory(ActionEvent event) {
        StringBuilder contentBuilder = new StringBuilder();
        File history = new File("History.txt");
        try (Scanner scanner = new Scanner(history)) {
            while (scanner.hasNextLine()) {
                contentBuilder.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            contentBuilder.append("File couldn't be read.");
            e.printStackTrace();
        }

        taHistory.setText(contentBuilder.toString());
    }
}
