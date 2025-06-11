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
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The timestamp for this Commit. */
    private long timestamp;
    /** The parent of this Commit */
    private Commit parent;
    /** The file structure and its reference to blob */
    private Map<String, String> files;
    private String commitHash;

    public Commit() {};

    public Commit(String message, Commit parent) {
        this.message = message == null ? "" : message;
        this.parent = parent;
        timestamp = new Date().getTime();
        files = new HashMap<>();
        /**
         * TODO: write a function to add stageFile to Commit
         */
        if (parent != null) {
            files = parent.files;
        }
    }


    public String getMessage() {
        return message;
    }

    public String getHash() {
        if (commitHash == null) {
            commitHash = sha1(this);
        }
        return commitHash;
    }

    /**
     * @param fileName the file you want to get hashCode
     * @return the hashCode of the input file
     */
    public String find(String fileName) {
        return parent == null ? null : files.get(fileName);
    }

    /**
     * @param fileName the file to put into blob
     * @param hash hashCode of the file
     */
    public void put(String fileName, String hash) {
        files.put(fileName, hash);
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

    public boolean isInit() {
        return parent == null;
    }

    public Commit parent() {
        return parent;
    }

    public void printInOrder() {
        if (this.isInit()) {
            return;
        } else {
            parent.printInOrder();
            System.out.println(this);
            System.out.println("---");
            System.out.println();
        }
    }




}




