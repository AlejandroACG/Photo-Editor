package com.svalero.editor.utils;
import javafx.scene.control.Alert;

public class Alerts {
    public static void errorWritingHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText("History log could not be updated");
        alert.showAndWait();
    }

    public static void errorCreatingHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText("Error creating history log");
        alert.setContentText("Log couldn't be found nor created");
        alert.showAndWait();
    }

    public static void errorCreatingResults() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText("Error creating results directory");
        alert.setContentText("Results directory couldn't be found nor created");
        alert.showAndWait();
    }

    public static void invalidSourcePath() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid Path to Source File(s)");
        alert.setContentText("Please select a valid file or directory to edit");
        alert.showAndWait();
    }

    public static void tooManyTabs() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Too Many Tabs Open");
        alert.setContentText("Close some tabs or set a bigger maximum number of processes");
        alert.showAndWait();
    }

    public static void emptySourceDirectory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Source directory is empty");
        alert.setContentText("There are no images to edit in selected directory");
        alert.showAndWait();
    }

    public static void errorOpeningHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText("History log could not be opened");
        alert.showAndWait();
    }

    public static void invalidTabNumber() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid number");
        alert.setContentText("Max number of processes must be between 1 and 99");
        alert.showAndWait();
    }

    public static void errorOpeningTab() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText("Error opening tab");
        alert.showAndWait();
    }

    public static void filtersSuccess(String sourceName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Filters applied successfully");
        alert.setContentText("Filters were applied successfully to " + sourceName);
        alert.showAndWait();
    }

    public static void filtersFailure(String sourceName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText("Filters couldn't be applied");
        alert.setContentText("There was an error applying filters to " + sourceName);
        alert.showAndWait();
    }

    public static void saveFailure(String sourceName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText("Image couldn't be saved");
        alert.setContentText(sourceName + " couldn't be saved");
        alert.showAndWait();
    }

    public static void filterSuccess(String sourceName, String filter) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(filter + " applied successfully");
        alert.setContentText(filter + " applied successfully to " + sourceName);
        alert.showAndWait();
    }

    public static void filterFailure(String sourceName, String filter) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText(filter + " couldn't be applied");
        alert.setContentText(filter + " couldn't be applied to " + sourceName);
        alert.showAndWait();
    }
}
