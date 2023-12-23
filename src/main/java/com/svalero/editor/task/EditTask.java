package com.svalero.editor.task;
import javafx.concurrent.Task;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class EditTask extends Task<Integer> {
    private File initialFile;
    private File destinationFolder;

    public EditTask(File initialFile, File destinationFolder) throws IOException {
        this.initialFile = initialFile;
        this.destinationFolder = destinationFolder;

        // TODO It'll need to get the filters in the right order.
        String extension = initialFile.getName().substring(initialFile.getName().lastIndexOf('.'));

        File destinationFile;
        do {
            String uniqueFileName = UUID.randomUUID() + extension;
            destinationFile = new File(destinationFolder, uniqueFileName);
        } while (destinationFile.exists());

        // TODO This line must be changed after the filters are implemented.
        Files.copy(initialFile.toPath(), destinationFile.toPath());

        // TODO May change the save function to only save when selected on the TabPane itself.
    }
    @Override
    protected Integer call() throws Exception {
        return null;
    }
}
