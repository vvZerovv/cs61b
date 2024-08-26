package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gitlet.Repository.*;
import static gitlet.Utils.join;
import static gitlet.Utils.*;


/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author vv
 */
public class Commit implements Serializable {
    /**List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /** The message of this Commit. */
    private String message;
    /** The date of this Commit. */
    private Date timestamp;
    /** The parent id of this Commit. */
    private ArrayList<String> parent;
    /** The sha1 code of this Commit. */
    private String id;
    /** The blobs id of this Commit. */
    private ArrayList<String> filetree;
    /** the branch of the commit */
    private String branch;

    public Commit(String message,  ArrayList<String> parent, ArrayList<String> filetree, String branch) {
        this.message = message;
        this.parent = parent;
        this.filetree = filetree;
        this.branch = branch;
        if (parent == null) {
            timestamp = new Date(0);
        } else {
            Date date = new Date();
            timestamp = new Date(date.getTime());
        }
        this.id = sha1(serialize(this));
        //update head pointer
        Commit HEAD = this;
        writeObject(POINTER_HEAD,HEAD);
    }

    //store the commit in the COMMITS_DIR
    public void store() {
        File file = join(COMMITS_DIR, this.id);
        writeObject(file,this);
    }


    //get the filetree
    public ArrayList<String> getfiletree() {
        return filetree;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getparent() {
        return parent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getBranch() {
        return branch;
    }

    public void changeBranch(String branch) {
        this.branch = branch;
    }
}
