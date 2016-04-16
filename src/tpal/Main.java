package tpal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    public static Locale locale = new Locale("pl");
    private Scene scene;
    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{


        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"), ResourceBundle.getBundle("bundles.Bundle", locale));
        Parent root = loader.load();
        ((Controller)loader.getController()).main = this;
        stage = primaryStage;
        stage.setTitle("Hello World");
        scene = new Scene(root, 1000, 800);
        stage.setScene(scene);

        stage.show();



    }
    public static void main(String[] args) {
        launch(args);
    }

    public void reload() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"), ResourceBundle.getBundle("bundles.Bundle", locale));
        scene = new Scene(root);
        stage.setTitle("GUI");
        stage.setScene(scene);
        stage.show();

    }

}
