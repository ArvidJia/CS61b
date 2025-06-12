package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import static gitlet.Blobs.*;

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
    public static final File MASTER = join(GITLET_DIR, "master");
    public static final File ADDSTAGE = join(GITLET_DIR, "stage");
    public static final File RMSTAGE = join(GITLET_DIR, "rmstage");
    public static final File HEAD = join(GITLET_DIR, "head");
    private Blobs blobs;
    private Branch master;
    private Commit head;
    //HashMap<FileName, HashCode>
    private HashMap<String, String> addStage = new HashMap<>();
    private HashMap<String, String> rmStage = new HashMap<>();
    private final HashMap<String, String> clearMap = new HashMap<>();
    public Repository() {
        blobs = new Blobs();
        master = new Branch();
        head = master.getHead();
    }

    public void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdirs();
            try {
                BLOBS.createNewFile();
                MASTER.createNewFile();
                ADDSTAGE.createNewFile();
                RMSTAGE.createNewFile();
                HEAD.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeObject(HEAD, head);
            writeObject(BLOBS, blobs);
            writeObject(MASTER, (Serializable) master);
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

        Branch headBranch = this.getHeadBranch();
        headBranch.commit(message, addStage,  rmStage);
        head = headBranch.getHead();

        writeObject(HEAD, head);
        writeObject(ADDSTAGE, clearMap);
        writeObject(RMSTAGE, clearMap);
    }

    public void branch(String branchName) {
        head = readObject(HEAD, Commit.class);
        File branchFile = new File(GITLET_DIR, branchName);
        if (branchFile.exists() && branchFile.isFile()) {
            System.out.println("A branch with that name already exists");
            System.exit(1);
        } else {
            try {
                branchFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            head.setBranch(branchName);
            Branch newBranch = new Branch(head);
            writeObject(HEAD, head);
            writeObject(branchFile, newBranch);
        }
    }


    public void log() {
        master = readObject(MASTER, Branch.class);
        System.out.println(master);
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

    private Branch getHeadBranch() {
        File BRANCH = new File(GITLET_DIR, head.whichBranch());
        if (BRANCH.exists() && BRANCH.isFile()) {
            Branch branch = readObject(BRANCH, Branch.class);
            return branch;
        } else {
            System.exit(0);
            return null;
        }
    }

}
