package gitlet;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.*;

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
    public static final File BRANCH = join(GITLET_DIR, "currentBranch");
    /** the tracked file of a branch */
    public static final File TRACKEDFILE = join(GITLET_DIR, "trackedfile");


    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists "
                    + "in the current directory.");
            System.exit(0);
        } else {
            GITLET_DIR.mkdirs();
            COMMITS_DIR.mkdirs();
            STAGING_DIR.mkdirs();
            BRANCHES_DIR.mkdirs();
            TRACKEDFILE.mkdirs();
            ArrayList<String> list1 = new ArrayList<>();
            ArrayList<String> list2 = new ArrayList<>();
            ArrayList<String> list3 = new ArrayList<>();
            writeObject(REMOVE_LIST, list2);
            writeObject(ADD_LIST, list1);
            HashMap<String, Blob> blobs = new HashMap<>();
            writeObject(HASH_MAP, blobs);
            Commit firstcommit = new Commit("initial commit", new ArrayList<>(),
                    new ArrayList<String>(), "master");
            firstcommit.store();
            writeContents(BRANCH, "master");
            writePointers(firstcommit);
            File file = join(TRACKEDFILE, "master");
            writeObject(file, list3);
        }
    }
    public static void addCommand(String filename) {
        checkDelete();
        File current = join(CWD, filename);
        File staged = join(STAGING_DIR, filename);
        if (!current.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        ArrayList<String> removeList = getRemoveList();
        if (removeList.contains(filename)) {
            removeList.remove(filename);
            writeObject(REMOVE_LIST, removeList);
        }
        String contentNow = readContentsAsString(current);
        ArrayList<String> blobs = getCommitFileTree();
        Blob blob = new Blob(current, current);
        if (blobs.contains(blob.getId())) {
            if (staged.exists()) {
                staged.delete();
                ArrayList<String> obj = getAddList();
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




    public static void commitCommand(String mes) {
        checkDelete();
        ArrayList<String> newShot = getAddList();
        ArrayList<String> deleteFile = getRemoveList();
        if (newShot.isEmpty() && deleteFile.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (mes.isEmpty()) {
            System.out.println("Please enter a commit message.");
        }
        HashMap<File, String> trackedPath = getPath();
        Commit current = getLastCommit();
        ArrayList<String> parentsShot = current.getfiletree();
        String parentid = current.getId();
        ArrayList<String> parent = new ArrayList<>();
        parent.add(parentid);
        String currentBranch = readContentsAsString(BRANCH);
        File file2 = join(TRACKEDFILE, currentBranch);
        ArrayList<String> trackedfile = readObject(file2, ArrayList.class);
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
            if (!trackedfile.contains(file)) {
                trackedfile.add(file);
            }
        }
        writeObject(file2, trackedfile);
        for (String file : deleteFile) {
            File currentfile = join(CWD, file);
            if (trackedPath.containsKey(currentfile)) {
                String blobId = trackedPath.get(currentfile);
                parentsShot.remove(blobId);
            }
        }
        writeObject(ADD_LIST, new ArrayList<>());
        writeObject(REMOVE_LIST, new ArrayList<>());
        String branch = readContentsAsString(BRANCH);
        Commit newcommit = new Commit(mes, parent, parentsShot, branch);
        writePointers(newcommit);
        newcommit.store();
    }



    public static void rmCommand(String filename) {
        File staged = join(STAGING_DIR, filename);
        HashMap<File, String> files = getPath();
        File deletefile = join(CWD, filename);
        if (!staged.exists() && !files.containsKey(deletefile)) {
            System.out.println("No reason to remove the file.");
        }
        if (staged.exists()) {
            staged.delete();
            ArrayList<String> addList = getAddList();
            addList.remove(filename);
            writeObject(ADD_LIST, addList);
        }
        if (files.containsKey(deletefile) && deletefile.exists()) {
            deletefile.delete();
            ArrayList<String> deleteFiles = getRemoveList();
            deleteFiles.add(filename);
            writeObject(REMOVE_LIST, deleteFiles);
        }
    }



    public static void logCommand() {
        Commit head = getLastCommit();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-08:00"));
        while (head != null) {
            ArrayList<String> parent = head.getparent();
            System.out.println("===");
            System.out.println("commit " + head.getId());
            if (!parent.isEmpty()) {
                if (parent.size() == 2) {
                    System.out.println("Merge: " + parent.get(0).substring(0, 7)
                            + " " + parent.get(1).substring(0, 7));
                }
            }
            System.out.println("Date: " + sdf.format(head.getTimestamp()));
            System.out.println(head.getMessage());
            System.out.println();
            if (parent.isEmpty()) {
                break;
            }
            head = getCommit(parent.get(0));
        }
    }


    public static void globallogCommand() {
        List<String> files = plainFilenamesIn(COMMITS_DIR);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-08:00"));
        for (String file : files) {
            File commitFile = join(COMMITS_DIR, file);
            Commit commit = readObject(commitFile, Commit.class);
            System.out.println("===");
            System.out.println("commit " + commit.getId());
            System.out.println("Date: " + sdf.format(commit.getTimestamp()));
            System.out.println(commit.getMessage());
            System.out.println();
        }
    }



    public static void findCommand(String message) {
        List<String> files = plainFilenamesIn(COMMITS_DIR);
        int size = files.size();
        int num = 0;
        for (String file : files) {
            File commitFile = join(COMMITS_DIR, file);
            Commit commit = readObject(commitFile, Commit.class);
            String mes = commit.getMessage();
            if (mes.equals(message)) {
                System.out.println(commit.getId());
                num += 1;
            }
        }
        if (num == 0) {
            System.out.println("Found no commit with that message.");
        }
    }




    public static void statusCommand() {
        checkDelete();
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
        String name = readContentsAsString(BRANCH);
        System.out.println("=== Branches ===");
        for (String file : branches) {
            if (name.equals(file)) {
                System.out.println("*" + file);
            } else {
                System.out.println(file);
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


    public static void checkoutOne(String filename) {
        Commit lastcommit = getLastCommit();
        checkoutTwo(lastcommit.getId(), filename);
    }


    public static void checkoutTwo(String commitId, String filename) {
        File commitFile = join(COMMITS_DIR, commitId);
        if (commitId.length() == 8) {
            List<String> files = plainFilenamesIn(COMMITS_DIR);
            for (String file : files) {
                if (file.startsWith(commitId)) {
                    checkoutTwo(file, filename);
                    System.exit(0);
                }
            }
        }
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        HashMap<File, String> trackedPath = getPath(commitId);
        File currentfile = join(CWD, filename);
        if (trackedPath.containsKey(currentfile)) {
            if (currentfile.exists()) {
                currentfile.delete();
            }
            String blobId = trackedPath.get(currentfile);
            HashMap<String, Blob> blobs = getBlobs();
            Blob blob = blobs.get(blobId);
            writeContents(currentfile, blob.getContent());
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

    public static void checkoutThree(String branch) {
        File file = join(BRANCHES_DIR, branch);
        if (!file.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        HashMap<File, String> trackedPath = getPath();
        List<String> files = plainFilenamesIn(CWD);
        File branchFile = join(BRANCHES_DIR, branch);
        Commit commit = readObject(branchFile, Commit.class);
        HashMap<File, String> trackedChange = getPath(commit.getId());
        for (String name : files) {
            File cwdfile  = join(CWD, name);
            if (!trackedPath.containsKey(cwdfile) && trackedChange.containsKey(cwdfile)) {
                System.out.println("There is an untracked file in the way; delete it, "
                        + "or add and commit it first.");
                System.exit(0);
            }
        }
        ArrayList<String> commitFiles = commit.getfiletree();
        HashMap<String, Blob> blobs = getBlobs();
        for (File trackedFile : trackedPath.keySet()) {
            trackedFile.delete();
        }
        for (String blobid : commitFiles) {
            Blob blob = blobs.get(blobid);
            writeContents(blob.getFilePath(), blob.getContent());
        }
        writeContents(BRANCH, branch);
        writeObject(POINTER_HEAD, commit);
        cleanStaging();
    }



    public static void branchCommand(String branch) {
        File file = join(BRANCHES_DIR, branch);
        if (file.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        File file2 = join(TRACKEDFILE, branch);
        writeObject(file2, new ArrayList<String>());
        Commit commit = readObject(POINTER_HEAD, Commit.class);
        writeObject(file, commit);
    }


    public static void rmbranchCommand(String branch) {
        File file = join(BRANCHES_DIR, branch);
        if (!file.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(BRANCH);
        if (currentBranch.equals(branch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        file.delete();
    }

    public static void resetCommand(String commitId) {
        File fileC = join(COMMITS_DIR, commitId);
        if (!fileC.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit commit = readObject(fileC, Commit.class);
        String branch = commit.getBranch();
        String currentBranchreally = readContentsAsString(BRANCH);
        Commit currentCommit = getLastCommit();
        String currentBranch = currentCommit.getBranch();
        if (!currentBranchreally.equals(currentBranch)) {
            currentBranch = currentBranchreally;
        }
        List<String> files = plainFilenamesIn(CWD);
        File file2 = join(TRACKEDFILE, currentBranch);
        ArrayList<String> trackedPath = readObject(file2, ArrayList.class);
        HashMap<File, String> trackedChange = getPath(commitId);
        if (files != null) {
            for (String name : files) {
                File cwdfile = join(CWD, name);
                if (!trackedPath.contains(name) && trackedChange.containsKey(cwdfile)) {
                    System.out.println("There is an untracked file in the way; delete it, "
                            + "or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        ArrayList<String> commitFiles = commit.getfiletree();
        HashMap<String, Blob> blobs = getBlobs();
        for (String trackedFile : trackedPath) {
            File file4 = join(CWD, trackedFile);
            file4.delete();
        }
        for (String blobid : commitFiles) {
            Blob blob = blobs.get(blobid);
            writeContents(blob.getFilePath(), blob.getContent());
        }
        writeObject(POINTER_HEAD, commit);
        File branchfile  = join(BRANCHES_DIR, branch);
        writeObject(branchfile, commit);
        cleanStaging();
    }



    public static void mergeCommand(String branch) {
        check(branch);
        File file = join(BRANCHES_DIR, branch);
        Commit branchCommit = readObject(file, Commit.class);
        Commit headCommit = getLastCommit();
        Commit splitCommit = getSplit(branchCommit, headCommit);
        HashMap<File, String> branchPath = getPath(branchCommit.getId());
        HashMap<File, String> headPath = getPath(headCommit.getId());
        HashMap<File, String> splitPath = getPath(splitCommit.getId());
        String currentBranch = readContentsAsString(BRANCH);
        File file3 = join(TRACKEDFILE, currentBranch);
        ArrayList<String> trackedfile = readObject(file3, ArrayList.class);
        Set<File> allFiles = new HashSet<>();
        allFiles.addAll(headPath.keySet());
        allFiles.addAll(splitPath.keySet());
        allFiles.addAll(branchPath.keySet());
        ArrayList<String> filetree = headCommit.getfiletree();
        HashMap<String, Blob> blobs = getBlobs();
        for (File name : allFiles) {
            mergehelper(name, branchPath, headPath, splitPath, filetree, blobs, trackedfile);
        }
        writeObject(file3, trackedfile);
        writeObject(HASH_MAP, blobs);
        String mess = "Merged " + branch + " into " + currentBranch + ".";
        String s = headCommit.getId();
        String b = branchCommit.getId();
        ArrayList<String> list3 = new ArrayList<>();
        list3.add(s);
        list3.add(b);
        Commit newcommit = new Commit(mess, list3, filetree, currentBranch);
        writePointers(newcommit);
        newcommit.store();
    }


    private static void mergehelper(File name, HashMap<File, String> branchPath, HashMap
            <File, String> headPath, HashMap<File, String> splitPath, ArrayList
            <String> filetree, HashMap<String, Blob> blobs, ArrayList<String> trackedfile) {
        if (splitPath.containsKey(name) && branchPath.containsKey(name)
                && headPath.containsKey(name)) {
            if (!splitPath.get(name).equals(branchPath.get(name))
                    && splitPath.get(name).equals(headPath.get(name))) {
                filetree.remove(headPath.get(name));
                filetree.add(branchPath.get(name));
                writeContents(name, blobs.get(branchPath.get(name)).getContent());
            }
            if (!splitPath.get(name).equals(branchPath.get(name))
                    && !branchPath.get(name).equals(headPath.get(name))
                    && !splitPath.get(name).equals(headPath.get(name))) {
                System.out.println("Encountered a merge conflict.");
                Blob blobOfHead = blobs.get(headPath.get(name));
                Blob blobOfBranch = blobs.get(branchPath.get(name));
                String contents = "<<<<<<< HEAD" + "\n"
                        + blobOfHead.getContent()
                        + "=======" + "\n"
                        + blobOfBranch.getContent()
                        + ">>>>>>>" + "\n";
                writeContents(name, contents);
                Blob newBlob = new Blob(name, name);
                filetree.add(newBlob.getId());
                blobs.put(newBlob.getId(), newBlob);
            }
        }
        if (!splitPath.containsKey(name) && branchPath.containsKey(name)
                && !headPath.containsKey(name)) {
            filetree.remove(headPath.get(name));
            filetree.add(branchPath.get(name));
            writeContents(name, blobs.get(branchPath.get(name)).getContent());
            String originalString = name.toString();
            String prefixToRemove = CWD.toString() + "\\";
            String result = originalString.substring(prefixToRemove.length());
            if (!trackedfile.contains(result)) {
                trackedfile.add(result);
            }
        }
        if (splitPath.containsKey(name) && !branchPath.containsKey(name)
                && headPath.containsKey(name)) {
            if (splitPath.get(name).equals(headPath.get(name))) {
                filetree.remove(headPath.get(name));
                name.delete();
            }
        }
        if (splitPath.containsKey(name) && branchPath.containsKey(name)
                && !headPath.containsKey(name)) {
            if (!splitPath.get(name).equals(branchPath.get(name))) {
                Blob blobOfBranch = blobs.get(branchPath.get(name));
                System.out.println("Encountered a merge conflict.");
                String contents = "<<<<<<< HEAD" + "\n" + "" + "=======" + "\n"
                        + blobOfBranch.getContent() + ">>>>>>>" + "\n";
                writeContents(name, contents);
                Blob newBlob = new Blob(name, name);
                filetree.add(newBlob.getId());
                blobs.put(newBlob.getId(), newBlob);
                String originalString = name.toString();
                String prefixToRemove = CWD.toString() + "\\";
                String result = originalString.substring(prefixToRemove.length());
                if (!trackedfile.contains(result)) {
                    trackedfile.add(result);
                }
            }
        }
        if (splitPath.containsKey(name) && !branchPath.containsKey(name)
                && headPath.containsKey(name)) {
            if (!splitPath.get(name).equals(headPath.get(name))) {
                Blob blobOfHead = blobs.get(headPath.get(name));
                System.out.println("Encountered a merge conflict.");
                String contents = "<<<<<<< HEAD" + "\n" + blobOfHead.getContent()
                        + "=======" + "\n" + "" + ">>>>>>>" + "\n";
                writeContents(name, contents);
                Blob newBlob = new Blob(name, name);
                filetree.add(newBlob.getId());
                blobs.put(newBlob.getId(), newBlob);
            }
        }
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
        for (String id : blobid) {
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

    private static HashMap<String, Blob> getBlobs() {
        return readObject(HASH_MAP, HashMap.class);
    }

    private static ArrayList<String> getRemoveList() {
        return readObject(REMOVE_LIST, ArrayList.class);
    }

    // update the map of blob id and blob and serialize it
    private static void updatemap(String s, Blob b) {
        HashMap<String, Blob> blobs = getBlobs();
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

    private static void checkDelete() {
        HashMap<File, String> trackedPath = getPath();
        for (File file : trackedPath.keySet()) {
            if (!file.exists()) {
                ArrayList<String> list = readObject(REMOVE_LIST, ArrayList.class);
                String originalString = file.toString();
                String prefixToRemove = CWD.toString() + "\\";
                String result = originalString.substring(prefixToRemove.length());
                if (!list.contains(result)) {
                    list.add(result);
                    writeObject(REMOVE_LIST, list);
                }
            }
        }
    }

    private static void cleanStaging() {
        writeObject(ADD_LIST, new ArrayList<>());
        writeObject(REMOVE_LIST, new ArrayList<>());
    }

    private static Commit getSplit(Commit commit, Commit head) {
        ArrayList<String> list = getCommits(commit);
        ArrayList<String> list2 = getCommits(head);
        for (String com : list) {
            if (list2.contains(com)) {
                return getCommit(com);
            }
        }
        return null;
    }

    private static ArrayList<String> getCommits(Commit commit) {
        ArrayList<String> commits = new ArrayList<>();
        while (commit != null) {
            commits.add(commit.getId());
            if (commit.getparent().size() == 2) {
                commits.add(commit.getparent().get(1));
            }
            if (commit.getparent().isEmpty()) {
                break;
            }
            commit = getCommit(commit.getparent().get(0));
        }
        return commits;
    }

    public static void checkgitlet() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void check(String branch) {
        File file = join(BRANCHES_DIR, branch);
        if (!file.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        Commit branchCommit = readObject(file, Commit.class);
        Commit headCommit = getLastCommit();
        Commit splitCommit = getSplit(branchCommit, headCommit);
        String currentBranch = readContentsAsString(BRANCH);
        if (currentBranch.equals(branch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        if (splitCommit.getId().equals(branchCommit.getId())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitCommit.getId().equals(headCommit.getId())) {
            checkoutThree(branch);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        ArrayList<String> addList = getAddList();
        ArrayList<String> removeList = getRemoveList();
        if (!addList.isEmpty() || !removeList.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        HashMap<File, String> branchPath = getPath(branchCommit.getId());
        HashMap<File, String> headPath = getPath(headCommit.getId());
        HashMap<File, String> splitPath = getPath(splitCommit.getId());
        File file3 = join(TRACKEDFILE, currentBranch);
        List<String> files = plainFilenamesIn(CWD);
        for (String name : files) {
            File cwdfile = join(CWD, name);
            if (!headPath.containsKey(cwdfile) && (branchPath.containsKey(cwdfile)
                    || splitPath.containsKey(cwdfile))) {
                System.out.println("There is an untracked file in the way; delete it, "
                        + "or add and commit it first.");
                System.exit(0);
            }
        }
    }
}
