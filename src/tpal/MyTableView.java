package tpal;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Marcin on 14.04.2016.
 */
@SuppressWarnings("unchecked")
public class MyTableView extends TableView{

    private SimpleStringProperty actualDir;
    private SimpleBooleanProperty disableUpButton = new SimpleBooleanProperty(false);
    private Path actualPath;

    private TextField textField;
    private final ObservableList<MyFile> data = FXCollections.observableArrayList();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    public MyTableView(TextField tf){
        super();
        actualDir = new SimpleStringProperty("\\");
        textField = tf;
        setComputerRootDirectory();


        TableColumn imageColumn = new TableColumn();
        imageColumn.setCellValueFactory(new PropertyValueFactory<MyFile, Boolean>("isDirectory"));
        imageColumn.setCellFactory(param -> new booleanTableCell());
        imageColumn.prefWidthProperty().bind(this.widthProperty().divide(20));
        imageColumn.maxWidthProperty().bind(this.widthProperty().divide(20));

        TableColumn pathColumn = new TableColumn(Controller.bundle.getString("column.filename"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<MyFile, String>("name"));
        pathColumn.setCellFactory(param -> {
            StringTableCell cell = new StringTableCell();
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new MyEventHandler());
            return cell;
        });
        pathColumn.prefWidthProperty().bind(this.widthProperty().divide(2));

        TableColumn sizeColumn = new TableColumn(Controller.bundle.getString("column.size"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<MyFile, String>("size"));
        sizeColumn.setCellFactory(param -> {
            LongTableCell cell = new LongTableCell();
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new MyEventHandler());
            return cell;
        });
        sizeColumn.prefWidthProperty().bind(this.widthProperty().divide(5));

        TableColumn dateColumn = new TableColumn(Controller.bundle.getString("column.date"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<MyFile, String>("creationDate"));
        dateColumn.setCellFactory(param -> {
            DateTableCell cell =  new DateTableCell();
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new MyEventHandler());
            return cell;
        });
        dateColumn.prefWidthProperty().bind(this.widthProperty().divide(5));

        this.setItems(data);
        this.getColumns().addAll(imageColumn, pathColumn, sizeColumn, dateColumn);

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.setContextMenu();
        this.setDragMechanism();

    }

    /**
     * Ustawia katalog glowny dla tabeli na katalog root systemu
     */
    private void setComputerRootDirectory(){
        data.clear();
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
        for(Path name : rootDirectories){
            MyFile file = new MyFile(name);
            data.add(file);
        }
        textField.setText("\\");
        actualDir.set("\\");
        actualPath = null;
        disableUpButton.set(true);

    }

    /**
     * Ustawia katalog główny dla tabeli na wskazany w parametrze.
     * @param f
     */
    private void setTreeRootDirectory(MyFile f){
        Path path = f.getFullPath();
        DirectoryStream<Path> dir;
        data.clear();

        try {
            dir = Files.newDirectoryStream(path);
            for(Path file : dir){
                MyFile item = new MyFile(file);
                data.add(item);
            }
            actualDir.set(f.getName());
            actualPath = path;
            textField.setText(actualPath.toString());
            disableUpButton.set(false);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Odmowa dostępu");
            alert.setHeaderText("");
            alert.setContentText("Brak uprawnień");
            alert.showAndWait();
        }

    }

    /**
     * Klikniecie na komorke na liscie
     */
    class MyEventHandler implements  EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent event) {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                TableCell cell = (TableCell) event.getSource();
                int index = cell.getIndex();
                if (index >= data.size())
                    getSelectionModel().clearSelection();

                if (event.getClickCount() == 2) {
                    if (index < data.size() && data.get(index).isDirectory()) {
                        setTreeRootDirectory(data.get(index));
                    }
                }
            }
        }
    }

    class StringTableCell extends TableCell<MyFile, String> {
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

    class LongTableCell extends TableCell<MyFile, Long>{
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

    class DateTableCell extends TableCell<MyFile, FileTime>{
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

    class booleanTableCell extends TableCell<MyFile, Boolean>{
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (item != null) {
                ImageView iv;
                if (item.booleanValue()) {
                    iv = new ImageView(MyFile.folderClosedImage);
                } else {
                    iv = new ImageView(MyFile.fileImage);
                }
                iv.setFitWidth(16);
                iv.setFitHeight(16);
                setGraphic(iv);
            }else {
                setGraphic(null);
            }
        }
    }

    //TODO: Usuwanie katalogow do ktorych nie mamy praw dostepu
    private void setContextMenu(){
        MenuItem delete = new MenuItem(Controller.bundle.getString("remove.menuitem"));
        delete.setOnAction(event -> {
            if (this.getSelectionModel().getSelectedItem() != null && showConfirmationDialog("remove.header", "remove.text", null)){
                List<MyFile> files = (List<MyFile>) this.getSelectionModel().getSelectedItems();
                List<File> list = new ArrayList<File>();
                for (MyFile f : files)
                    list.add(f.getFullPath().toFile());

                FileTask task = new FileTask(list);

                task.addOnDoneListener(() -> refreshDir());
                new Thread(task).start();
            }
        });

        ContextMenu ctxMenu = new ContextMenu(delete);
        this.setContextMenu(ctxMenu);
    }

    public static boolean showConfirmationDialog(String header, String content, File file){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Controller.bundle.getString("remove.title"));
        alert.setHeaderText(Controller.bundle.getString(header));
        if (file == null)
            alert.setContentText(Controller.bundle.getString(content));
        else
            alert.setContentText(Controller.bundle.getString(content) + System.lineSeparator() + file.getAbsolutePath());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    private void setDragMechanism(){
        this.setOnDragDetected(event -> {
            Dragboard dragBoard = startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ClipboardContent content = new ClipboardContent();
            List<MyFile> selectedFiles = (List<MyFile>)((MyTableView)event.getSource()).getSelectionModel().getSelectedItems();
            List<java.io.File> list = new ArrayList<java.io.File>();
            for (MyFile file : selectedFiles){
                list.add(file.getFullPath().toFile());
            }
            content.putFiles(new ArrayList<java.io.File>(list));
            dragBoard.setContent(content);
            event.consume();
        });

        this.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (event.getGestureSource() == this){
                event.acceptTransferModes(TransferMode.NONE);
            }else if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                System.out.println("Dropped: ");
                Path destinationPath = null;
                if (event.getTarget() instanceof TableCell) {
                    TableCell v = ((TableCell) event.getTarget());
                    destinationPath = ((MyTableView)v.getTableView()).actualPath;
                }else{
                    MyTableView view = (MyTableView)((StackPane)event.getTarget()).getParent();
                    destinationPath = view.actualPath;
                }

                //Path source = db.getFiles().get(0).toPath();
                List<Path> sources = new ArrayList<Path>();
                for (java.io.File file : db.getFiles())
                    sources.add(file.toPath());
                //Path destination = ((MyTableView)v.getTableView()).actualPath.resolve(db.getFiles().get(0).toPath().getFileName());
                Path destination = destinationPath;
                if (!sources.contains(destination)) {
                    FileTask task = new FileTask(db.getFiles(), destination, event.getTransferMode().toString());
                    task.addOnDoneListener(() -> refreshDir());
                    task.addOnDoneListener(() -> ((MyTableView) event.getGestureSource()).refreshDir());
                    new Thread(task).start();
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void goUp() {
        if (actualPath.getParent() == null)
            setComputerRootDirectory();
        else
            setTreeRootDirectory(new MyFile(actualPath.getParent()));
    }

    public void refreshDir(){
        setTreeRootDirectory(new MyFile(actualPath));
    }

    public String getActualDir() {
        return actualDir.get();
    }

    public SimpleStringProperty actualDirProperty() {
        return actualDir;
    }

    public void setActualDir(String actualDir) {
        this.actualDir.set(actualDir);
    }

    public boolean getDisableUpButton() {
        return disableUpButton.get();
    }

    public SimpleBooleanProperty disableUpButtonProperty() {
        return disableUpButton;
    }

    public void setDisableUpButton(boolean disableUpButton) {
        this.disableUpButton.set(disableUpButton);
    }

}
