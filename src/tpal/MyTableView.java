package tpal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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

        TableColumn imageColumn = new TableColumn();
        imageColumn.setCellValueFactory(new PropertyValueFactory<File, Boolean>("isDirectory"));
        imageColumn.setCellFactory(param -> new booleanTableCell());
        imageColumn.prefWidthProperty().bind(this.widthProperty().divide(20));
        imageColumn.maxWidthProperty().bind(this.widthProperty().divide(20));

        TableColumn pathColumn = new TableColumn("Name");
        pathColumn.setCellValueFactory(new PropertyValueFactory<File, Path>("fullPath"));
        pathColumn.setCellFactory(param -> {
            PathTableCell cell = new PathTableCell();
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new MyEventHandler());
            return cell;
        });
        pathColumn.prefWidthProperty().bind(this.widthProperty().divide(2));

        TableColumn sizeColumn = new TableColumn("Size [B]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<File, String>("size"));
        sizeColumn.setCellFactory(param -> {
            LongTableCell cell = new LongTableCell();
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new MyEventHandler());
            return cell;
        });
        sizeColumn.prefWidthProperty().bind(this.widthProperty().divide(5));

        TableColumn dateColumn = new TableColumn("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<File, String>("creationDate"));
        dateColumn.setCellFactory(param -> {
            DateTableCell cell =  new DateTableCell();
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new MyEventHandler());
            return cell;
        });
        dateColumn.prefWidthProperty().bind(this.widthProperty().divide(5));

        this.setItems(data);
        this.getColumns().addAll(imageColumn, pathColumn, sizeColumn, dateColumn);

    }

    private void setRootDirectory(){
        data.clear();
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
        for(Path name : rootDirectories){
            File file = new File(name);
            data.add(file);
        }
    }




    class MyEventHandler implements  EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent event) {
            TableCell cell = (TableCell) event.getSource();
            int index = cell.getIndex();
            if (index < data.size() && data.get(index).isDirectory()){
                DirectoryStream<Path> dir = null;
                Path path = data.get(index).getFullPath();
                data.clear();
                try {
                    dir = Files.newDirectoryStream(path);
                    for(Path file : dir){
                        File item = new File(file);
                        data.add(item);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    class PathTableCell extends TableCell<File, Path> {
        @Override
        protected void updateItem(Path item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                //jeśli ścieżka to tylko nazwa dysku to wyswietlamy cala sciezke
                if (item.getNameCount() > 0)
                    setText(item.getFileName().toString());
                else
                    setText(item.toString());
            } else {
                setText(null);
            }
        }
    }

    class LongTableCell extends TableCell<File, Long>{
        @Override
        protected void updateItem(Long item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null){
                setText(item.toString());
            }else{
                setText("");
            }
        }
    }

    class DateTableCell extends TableCell<File, FileTime>{
        @Override
        protected void updateItem(FileTime item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null){
                setText(null);
            }else{
                String result = sdf.format(((FileTime)item).toMillis());
                setText(result);
            }
        }
    }

    class booleanTableCell extends TableCell<File, Boolean>{
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (item != null) {
                ImageView iv = null;
                if (item.booleanValue()) {
                    iv = new ImageView(File.folderClosedImage);
                } else {
                    iv = new ImageView(File.fileImage);
                }
                iv.setFitWidth(16);
                iv.setFitHeight(16);
                setGraphic(iv);
            }else {
                setGraphic(null);
            }
        }
    }


}
