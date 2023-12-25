package com.svalero.editor;
import java.io.File;

public class Utils {
    public static boolean isImage(File file) {
        // TODO Maybe I should remove .gif from here
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".gif") || fileName.endsWith(".bmp");
    }
}
