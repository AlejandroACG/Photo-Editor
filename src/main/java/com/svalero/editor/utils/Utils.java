package com.svalero.editor.utils;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

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
}
