
package gitlet;
import java.io.*;
import java.util.*;
import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.join;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    private File filePath;
    private String id;
    private String content;

    public Blob(File current, File staged) {
        String text = readContentsAsString(staged);
        this.filePath = current;
        this.content = text;
        this.id = sha1(content);
    }

    public File getFilePath() {
        return filePath;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}
