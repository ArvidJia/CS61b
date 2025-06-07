package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static gitlet.Blobs.*;
// TODO: any imports you need here

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
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File STAGED= join(GITLET_DIR, "staged");

    private gitlet.Blobs blobs;
    private Commit head;
    private HashMap<String, String> stagedFile = new HashMap<>();

    public Repository() {
        blobs = new gitlet.Blobs();
        head = new gitlet.Commit("init", null);
    }

    public void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdirs();
            try {
                BLOBS.createNewFile();
                HEAD.createNewFile();
                STAGED.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeObject(BLOBS, blobs);
            writeObject(HEAD, (Serializable) head);
            writeObject(STAGED, (Serializable) stagedFile);
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public void add(String fileName) {
       File file = join(CWD, fileName);

       blobs = readObject(BLOBS, Blobs.class);
       head = readObject(HEAD, Commit.class);
       stagedFile = readObject(STAGED, HashMap.class);

       String fileHash = head.getFileHash(fileName);
       // blobs.add(file) == null when the file already exist in blobs
       if (fileHash != null && blobs.add(file) == null) {
          System.out.println("File " + fileName + " isn't changed.");
          System.exit(0);
       } else {
           stagedFile.put(fileHash, fileName);
           writeObject(BLOBS, blobs);
           writeObject(STAGED, (Serializable) stagedFile);
       }
    }


    public void commit(String message) {
        head = readObject(HEAD, Commit.class);
        Commit newCommit = new gitlet.Commit(message, head);
        stagedFile = readObject(STAGED, HashMap.class);
        for (Map.Entry<String, String> entry : stagedFile.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            newCommit.put(key, val);
        }
        writeObject(HEAD, newCommit);
        writeObject(STAGED,new HashMap<String, String>());
    }

    public void log() {

    }









}
