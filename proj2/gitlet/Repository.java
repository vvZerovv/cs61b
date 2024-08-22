package gitlet;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static gitlet.Commit.COMMITS_DIR;
import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author vv
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The staging area. */
    public static final File STAGING_AREA = join(GITLET_DIR, "staging");
    /** The file in staging area for addition*/
    public static final File ADD_LIST = join(STAGING_AREA, "addlist");
    /** The file in staging area for removal*/
    public static final File REMOVE_LIST = join(STAGING_AREA, "removelist");
    /** The map between blobs and  current working directory*/
    public static final File HASH_DIR = join(GITLET_DIR, "hashmap");
    /** The commit tree*/
    public static final File COMMITS_TREE = join(GITLET_DIR, "commitTree");


    //TODO:. It will have a single branch: master, which initially points to this initial commit, and master will be the current branch.
    public static void initCommand(){
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        } else {
            GITLET_DIR.mkdirs();
            COMMITS_DIR.mkdirs();
            STAGING_AREA.mkdirs();
            ArrayList<String> list1 = new ArrayList<>();
            ArrayList<String> list2 = new ArrayList<>();
            writeObject(REMOVE_LIST, list2);
            writeObject(ADD_LIST, list1);
            HashMap<String, Blob> blobs = new HashMap<>();
            writeObject(HASH_DIR, blobs);
            Commit firstcommit = new Commit("initial commit",null, new ArrayList<String>());
            Tree<String> tree = new Tree<>();
            String id = firstcommit.getId();
            tree.addtomain(id);
            firstcommit.store();
            writeObject(COMMITS_TREE, tree);
        }
    }


    public static void addCommand(String filename){
        File current= join(CWD, filename);
        File staged = join(STAGING_AREA, filename);
        if (!current.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String contentNow= readContentsAsString(current);
        ArrayList<String> blobs = getCommitFileTree();
        Blob blob = new Blob(current, current);
        if (blobs.contains(blob.getId())) {
            if (staged.exists()){
                staged.delete();
                ArrayList<String> obj =getAddList();
                obj.remove(filename);
                writeObject(ADD_LIST, obj);
            }
            System.exit(0);
        }
        writeContents(staged, contentNow);
        ArrayList<String> obj = getAddList();
        obj.add(filename);
        writeObject(ADD_LIST, obj);
    }



    //TODO:delete
    public static void commitCommand(String mes){
        ArrayList<String> newShot = getAddList();
        ArrayList<String> deleteFile = getRemoveList();
        if (newShot.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        HashMap<File, String> currentpath = getPath();
        Commit current = getLastCommit();
        ArrayList<String> parentsShot = current.getfiletree();
        String parentid = current.getId();
        HashMap<String,Blob> blobs = getBlobs();
        for (String file : newShot) {
            File stagedfile = join(STAGING_AREA, file);
            File currentfile = join(CWD, file);
            Blob blob = new Blob(currentfile, stagedfile);
            if (currentpath.containsKey(currentfile)) {
                String blobId = currentpath.get(currentfile);
                parentsShot.remove(blobId);
                parentsShot.add(blob.getId());
                updatemap(blob.getId(), blob);
            } else {
                parentsShot.add(blob.getId());
                updatemap(blob.getId(), blob);
            }
            stagedfile.delete();
        }
        for (String file : deleteFile) {
            File currentfile = join(CWD, file);
            if (currentpath.containsKey(currentfile)) {
                String blobId = currentpath.get(currentfile);
                parentsShot.remove(blobId);
            }
        }
        writeObject(ADD_LIST, new ArrayList<>());
        writeObject(REMOVE_LIST, new ArrayList<>());
        Commit newcommit = new Commit(mes, parentid, parentsShot);
        //the new commit is added as a new node in the commit tree.
        newcommit.updateCommitTree();
        newcommit.store();
    }


    public static void rmCommand(String filename){
        File staged = join(STAGING_AREA, filename);
        if (staged.exists()) {
            staged.delete();
        }
        HashMap<File, String> files = getPath();
        File deletefile = join(CWD, filename);
        if (files.containsKey(deletefile) && deletefile.exists()) {
            deletefile.delete();
            ArrayList<String> deletefiles = getRemoveList();
            deletefiles.add(filename);
            writeObject(REMOVE_LIST, deletefiles);
        }
    }

    //return the map of file dir and blob id  of the current commit
    public static HashMap<File, String> getPath() {
        HashMap<File, String> map = new HashMap<>();
        Commit lastcommit = getLastCommit();
        ArrayList<String> blobid = lastcommit.getfiletree();
        HashMap<String, Blob> blobs = getBlobs();
        for (String id : blobid ) {
            Blob blob = blobs.get(id);
            File path = blob.getFilePath();
            map.put(path, id);
        }
        return map;
    }

    //return the current commit trace file
    public static ArrayList<File> getCommitTrackFile(){
        Commit lastcommit = getLastCommit();
        ArrayList<String> filetree = lastcommit.getfiletree();
        ArrayList<File> files = new ArrayList<>();
        for (String file : filetree) {
            File currentfile = join(CWD, file);
            files.add(currentfile);
        }
        return files;
    }

    public static ArrayList<String> getCommitFileTree() {
        Commit lastcommit = getLastCommit();
        ArrayList<String> filetree = lastcommit.getfiletree();
        return filetree;
    }

    public static Tree<String> getCommitTree(){
        return readObject(COMMITS_TREE, Tree.class);
    }

    public static Commit getCommit(String id) {
        File file = join(COMMITS_DIR, id);
        Commit commit = readObject(file, Commit.class);
        return commit;
    }

    //return the current commit
    public static Commit getLastCommit() {
        Tree<String> tree = getCommitTree();
        String id = tree.getlast();
        Commit commit = getCommit(id);
        return commit;
    }

    public static ArrayList<String> getAddList() {
        return readObject(ADD_LIST, ArrayList.class);
    }

    private static HashMap<String,Blob> getBlobs(){
        return readObject(HASH_DIR, HashMap.class);
    }

    private static ArrayList<String> getRemoveList() {
        return readObject(REMOVE_LIST, ArrayList.class);
    }

    // update the map of blob id and blob and serialize it
    private static void updatemap(String s, Blob b) {
        HashMap<String,Blob> blobs = getBlobs();
        blobs.put(s, b);
        writeObject(HASH_DIR, blobs);
    }

}
