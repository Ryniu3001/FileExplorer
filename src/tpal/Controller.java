package tpal;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static tpal.Main.locale;

public class Controller implements Initializable {

    @FXML private TabPane leftPane;
    @FXML private TextField leftTextField;
    @FXML private TabPane rightPane;
    @FXML private TextField rightTextField;
    @FXML private MenuItem langPL;
    @FXML private MenuItem langEN;
    @FXML private Button leftUpButton;
    @FXML private Button rightUpButton;
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
        leftPane.getTabs().get(0).textProperty().bind(tableView.actualDirProperty());
        leftUpButton.disableProperty().bind(tableView.disableUpButtonProperty());

        rightTextField.editableProperty().setValue(false);
        MyTableView tableView2 = new MyTableView(rightTextField);
        rightPane.getTabs().get(0).setContent(tableView2);
        rightPane.getTabs().get(0).textProperty().bind(tableView2.actualDirProperty());
        rightUpButton.disableProperty().bind(tableView2.disableUpButtonProperty());



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

    @FXML
    private void onLeftUpButtonAction(MouseEvent event){
        MyTableView table = (MyTableView) leftPane.getSelectionModel().getSelectedItem().getContent();
        table.goUp();
    }

    @FXML
    private void onRightUpButtonAction(MouseEvent event){
        MyTableView table = (MyTableView) rightPane.getSelectionModel().getSelectedItem().getContent();
        table.goUp();
    }

}