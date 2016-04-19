package tpal;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

/**
 * Created by Marcin on 19.04.2016.
 */

//TODO: zamienic na sam progressBar, ktory będzie tworzony i pokazywany w klasie FileTask (sprawdzic czy to nie bedize wplywalo na process UI progress bara)
public class ProgressWindow {

    final Label label = new Label("Files transfer:");
    final ProgressBar progressBar = new ProgressBar(0);
    final ProgressIndicator progressIndicator = new ProgressIndicator(0);
    final Button cancelButton = new Button("Cancel");
    final TextArea textArea = new TextArea();

    public ProgressWindow(java.io.File source, java.io.File destination){
        progressBar.setProgress(0);
        progressIndicator.setProgress(0);
        textArea.setText("");
        cancelButton.setDisable(false);

        Task copyWorker = createWorker(source, destination);

      /*  progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(copyWorker.progressProperty());

        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(copyWorker.progressProperty());*/
        copyWorker.messageProperty().addListener((observable, oldValue, newValue) -> {
            textArea.appendText(newValue + "\n");
        });

        new Thread(copyWorker).start();

        cancelButton.setOnAction(event -> {
            cancelButton.setDisable(true);
            copyWorker.cancel(true);

            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            progressIndicator.progressProperty().unbind();
            progressIndicator.setProgress(0);
            textArea.appendText("File transfer was canceled.");
        });

        VBox vb = new VBox();
        progressBar.prefWidthProperty().bind(vb.widthProperty());
        cancelButton.prefWidthProperty().bind(vb.widthProperty());
        vb.getChildren().addAll(progressBar, progressIndicator,textArea,cancelButton);

        Stage stage = new Stage();
        stage.setScene(new Scene(vb,400,300));
        stage.show();


    }

    private Task createWorker(java.io.File source, java.io.File destination){
        return new Task() {
            @Override
            protected Object call() throws Exception {
                //FileUtils.copyDirectoryWithListener(source, destination, listener );
                return true;
            }
        };
    }

}
