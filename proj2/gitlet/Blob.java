
package gitlet;
import java.io.*;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    private File filePath;
    private String id;
    private String content;

    public Blob(File current, File staged) {
        String text = readContentsAsString(staged);
        this.filePath = current;
        this.content = text;
        this.id = sha1(serialize(this));
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
