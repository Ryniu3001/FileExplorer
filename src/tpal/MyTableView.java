package tpal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;

/**
 * Created by Marcin on 14.04.2016.
 */
public class MyTableView extends TableView {

    private ObservableList<File> data = FXCollections.observableArrayList();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    public MyTableView(){
        super();
        setRootDirectory();

        TableColumn firstColumn = new TableColumn("First");
        firstColumn.setCellValueFactory(new PropertyValueFactory<File, String>("name"));

        TableColumn secondColumn = new TableColumn("Second");
        secondColumn.setCellValueFactory(new PropertyValueFactory<File, String>("size"));

        TableColumn third = new TableColumn("Third");
        third.setCellValueFactory(new PropertyValueFactory<File, String>("creationDate"));
        third.setCellFactory(param -> {
            return new TableCell(){
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null){
                        setText(null);
                    }else{
                        String result = sdf.format(((FileTime)item).toMillis());
                        setText(result);
                    }
                }
            };

        });

        this.setItems(data);
        this.getColumns().addAll(firstColumn, secondColumn, third);

    }

    private void setRootDirectory(){
        data.clear();
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
        for(Path name : rootDirectories){
            File file = new File(name);
            data.add(file);
        }
    }

}
