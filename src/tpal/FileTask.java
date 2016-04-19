package tpal;

import javafx.concurrent.Task;
import org.apache.commons.io.CopyProgressListener;

/**
 * Created by Marcin on 20.04.2016.
 */
public class FileTask extends Task implements CopyProgressListener {
    @Override
    protected Object call() throws Exception {
        return null;
    }

    @Override
    public void update(long l, boolean b) {

    }
}
