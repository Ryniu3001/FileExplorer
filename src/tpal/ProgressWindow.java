package tpal;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by Marcin on 19.04.2016.
 */

public class ProgressWindow {

    final ProgressBar progressBar = new ProgressBar(0);
    final ProgressIndicator progressIndicator = new ProgressIndicator(0);
    final Button cancelButton = new Button("Cancel");
    final TextArea textArea = new TextArea();

    public ProgressWindow(Task worker){
        progressBar.setProgress(0);
        progressIndicator.setProgress(0);
        textArea.setText("");
        textArea.setEditable(false);

        cancelButton.setDisable(false);

        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(worker.progressProperty());


        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(worker.progressProperty());
        worker.messageProperty().addListener((observable, oldValue, newValue) -> {
            textArea.appendText(newValue + "\n");
        });

        cancelButton.setOnAction(event -> {
            cancelButton.setDisable(true);
            worker.cancel(true);

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
        stage.setTitle("File Transfer");
        stage.show();


    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
