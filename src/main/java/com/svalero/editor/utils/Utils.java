package com.svalero.editor.utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {
    public static boolean isImage(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||fileName.endsWith(".bmp");
    }

    public static BufferedImage copyBufferedImage(BufferedImage originalImage) {
        BufferedImage copyOfImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        Graphics2D g = copyOfImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        return copyOfImage;
    }

    public static void historyFileExists(File historyFile) {
        if (!historyFile.exists()) {
            try {
                boolean created = historyFile.createNewFile();
                if (!created) {
                    Alerts.errorCreatingHistory();
                    System.out.println("Error creating history log");
                }
            } catch (IOException e) {
                Alerts.errorCreatingHistory();
                e.printStackTrace();
            }
        }
    }

    public static void resultsDirectoryExists() {
        File resultsDirectory = new File ("Results");
        if (!resultsDirectory.exists()) {
            boolean created = resultsDirectory.mkdir();
            if (!created) {
                Alerts.errorCreatingResults();
                System.out.println("Error creating history log");
            }
        }
    }

    public static void choiceBoxSetUp(ChoiceBox<String> cb1, ChoiceBox<String> cb2, ChoiceBox<String> cb3,
                                      ChoiceBox<String> cb4) {
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

    public static void selectedFiltersFill(ArrayList<String> selectedFilters, ChoiceBox<String> cb1,
                                           ChoiceBox<String> cb2, ChoiceBox<String> cb3,
                                           ChoiceBox<String> cb4) {
        selectedFilters.add(cb1.getValue());
        if (cb2.getValue() != null) selectedFilters.add(cb2.getValue());
        if (cb3.getValue() != null) selectedFilters.add(cb3.getValue());
        if (cb4.getValue() != null) selectedFilters.add(cb4.getValue());
    }
}
