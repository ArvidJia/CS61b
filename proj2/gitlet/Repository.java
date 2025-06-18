package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Arvid Jia
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS = join(GITLET_DIR, "commits");
    public static final File ADDSTAGE = join(GITLET_DIR, "stage");
    public static final File RMSTAGE = join(GITLET_DIR, "rmstage");
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    private Blobs blobs;
    private Commits commits;
    private Commit head;
    //HashMap<FileName, HashCode>
    private HashMap<String, String> addStage = new HashMap<>();
    private HashMap<String, String> rmStage = new HashMap<>();
    private final HashMap<String, String> clearMap = new HashMap<>();

    public Repository() {
        blobs = new Blobs();
        commits = new Commits();
        head = commits.getHead();
    }

    public void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdirs();
            try {
                BLOBS.createNewFile();
                COMMITS.createNewFile();
                ADDSTAGE.createNewFile();
                RMSTAGE.createNewFile();
                HEAD.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeObject(HEAD, head);
            writeObject(BLOBS, blobs);
            writeObject(COMMITS, (Serializable) commits);
            writeObject(RMSTAGE, (Serializable) rmStage);
            writeObject(ADDSTAGE, (Serializable) addStage);
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public void add(String fileName) {
        File file = join(CWD, fileName);
        blobs = readObject(BLOBS, Blobs.class);
        addStage = readObject(ADDSTAGE, HashMap.class);
        Commit head = readObject(HEAD, Commit.class);
        String oldFileHash = head.find(fileName);
        String newFileHash = blobs.add(file);
        if (oldFileHash == null && newFileHash == null) {
            System.out.println("This file " + fileName + "is not changed");
            System.exit(0);
        }
        writeObject(BLOBS, blobs);
        addStage.put(fileName, newFileHash);
        writeObject(ADDSTAGE, addStage);
    }

    public void rm(String fileName) {
        addStage = readObject(ADDSTAGE, HashMap.class);
        Commit head = readObject(HEAD, Commit.class);
        String fileHash = addStage.remove(fileName);
        fileHash = fileHash == null ? head.find(fileHash) : fileHash;
        if (fileHash != null) {
            File rmFile = new File(GITLET_DIR, fileName);
            if (rmFile.exists()) {
                rmFile.delete();
            }
            rmStage = readObject(RMSTAGE, HashMap.class);
            rmStage.put(fileName, fileHash);
        }
    }

    public void commit(String message) {
        addStage = readObject(ADDSTAGE, HashMap.class);
        rmStage = readObject(RMSTAGE, HashMap.class);
        if (addStage.isEmpty() && rmStage.isEmpty()) {
            System.out.println("Nothing to commit");
            System.exit(0);
        }

        commits = readObject(COMMITS, Commits.class);
        commits.commit(message, addStage, rmStage);
        head = commits.getHead();

        writeObject(HEAD, head);
        writeObject(COMMITS, commits);
        writeObject(ADDSTAGE, clearMap);
        writeObject(RMSTAGE, clearMap);
    }

    public void branch(String branchName) {
       commits = readObject(COMMITS, Commits.class);
       commits.branch(branchName);
       writeObject(COMMITS, commits);
    }

    public void log() {
        commits = readObject(COMMITS, Commits.class);
        System.out.println(commits);
        System.exit(0);
    }

    public void checkOut(String fileName) {
        head = readObject(HEAD, Commit.class);
        String hash = head.find(fileName);
        blobs = readObject(BLOBS, Blobs.class);
        String contents = blobs.get(hash);

        File file = join(CWD, fileName);
        writeContents(file, contents);
    }



}
