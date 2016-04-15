package tpal;


import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class Controller {

    @FXML private TabPane leftPane;

    @FXML
    public void initialize(){

        MyTableView tableView = new MyTableView();
        leftPane.getTabs().get(0).setContent(tableView);
    }
}