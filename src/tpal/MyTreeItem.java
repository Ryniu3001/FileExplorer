package tpal;

import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by Marcin on 13.04.2016.
 */
@SuppressWarnings("unchecked")
public class MyTreeItem extends TreeItem{

    public final static Image folderClosedImage = new Image(ClassLoader.getSystemResourceAsStream("resources/close-folder.png"));
    public final static Image folderOpenedImage = new Image(ClassLoader.getSystemResourceAsStream("resources/open-folder.png"));
    public final static Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("resources/file.png"));
    private TreeItem emptyTI = new TreeItem("Empty");

    private String fullPath;
    private boolean isDirectory;
    private boolean isEmpty;
    private static MyTreeItem rootInstance;

    public static MyTreeItem getRootInstance(String name){
        if (rootInstance == null){
            rootInstance = new MyTreeItem(name);
        }
        return rootInstance;
    }

    public MyTreeItem(String root){
        this.setValue(root);
        //dodaje handlera tylko do ROOTa
        this.addEventHandler(TreeItem.branchExpandedEvent(), (e) -> {
            MyTreeItem src = (MyTreeItem) e.getSource();

            if (src.isDirectory() && src.isExpanded()){
                ImageView iv = (ImageView)src.getGraphic();
                iv.setImage(folderOpenedImage);
            }
            System.out.println(e.getTarget());
            System.out.println(e.getEventType());
            if (src.isEmpty){
                Path p = Paths.get(src.getFullPath());
                try {
                    BasicFileAttributes attribs = Files.readAttributes(p, BasicFileAttributes.class);

                    if (attribs.isDirectory()){
                        DirectoryStream<Path> dir = Files.newDirectoryStream(p);
                        for(Path file : dir){
                            MyTreeItem item = new MyTreeItem(file);
                            src.getChildren().add(item);
                        }
                        if (src.getChildren().size() > 1){
                            src.isEmpty = false;
                            src.getChildren().remove(src.getEmptyTI());
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Odmowa dostępu");
                    alert.setHeaderText("");
                    alert.setContentText("Brak uprawnień");
                    alert.showAndWait();
                }
            }else{

                //TODO: Rescannig directory for changes (remove files)
            }
        });

        this.addEventHandler(TreeItem.branchCollapsedEvent(), event -> {
            System.out.println("cool");
        });
    }


    public MyTreeItem(Path path) {
        super(path.toString());
        this.fullPath = path.toString();
        if (Files.isDirectory(path)) {
            this.isDirectory = true;
            ImageView iv = new ImageView(folderClosedImage);
            iv.setFitWidth(16);
            iv.setFitHeight(16);
            this.setGraphic(iv);
            this.isEmpty = true;                //na poczatku zakladamy ze jest pusty
            this.getChildren().add(emptyTI);    //dodajemy jakis item zeby bylo mozna rozwinac element
        } else {
            this.isDirectory = false;
            ImageView iv = new ImageView(fileImage);
            iv.setFitWidth(16);
            iv.setFitHeight(16);
            this.setGraphic(iv);
        }
        if (!fullPath.endsWith(File.separator)) {
            String value = path.toString();
            int indexOf = value.lastIndexOf(File.separator);
            if (indexOf > 0) {
                this.setValue(value.substring(indexOf + 1));
            } else {
                this.setValue(value);
            }
        }
    }

        public String getFullPath(){
            return this.fullPath;
        }

        public boolean isDirectory() {
            return isDirectory;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        public void setEmpty(boolean empty) {
            isEmpty = empty;
        }

        public void setDirectory(boolean directory) {
            isDirectory = directory;
        }

        public TreeItem getEmptyTI() {
            return emptyTI;
        }

        public void setEmptyTI(TreeItem emptyTI) {
            this.emptyTI = emptyTI;
        }

}
