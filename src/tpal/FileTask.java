package tpal;

import javafx.concurrent.Task;
import javafx.scene.input.TransferMode;
import org.apache.commons.io.CopyProgressListener;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 20.04.2016.
 */
public class FileTask extends Task implements CopyProgressListener {
    private List<java.io.File> source;
    private Path destination;
    private long workMax = 0;
    private long workDone = 0;
    private ProgressWindow progressWindow;
    private List<FileTaskWorkDoneListener> listener = new ArrayList<>();

    private String mode;

    public FileTask(List<java.io.File> source){
        this(source, null, "DELETE");
    }

    public FileTask(List<java.io.File> source, Path destination, String transferMode){
        super();
        this.source = source;
        this.destination = destination;

        this.workDone = 0;
        this.mode = transferMode;

        progressWindow = new ProgressWindow(this);

    }

    @Override
    protected Object call() throws Exception {
        for (File file : source){
            if (file.isDirectory())
                this.workMax += FileUtils.sizeOfDirectory(file);
            else
                this.workMax += FileUtils.sizeOf(file);
        }

        for (File file : source){
            if (mode.equals(TransferMode.COPY.toString())) {
                if (file.isDirectory())
                    FileUtils.copyDirectoryWithListener(file, destination.resolve(file.getName()).toFile(), this);
                else
                    FileUtils.copyFile(file, destination.resolve(file.getName()).toFile(), true, this);
            } else if (mode.equals("DELETE")) {
                FileUtils.deleteQuietly(file, this);
            } else if (mode.equals(TransferMode.MOVE.toString())){
                if (file.isDirectory())
                    FileUtils.moveDirectory(file, destination.resolve(file.getName()).toFile(), this);
                else
                    FileUtils.moveFile(file, destination.resolve(file.getName()).toFile(), this);
            }
        }

        return true;
    }

    @Override
    synchronized public void update(long l, boolean b, File file, File file1) {
        workDone += l;
        if (workDone == 0 && workMax == 0) { //podczas usuwania pustego folderu
            workDone = 1;
            workMax = 1;
        }
        updateProgress(workDone, workMax);
        if (mode.equals("DELETE"))
            updateMessage(file.getAbsolutePath() + " deleted");
        else
            updateMessage(file.getAbsolutePath() + " ==> " + file1.getAbsolutePath());


        if (workDone >= workMax){
            progressWindow.getCancelButton().setDisable(true);
            this.done();
        }
    }

    @Override
    protected void done() {
        if (listener != null && !listener.isEmpty()){
            for (FileTaskWorkDoneListener listener : this.listener) listener.onFileTaskDone();
        }
    }

    public void addOnDoneListener(FileTaskWorkDoneListener listener){
        this.listener.add(listener);
    }

    interface FileTaskWorkDoneListener{
        public void onFileTaskDone();
    }

}
