package com.svalero.editor.task;
import javafx.concurrent.Task;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class EditTask extends Task<File> {
    private final File initialFile;
    private final File destinationFolder;
    private final ArrayList<String> selectedFilters;

    public EditTask(File initialFile, File destinationFolder, ArrayList<String> selectedFilters) throws IOException, InterruptedException {
        // TODO It'll need to get the filters in the right order.
        this.initialFile = initialFile;
        this.destinationFolder = destinationFolder;
        this.selectedFilters = selectedFilters;
    }

    @Override
    protected File call() throws Exception {
        Thread.sleep(10000);

        String extension = initialFile.getName().substring(initialFile.getName().lastIndexOf('.'));

        // TODO Remove commentary marks after the Tasks are implemented for real.
/*
        for (String selectedFilter : selectedFilters) {
            if (selectedFilter.equals("Grayscale")) {
                GrayscaleTask grayscaleTask = new EditTask(this.initialFile);
                grayscaleTask.setOnSucceeded(workerStateEvent -> {
                    this.initialFile = grayscaleTask.getValue();
                });
                new Thread(grayscaleTask).start();
            } else if (selectedFilter.equals("Invert Colors")) {
                InvertColorsTask invertColorsTask = new EditTask(this.initialFile);
                invertColorsTask.setOnSucceeded(workerStateEvent -> {
                    this.initialFile = invertColorsTask.getValue();
                });
                new Thread(invertColorsTask).start();
            } else if (selectedFilter.equals("Increase Brightness")) {
                IncreaseBrightnessTask increaseBrightnessTask = new EditTask(this.initialFile);
                increaseBrightnessTask.setOnSucceeded(workerStateEvent -> {
                    this.initialFile = increaseBrightnessTask.getValue();
                });
                new Thread(increaseBrightnessTask).start();
            } else if (selectedFilter.equals("Blur")) {
                BlurTask blurTask = new EditTask(this.initialFile);
                blurTask.setOnSucceeded(workerStateEvent -> {
                    this.initialFile = blurTask.getValue();
                });
                new Thread(blurTask).start();
            }
        }
*/
        File resultFile;
        do {
            String uniqueFileName = UUID.randomUUID() + extension;
            resultFile = new File(destinationFolder, uniqueFileName);
        } while (resultFile.exists());

        // TODO This line must be changed after the filters are implemented.
        Files.copy(initialFile.toPath(), resultFile.toPath());

        // TODO May change the save function to only save when selected on the TabPane itself.

        return resultFile;
    }
}
