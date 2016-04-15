package tpal;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Controller {

    @FXML private TabPane leftPane;

    @FXML
    public void initialize(){

        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();

        ObservableList<File> data = FXCollections.observableArrayList();
        for(Path name : rootDirectories){
            File file = new File(name);
            data.add(file);
        }

        MyTableView tableView = new MyTableView();
        leftPane.getTabs().get(0).setContent(tableView);
    }
}