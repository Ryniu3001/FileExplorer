package tpal;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * Created by Marcin on 14.04.2016.
 */
public class MyFile {
    public final static Image folderClosedImage = new Image(ClassLoader.getSystemResourceAsStream("resources/close-folder.png"));
    public final static Image folderOpenedImage = new Image(ClassLoader.getSystemResourceAsStream("resources/open-folder.png"));
    public final static Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("resources/file.png"));

    private Boolean isDirectory;
    private ImageView image;
    private Path fullPath;
    private String name;
    private Long size;
    private FileTime creationDate;


    public MyFile(Path path) {
        this.fullPath = path;
        this.name = getFileName(path);

        if (Files.isDirectory(path)) {
            this.isDirectory = Boolean.TRUE;
            image = new ImageView(folderOpenedImage);
        } else {
            this.isDirectory = Boolean.FALSE;
            image = new ImageView(fileImage);
        }
        image.setFitWidth(16);
        image.setFitHeight(16);

        try {
            BasicFileAttributes attribs = Files.readAttributes(path, BasicFileAttributes.class);
            if (!this.isDirectory()) this.size = attribs.size();
            this.creationDate = attribs.creationTime();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Nie można odczytać atrybutów");
        }

    }

    public Path getFullPath() {
        return fullPath;
    }

    public void setFullPath(Path fullPath) {
        this.fullPath = fullPath;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public FileTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(FileTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean isDirectory() {
        return isDirectory;
    }
    //getter for TableCell impelemntation
    public Boolean getIsDirectory() {
        return isDirectory;
    }

    public void setDirectory(Boolean directory) {
        isDirectory = directory;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Zwraca nazwe folderu (ostatni czlon ścieżki)
     * @param path
     * @return
     */
    public String getFileName(Path path){
        if (path.getNameCount() > 0)
            return(path.getFileName().toString());
        else
            return(path.toString());
    }
}
