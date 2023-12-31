package com.svalero.editor.utils;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
}
