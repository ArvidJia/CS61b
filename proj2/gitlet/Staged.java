package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import static gitlet.Blobs.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;
import static gitlet.Commit.*;

public class Staged implements Serializable {
    /** A Map Staged added file */
    private Map<String, String> addStage = new HashMap<String,String>();
    /** A Map Staged removal file */
    private Map<String, String> removeStage = new HashMap<String,String>();

    /** Staged file */
    public static final File STAGED= join(GITLET_DIR, "staged");

    public Staged() {};


    /**
     * add the FileName:FileHash to addStaged
     * add the FileContent to Blobs
     * write this Staged Object Down
     * @param fileName the file want to add to staging area.
     */
    public void addStage(String fileName) {
        File currentFile = new File(CWD, fileName);
        Commit commit = readObject(COMMITS, Commit.class);
        if (!currentFile.exists() || !currentFile.isDirectory()) {
            System.out.println("The file " + fileName + " does not exist.");
            System.exit(0);
        } else if (commit.fileInHead(fileName, sha1(currentFile))) {
            System.out.println("The file " + fileName + " have no changes.");
            System.exit(0);
        } else {
            Blobs blobs =  readObject(BLOBS, Blobs.class);
            String hashKey = blobs.add(currentFile);
            addStage.put(fileName, hashKey);
        }
    }

    public void removeStage(String fileName) {
        File currentFile = new File(CWD, fileName);
        Commit commit = readObject(COMMITS, Commit.class);
        if (!currentFile.exists() || !currentFile.isDirectory()) {
            System.out.println("The file " + fileName + " does not exist.");
            System.exit(0);
        } else if (commit.fileInHead(fileName, sha1(currentFile))) {
            System.out.println("The file " + fileName + " have no changes.");
            System.exit(0);
        } else {
            Blobs blobs =  readObject(BLOBS, Blobs.class);
            String hashKey = blobs.add(currentFile);
            removeStage.put(fileName,hashKey);
        }
    }

    public Map<String, String> getAddStage() {
        return addStage;
    }

    public Map<String, String> getRemoveStage() {
        return removeStage;
    }


    public boolean isEmpty() {
        return addStage.isEmpty() && removeStage.isEmpty();
    }

    public void clear() {
        this.addStage.clear();
        this.removeStage.clear();
    }


}
