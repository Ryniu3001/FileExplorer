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

    private String name;
    private boolean isDirectory;
    private ImageView image;
    private Path fullPath;
    private long size;
    private FileTime creationDate;


    public File(Path path) {
        this.fullPath = path;

        try {
            BasicFileAttributes attribs = Files.readAttributes(path, BasicFileAttributes.class);
            this.size = attribs.size();
            this.creationDate = attribs.creationTime();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Nie można odczytać atrybutów");
        }

        //jeśli ścieżka to tylko nazwa dysku to wyswietlamy cala sciezke
        if (path.getNameCount() > 1)
            this.name = path.getFileName().toString();
        else
            this.name = path.toString();

        if (Files.isDirectory(path)) {
            this.isDirectory = true;
            image = new ImageView(folderOpenedImage);
        } else {
            this.isDirectory = false;
            image = new ImageView(fileImage);
        }
        image.setFitWidth(16);
        image.setFitHeight(16);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getFullPath() {
        return fullPath;
    }

    public void setFullPath(Path fullPath) {
        this.fullPath = fullPath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(FileTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
