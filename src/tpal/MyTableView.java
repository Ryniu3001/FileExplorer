package tpal;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;

/**
 * Created by Marcin on 14.04.2016.
 */
@SuppressWarnings("unchecked")
public class MyTableView extends TableView {

    public static final String BACK_ACTION_STRING = "<--";

    public SimpleStringProperty actualDir;

    private TextField textField;
    private final ObservableList<File> data = FXCollections.observableArrayList();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    public MyTableView(TextField tf){
        super();
        setComputerRootDirectory();
        actualDir = new SimpleStringProperty("\\");
        textField = tf;

        TableColumn imageColumn = new TableColumn();
        imageColumn.setCellValueFactory(new PropertyValueFactory<File, Boolean>("isDirectory"));
        imageColumn.setCellFactory(param -> new booleanTableCell());
        imageColumn.prefWidthProperty().bind(this.widthProperty().divide(20));
        imageColumn.maxWidthProperty().bind(this.widthProperty().divide(20));

        TableColumn pathColumn = new TableColumn(Controller.bundle.getString("column.filename"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<File, String>("name"));
        pathColumn.setCellFactory(param -> {
            StringTableCell cell = new StringTableCell();
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new MyEventHandler());
            return cell;
        });
        pathColumn.prefWidthProperty().bind(this.widthProperty().divide(2));
        pathColumn.setComparator((o1, o2) -> {
            System.out.println(pathColumn.getSortType());
            if (((String)o1).equals(BACK_ACTION_STRING))
                return -1;
            else if ((((String)o2).equals(BACK_ACTION_STRING)))
                return 1;
            else
                return  ((String)o1).compareTo((String)o2);
        });

        TableColumn sizeColumn = new TableColumn(Controller.bundle.getString("column.size"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<File, String>("size"));
        sizeColumn.setCellFactory(param -> {
            LongTableCell cell = new LongTableCell();
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new MyEventHandler());
            return cell;
        });
        sizeColumn.prefWidthProperty().bind(this.widthProperty().divide(5));

        TableColumn dateColumn = new TableColumn(Controller.bundle.getString("column.date"));
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

    /**
     * Ustawia katalog glowny dla tabeli na katalog root systemu
     */
    private void setComputerRootDirectory(){
        data.clear();
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
        for(Path name : rootDirectories){
            File file = new File(name);
            data.add(file);
        }
    }

    /**
     * Ustawia katalog główny dla tabeli na wskazany w parametrze.
     * @param path
     */
    private void setTreeRootDirectory(Path path){
        DirectoryStream<Path> dir;
        data.clear();

        /* Nie da sie pobrac Path do sciezki z wylistowanymi dyskami, dlatego
            obejściem jest wstawienie sztucznej ścieżki i przy klienięciu porównywanie.
            Jeśli będzie tam znak "\" to należy wyświetlić listę dysków
         */
        File parent;
        if (path.equals(path.getRoot())){
            parent = new File(Paths.get("\\"));
        }else{
            parent = new File(path.getParent());
        }
        parent.setName(BACK_ACTION_STRING);
        data.add(parent);

        try {
            dir = Files.newDirectoryStream(path);
            for(Path file : dir){
                File item = new File(file);
                data.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Odmowa dostępu");
            alert.setHeaderText("");
            alert.setContentText("Brak uprawnień");
            alert.showAndWait();
        }

    }


    class MyEventHandler implements  EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent event) {
            TableCell cell = (TableCell) event.getSource();
            int index = cell.getIndex();
            if (index < data.size() && data.get(index).isDirectory()){
                textField.setText(data.get(index).getFullPath().toString());
                actualDir.set(data.get(index).getName());
                if (data.get(index).getFullPath().toString().equals("\\"))
                    setComputerRootDirectory();
                else
                    setTreeRootDirectory(data.get(index).getFullPath());
            }

        }
    }

    class StringTableCell extends TableCell<File, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item);
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
                String result = sdf.format(item.toMillis());
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
                ImageView iv;
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
