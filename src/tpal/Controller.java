package tpal;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static tpal.Main.locale;

public class Controller implements Initializable {

    @FXML private TabPane leftPane;
    @FXML private TextField leftTextField;
    @FXML private MenuItem langPL;
    @FXML private MenuItem langEN;
    public static ResourceBundle bundle;
    public static Main main;

/*    @FXML
    public void initialize(){

        leftTextField.editableProperty().setValue(false);

        MyTableView tableView = new MyTableView(leftTextField);
        leftPane.getTabs().get(0).setContent(tableView);
    }*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (resources != null)
            loadLang(resources.getLocale().toString());
        else
            loadLang(Locale.getDefault().getLanguage());

        leftTextField.editableProperty().setValue(false);
        MyTableView tableView = new MyTableView(leftTextField);
        leftPane.getTabs().get(0).setContent(tableView);
        leftPane.getTabs().get(0).textProperty().bind(tableView.actualDir);

    }



    @FXML
    public void btnPL(ActionEvent event) throws IOException {
        loadLang("pl");
        Main.stage.close();
        main.reload();
    }

    @FXML
    public void btnEN(ActionEvent event) throws IOException {
        loadLang("en");
        Main.stage.close();
        main.reload();
    }

    private void loadLang(String lang){
        main.locale = new Locale(lang);
        bundle = ResourceBundle.getBundle("bundles.Bundle", locale);

    }

    private void setTextProperties(){

    }

}