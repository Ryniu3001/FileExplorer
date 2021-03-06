package tpal;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.input.TransferMode;
import org.apache.commons.io.CopyProgressListener;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.apache.commons.io.FileUtils.deleteQuietly;

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
    private static final String deleteOperationType = "DELETE";

    public FileTask(List<java.io.File> source){
        this(source, null, deleteOperationType);
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
                if (file.isDirectory()){
                    boolean copy = true;
                    if (destination.resolve(file.getName()).toFile().exists()){
                        copy = askUser("move.header","move.direcotry.text", destination.resolve(file.getName()).toFile());
                    }
                    if (copy)
                        FileUtils.copyDirectoryWithListener(file, destination.resolve(file.getName()).toFile(), this);
                    else
                        update(FileUtils.sizeOfDirectory(file), true, file, destination.resolve(file.getName()).toFile(), null);
                }
                else {
                    boolean copy = true;
                    if (destination.resolve(file.getName()).toFile().exists()){
                        copy = askUser("move.header","move.text", destination.resolve(file.getName()).toFile());
                    }
                    if (copy)
                        FileUtils.copyFile(file, destination.resolve(file.getName()).toFile(), true, this);
                    else
                        update(FileUtils.sizeOf(file), true, file, destination.resolve(file.getName()).toFile(), null);
                }
            } else if (mode.equals(deleteOperationType)) {
                deleteQuietly(file, this);

            } else if (mode.equals(TransferMode.MOVE.toString())){
                if (file.isDirectory()) {
                    boolean move = true;
                    if (destination.resolve(file.getName()).toFile().exists()) {
                        move = askUser("move.header","move.direcotry.text", destination.resolve(file.getName()).toFile());
                    }
                    if (move)
                        FileUtils.moveDirectory(file, destination.resolve(file.getName()).toFile(), this);
                    else
                        update(FileUtils.sizeOfDirectory(file), true, file, destination.resolve(file.getName()).toFile(), null);

                }
                else {
                    boolean move = true;
                    if (destination.resolve(file.getName()).toFile().exists()){
                        move = askUser("move.header","move.text", destination.resolve(file.getName()).toFile());
                        if (move) FileUtils.deleteQuietly(destination.resolve(file.getName()).toFile());
                    }
                    if (move)
                        FileUtils.moveFile(file, destination.resolve(file.getName()).toFile(), this);
                    else
                        update(FileUtils.sizeOf(file), true, file, destination.resolve(file.getName()).toFile(), null);
                }
            }
        }

        FileUtils.operationCanceled = false;
        //this.done();
        return true;
    }

    private boolean askUser(String a, String b, File f) throws ExecutionException, InterruptedException {
        FutureTask<Boolean> futureTask = new FutureTask(() -> {
            return MyTableView.showConfirmationDialog(a,b, f);
        });
        Platform.runLater(futureTask);
        return futureTask.get();
    }

    @Override
    synchronized public void update(long l, boolean b, File file, File file1, String info) {
        workDone += l;
        if (workDone == 0 && workMax == 0) { //podczas usuwania pustego folderu
            workDone = 1;
            workMax = 1;
        }
        updateProgress(workDone, workMax);
        if (info != null){
            updateMessage(info);
        }else if (mode.equals(deleteOperationType))
            if (file != null && b==true)
                updateMessage(file.getAbsolutePath() + " deleted");
            else if (file != null && b==false)
                updateMessage(file.getAbsolutePath() + " access denied");
        else if (file != null && file1 != null)
            updateMessage(file.getAbsolutePath() + " ==> " + file1.getAbsolutePath());

        if (workDone >= workMax){
            progressWindow.getCancelButton().setDisable(true);
            System.out.println("DONE");

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
