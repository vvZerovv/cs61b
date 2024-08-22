package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import static gitlet.Repository.COMMITS_TREE;
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
    /** The date of this Commit. */
    private Date timestamp;
    /** The parent id of this Commit. */
    private String parent;
    /** The sha1 code of this Commit. */
    private String id;
    /** The blobs id of this Commit. */
    private ArrayList<String> filetree;

    public Commit(String message,  String parent, ArrayList<String> filetree) {
        this.message = message;
        this.parent = parent;
        this.filetree = filetree;
        if (parent == null) {
            timestamp = new Date(0);
        } else {
            timestamp = new Date();
        }
        this.id = sha1(serialize(this));
    }

    //store the commit in the COMMITS_DIR
    public void store() {
        File file = join(COMMITS_DIR, this.id);
        writeObject(file,this);
    }

    //update the commitTree
    public void updateCommitTree() {
        Tree<String> commitTree = readObject(COMMITS_TREE,Tree.class);
        commitTree.addtomain(this.id);
        writeObject(COMMITS_TREE, commitTree);
    }



    //return the current commit
    public static Commit getlast() {
        Tree<String> commitTree = readObject(COMMITS_TREE,Tree.class);
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
