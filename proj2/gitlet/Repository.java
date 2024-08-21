package gitlet;

import java.io.File;
import java.util.ArrayList;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author vv
 */
public class Repository {
    /**
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The staging area. */
    public static final File STAGING_AREA = join(GITLET_DIR, "staging");
    /** The file in staging area */
    public static final File LIST_FILE = join(STAGING_AREA, "list");

    /**
     * Description: Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit: a commit that contains no files
     * and has the commit message initial commit (just like that, with no punctuation).
     * TODO It will have a single branch: master, which initially points to this initial commit and master will be the current branch.
     * The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1 January 1970
     * in whatever format you choose for dates (this is called “The (Unix) Epoch”, represented internally by the time 0.)
     * Since the initial commit in all repositories created by Gitlet will have exactly the same content, it follows that all
     * repositories will automatically share this commit (they will all have the same UID) and all commits in all repositories will
     * trace back to it.
     */
    public static void initCommand(){
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        } else {
            GITLET_DIR.mkdirs();
            Commit.COMMITS_DIR.mkdirs();
            STAGING_AREA.mkdirs();
            Blob.BLOBS_DIR.mkdir();
            Commit firstcommit = new Commit("initial commit",null, new ArrayList<String>());
            firstcommit.store();
        }
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area
     * (see the description of the commit command).
     * For this reason, adding a file is also called staging the file for addition.
     * Staging an already-staged file overwrites
     * the previous entry in the staging area with the new contents.
     * The staging area should be somewhere in .gitlet.
     * If the current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there
     * (as can happen when a file is changed, added, and then changed back to it’s original version).
     * The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
     */
    public static void addCommand(String filename){
        File file = join(CWD, filename);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        File staged = join(STAGING_AREA, filename);
        if (staged.exists()) {
            String contentnow = readContentsAsString(file);
            String nowhash = sha1(contentnow);
            String contentstaged = readContentsAsString(staged);
            String stagedhash = sha1(contentstaged);
            if (nowhash.equals(stagedhash)) {
                staged.delete();
                System.exit(0);
            }
            writeContents(staged, contentnow);
            ArrayList<String> obj = new ArrayList<>();
            obj.add(filename);
            writeObject(LIST_FILE, obj);
        }
    }

    /**Saves a snapshot of tracked files in the current commit and staging area
     * so they can be restored at a later time, creating a new commit.
     * The commit is said to be tracking the saved files.
     * By default, each commit’s snapshot of files will be exactly the same
     * as its parent commit’s snapshot of files; it will keep versions of files
     * exactly as they are, and not update them.
     * A commit will only update the contents of files it is tracking
     * that have been staged for addition at the time of commit,
     * in which case the commit will now include the version of the file
     * that was staged instead of the version it got from its parent.
     * A commit will save and start tracking any files that were staged for
     * addition but weren’t tracked by its parent. Finally, files tracked in
     * the current commit may be untracked in the new commit as a result being staged
     * TODO:for removal by the rm command (below).
     * The bottom line: By default a commit has the same
     * file contents as its parent. Files staged for addition
     * and removal are the updates to the commit. Of course, the date (and likely the mesage)
     * will also different from the parent.
     *
     */
    public static void commitCommand(String mes){
        ArrayList<String> newShot = readObject(LIST_FILE,ArrayList.class);
        Commit current = Commit.getlast();
        ArrayList<String> parentsShot = current.getfiletree();
        String parentid = current.getId();
        for(String file : newShot) {
            File fileinStaged = join(STAGING_AREA, file);
            File currentFile = join(CWD, file);
            String content = readContentsAsString(fileinStaged);
            if(parentsShot.contains(file)) {
                File parentfile = join(Blob.BLOBS_DIR, file);
            } else {
                parentsShot.add(sha1(content));
                Blob b = new Blob(currentFile);
            }
        }
        Commit newcommit = Commit(mes, parentid, );

    }
}
