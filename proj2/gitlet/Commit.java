package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /** The message of this Commit. */
    private String message;
    /** The timestamp for this Commit. */
    private long timestamp;
    /** The parent of this Commit */
    private String parentHash;
    /** The file structure and its reference to blob */
    private Map<String, String> fileMap;
    private int unStored;
    private String branch;
    private String commitHash;


    public Commit() {};

    public Commit(String message, Commit parent) {
        this.message = message == null ? "" : message;
        this.parentHash = parent == null ? null : parent.commitHash();
        branch = parent == null ? "master" : parent.whichBranch();
        timestamp = new Date().getTime();
        fileMap = new HashMap<>();
        unStored = parent == null ? 0 : parent.unStored() + 1;
        if (parentHash != null) {
            fileMap = parent.getFileMap();
        }
        hashConstructor();
    }

    public String commitHash() {
        return commitHash;
    }

    private void hashConstructor() {
        if (parentHash != null && !fileMap.isEmpty()) {
            commitHash = sha1(this.message, String.valueOf(timestamp), parentHash, fileMap.toString());
        } else {
            commitHash = sha1(this.message, String.valueOf(timestamp));
        }
    }

    public String whichBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Map<String, String> getFileMap() {
        return fileMap;
    }

    public String parentHash(){
        return parentHash;
    }
    /**
     * @param fileName the file you want to get hashCode
     * @return the hashCode of the input file
     */
    public String find(String fileName) {
        return parentHash == null ? null : fileMap.get(fileName);
    }

    /**
     * @param fileName the file to add into blob
     * @param hash hashCode of the file
     */
    public void add(String fileName, String hash) {
        fileMap.put(fileName, hash);
    }


    public void remove(String fileName, String hash) {
        fileMap.remove(fileName, hash);
    }

    public int unStored() {
        return unStored;
    }

    public void store(Branch branch) {
        this.storeHelper(branch);
    }

    private void storeHelper(Branch branch) {
        Commit parent = branch.parent(this);
        if (this.unStored == 0 || parent == null) {
            return;
        } else if (this.unStored > 0) {
            parent.storeHelper(branch);
            this.unStored = 0;
            branch.addCommit(this);
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        Date date = new Date(timestamp);
        Formatter formatter = new Formatter();
        formatter.format("Date: %1$ta %1$tb %1$td %1$tT %1$tY %1$tz",date);
        sb.append("===\ncommit " + commitHash + "\n");
        sb.append("Data: " +formatter.toString() + "\n");
        sb.append("message: " + message + "\n");
        return sb.toString();
    }
}


