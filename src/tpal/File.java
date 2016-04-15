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
public class File {
    public final static Image folderClosedImage = new Image(ClassLoader.getSystemResourceAsStream("resources/close-folder.png"));
    public final static Image folderOpenedImage = new Image(ClassLoader.getSystemResourceAsStream("resources/open-folder.png"));
    public final static Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("resources/file.png"));

    private Boolean isDirectory;
    private ImageView image;
    private Path fullPath;
    private Long size;
    private FileTime creationDate;


    public File(Path path) {
        this.fullPath = path;

        if (Files.isDirectory(path)) {
            this.isDirectory = new Boolean(true);
            image = new ImageView(folderOpenedImage);
        } else {
            this.isDirectory = new Boolean(false);
            image = new ImageView(fileImage);
        }
        image.setFitWidth(16);
        image.setFitHeight(16);

        try {
            BasicFileAttributes attribs = Files.readAttributes(path, BasicFileAttributes.class);
            if (this.isDirectory()) this.size = attribs.size();
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
}
