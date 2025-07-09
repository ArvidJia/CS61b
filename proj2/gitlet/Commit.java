package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  It maintains some metadata like time, messages and a fileMap.
 *  fileMap<fileName, fileHash>, as the heart of Commit,
 *  holds the reference to blobs, which holds the fileContent.
 *  It serves as a bridge between fileName to fileContent, to
 *  getfileContent, findFileHash or judgeIsFileExists, etc.
 *  @author Arvid Jia
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
    private String commitHash;


    public Commit() {};

    public Commit(String message, Commit parent) {
        this.message = message == null ? "" : message;
        this.parentHash = parent == null ? null : parent.commitHash();
        timestamp = parent == null ? new Date(0).getTime() : new Date().getTime();
        fileMap = new HashMap<>();
        if (parentHash != null) {
            fileMap.putAll(parent.getFileMap());
        }
        hashConstructor();
    }

    public String commitHash() {
        return commitHash;
    }

    public String message() {
        return message;
    }

    private void hashConstructor() {
        if (parentHash != null && !fileMap.isEmpty()) {
            commitHash = sha1(this.message, String.valueOf(timestamp), parentHash, fileMap.toString());
        } else {
            commitHash = sha1(this.message, String.valueOf(timestamp));
        }
    }


    public Map<String, String> getFileMap() {
        return fileMap;
    }

    public String parentHash(){
        return parentHash;
    }
    /**
     * @param fileName the file you want to get hashCode
     * @return hashCode of the input file, return null when not exist
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Date date = new Date(timestamp);
        Formatter formatter = new Formatter(Locale.ENGLISH);
        formatter.format("Date: %1$ta %1$tb %1$td %1$tT %1$tY %1$tz",date);
        sb.append("===\ncommit " + commitHash + "\n");
        sb.append(formatter.toString() + "\n");
        sb.append(message);
        sb.append("\n\n");
        return sb.toString();
    }



}


