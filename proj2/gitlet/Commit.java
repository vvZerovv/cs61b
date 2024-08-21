package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import static gitlet.Repository.GITLET_DIR;
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
    private Date timestamp;
    private String parent;
    //sha1 hash code of myself
    private String id;
    //the whole commit tree
    public static Tree<String> commitTree;
    //mapping file in Bolbs
    private ArrayList<String> filetree;

    public Commit(String message,  String parent, ArrayList<String> filetree) {
        this.message = message;
        this.parent = parent;
        if (parent == null) {
            timestamp = new Date(0);
            commitTree = new Tree();
        } else {
            timestamp = new Date();
        }
        this.filetree = filetree;
        String hashcontent = message + timestamp.toString() + parent;
        String hashcode = sha1(hashcontent);
        commitTree.addtomain(hashcode);
        this.id = hashcode;
    }

    //store the commit in the COMMITS_DIR
    public void store() {
        String hashcontent = message + timestamp.toString() + parent;
        String hashcode = sha1(hashcontent);
        File file = join(COMMITS_DIR, hashcode);
        writeObject(file,this);
    }

    //return the current commit
    public static Commit getlast() {
        String parentid = commitTree.getlast();
        File file = join(COMMITS_DIR, parentid);
        Commit parent = readObject(file, Commit.class);
        return parent;
    }

    //get the filetree
    public ArrayList<String> getfiletree() {
        return filetree;
    }

    public String getId() {
        return id;
    }

}
