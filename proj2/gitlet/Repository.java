package gitlet;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

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
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    /** The file in staging area for addition*/
    public static final File ADD_LIST = join(STAGING_DIR, "addlist");
    /** The file in staging area for removal*/
    public static final File REMOVE_LIST = join(STAGING_DIR, "removelist");
    /** The map between blob id and the blob*/
    public static final File HASH_MAP = join(GITLET_DIR, "hashmap");
    /** The branches file*/
    public static final File BRANCHES_DIR = join(GITLET_DIR, "Pointers");
    /** The HEAD Pointer */
    public static final File POINTER_HEAD = join(GITLET_DIR, "headPointer");
    /** The master branch Pointer*/
    public static final File BRANCH_MASTER = join(BRANCHES_DIR, "master");
    /** The head branch */
    public static final File BRANCH= join(GITLET_DIR, "currentBranch");


    public static void initCommand(){
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        } else {
            GITLET_DIR.mkdirs();
            COMMITS_DIR.mkdirs();
            STAGING_DIR.mkdirs();
            BRANCHES_DIR.mkdirs();
            ArrayList<String> list1 = new ArrayList<>();
            ArrayList<String> list2 = new ArrayList<>();
            writeObject(REMOVE_LIST, list2);
            writeObject(ADD_LIST, list1);
            HashMap<String, Blob> blobs = new HashMap<>();
            writeObject(HASH_MAP, blobs);
            Commit firstcommit = new Commit("initial commit",null, new ArrayList<String>());
            firstcommit.store();
            writeContents(BRANCH, "master");
            writePointers(firstcommit);
        }
    }


    public static void addCommand(String filename){
        File current= join(CWD, filename);
        File staged = join(STAGING_DIR, filename);
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
        HashMap<File, String> trackedPath = getPath();
        Commit current = getLastCommit();
        ArrayList<String> parentsShot = current.getfiletree();
        String parentid = current.getId();
        HashMap<String,Blob> blobs = getBlobs();
        for (String file : newShot) {
            File stagedfile = join(STAGING_DIR, file);
            File currentfile = join(CWD, file);
            Blob blob = new Blob(currentfile, stagedfile);
            if (trackedPath.containsKey(currentfile)) {
                String blobId = trackedPath.get(currentfile);
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
            if (trackedPath.containsKey(currentfile)) {
                String blobId = trackedPath.get(currentfile);
                parentsShot.remove(blobId);
            }
        }
        writeObject(ADD_LIST, new ArrayList<>());
        writeObject(REMOVE_LIST, new ArrayList<>());
        Commit newcommit = new Commit(mes, parentid, parentsShot);
        //the new commit is added as a new node in the commit tree.
        newcommit.store();
        writePointers(newcommit);
    }



    public static void rmCommand(String filename){
        File staged = join(STAGING_DIR, filename);
        if (staged.exists()) {
            staged.delete();
        }
        HashMap<File, String> files = getPath();
        File deletefile = join(CWD, filename);
        if (files.containsKey(deletefile) && deletefile.exists()) {
            deletefile.delete();
            ArrayList<String> deleteFiles = getRemoveList();
            deleteFiles.add(filename);
            writeObject(REMOVE_LIST, deleteFiles);
        }
        if (!staged.exists() && !files.containsKey(deletefile)) {
            System.out.println("No reason to remove the file.");
        }
    }



    //TODO:For merge commits??? (those that have two parent commits), add a line just below the first
    public static void logCommand() {
        Commit head = getLastCommit();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-08:00"));
        while (head!=null) {
            System.out.println("===");
            System.out.println("commit "+head.getId());
            System.out.println("Date: " + sdf.format(head.getTimestamp()));
            System.out.println(head.getMessage());
            System.out.println();
            head = getCommit(head.getparent());
        }
    }



    public static void globallogCommand(){
        List<String> files = plainFilenamesIn(COMMITS_DIR);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-08:00"));
        for (String file : files) {
            File commitFile = join(COMMITS_DIR, file);
            Commit commit = readObject(commitFile, Commit.class);
            System.out.println("===");
            System.out.println("commit "+commit.getId());
            System.out.println("Date: "+sdf.format(commit.getTimestamp()));
            System.out.println(commit.getMessage());
            System.out.println();
        }
    }



    public static void findCommand(String message){
        List<String> files = plainFilenamesIn(COMMITS_DIR);
        int size = files.size();
        int num = 0;
        for (String file : files) {
            File commitFile = join(COMMITS_DIR, file);
            Commit commit = readObject(commitFile, Commit.class);
            String mes = commit.getMessage();
            if (mes.equals(message)) {
                System.out.println("commit "+commit.getId());
                num += 1;
            }
        }
        if (num == 0) {
            System.out.println("Found no commit with that message.");
        }
    }



    //TODO：Untracked Files and Modifications Not Staged For Commit
    public static void statusCommand(){
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
        String name = readContentsAsString(BRANCH);
        System.out.println("=== Branches ===");
        for (String file : branches) {
            if (name.equals(file)) {
                System.out.println("*"+file);
            } else {
                System.out.println("other"+file);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        ArrayList<String> addFiles = getAddList();
        for (String file : addFiles) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        ArrayList<String> removeFiles = getRemoveList();
        for (String file : removeFiles) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }


    public static void checkoutOne(String filename){
        Commit lastcommit = getLastCommit();
        checkoutTwo(lastcommit.getId(), filename);
    }


    public static void checkoutTwo(String CommitId, String filename){
        File commitFile = join(COMMITS_DIR, CommitId);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        HashMap<File,String> trackedPath = getPath(CommitId);
        File currentfile = join(CWD, filename);
        if (trackedPath.containsKey(currentfile)) {
            if (currentfile.exists()) {
                currentfile.delete();
            }
            String blobId = trackedPath.get(currentfile);
            HashMap<String,Blob> blobs = getBlobs();
            Blob blob = blobs.get(blobId);
            writeContents(currentfile,blob.getContent());
            ArrayList<String> removeFiles = getRemoveList();
            ArrayList<String> addFiles = getAddList();
            if (removeFiles.contains(filename)) {
                removeFiles.remove(filename);
                writeObject(REMOVE_LIST, removeFiles);
            }
            if (addFiles.contains(filename)) {
                addFiles.remove(filename);
                writeObject(ADD_LIST, addFiles);
            }
        } else {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

    }

    public static void checkoutThree(String branch){
        File file = join(BRANCHES_DIR, branch);
        if (!file.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(BRANCH);
        if (currentBranch.equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        HashMap<File,String> trackedPath = getPath();
        List<String> files = plainFilenamesIn(CWD);
        for (String name : files) {
            File cwdfile  = join(CWD, name);
            if (!trackedPath.containsKey(cwdfile)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        File branchFile = join(BRANCHES_DIR, branch);
        Commit commit = readObject(branchFile, Commit.class);
        ArrayList<String> commitFiles = commit.getfiletree();
        HashMap<String, Blob> blobs = getBlobs();
        for (File trackedFile : trackedPath.keySet()) {
            trackedFile.delete();
        }
        for (String blobid : commitFiles) {
            Blob blob = blobs.get(blobid);
            writeContents(blob.getFilePath(),blob.getContent());
        }
        writeContents(BRANCH, branch);
        writeObject(BRANCH, commit);
    }

    //return the map of file dir and blob id  of the current commit
    public static HashMap<File, String> getPath() {
        Commit head = getLastCommit();
        return getPath(head.getId());
    }

    public static HashMap<File, String> getPath(String commitId) {
        HashMap<File, String> map = new HashMap<>();
        Commit commit = getCommit(commitId);
        ArrayList<String> blobid = commit.getfiletree();
        HashMap<String, Blob> blobs = getBlobs();
        for (String id : blobid ) {
            Blob blob = blobs.get(id);
            File path = blob.getFilePath();
            map.put(path, id);
        }
        return map;
    }

    public static ArrayList<String> getCommitFileTree() {
        Commit lastcommit = getLastCommit();
        return  lastcommit.getfiletree();
    }


    public static Commit getCommit(String id) {
        if (id == null) {
            return null;
        }
        File file = join(COMMITS_DIR, id);
       return readObject(file, Commit.class);
    }

    //return the current commit
    public static Commit getLastCommit() {
        return readObject(POINTER_HEAD, Commit.class);
    }

    public static ArrayList<String> getAddList() {
        return readObject(ADD_LIST, ArrayList.class);
    }

    private static HashMap<String,Blob> getBlobs(){
        return readObject(HASH_MAP, HashMap.class);
    }

    private static ArrayList<String> getRemoveList() {
        return readObject(REMOVE_LIST, ArrayList.class);
    }

    // update the map of blob id and blob and serialize it
    private static void updatemap(String s, Blob b) {
        HashMap<String,Blob> blobs = getBlobs();
        blobs.put(s, b);
        writeObject(HASH_MAP, blobs);
    }


    /** update the head pointer and the branch pointer*/
    public static void writePointers(Commit commit) {
        String branch = readContentsAsString(BRANCH);
        writeObject(POINTER_HEAD, commit);
        File file = join(BRANCHES_DIR, branch);
        writeObject(file, commit);
    }

}
