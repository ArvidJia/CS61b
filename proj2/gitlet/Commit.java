package gitlet;

import java.io.Serializable;
import java.util.*;

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
    /**
     * Second parent for merge commit
     */
    private String parentHash2;
    /** The file structure and its reference to blob */
    private Map<String, String> fileMap;
    private String commitHash;

    public Commit() {};

    public Commit(String message, Commit parent) {
        this.message = message == null ? "" : message;
        this.parentHash = parent == null ? null : parent.hash();
        timestamp = parent == null ? new Date(0).getTime() : new Date().getTime();
        fileMap = new HashMap<>();
        if (parentHash != null) {
            fileMap.putAll(parent.getFileMap());
        }
        hashConstructor();
    }

    public Commit(String message, Commit parent1, Commit parent2) {
        this.message = message == null ? "" : message;
        this.parentHash = parent1.hash();
        this.parentHash2 = parent2.hash();
        timestamp = new Date().getTime();
        fileMap = new HashMap<>();
        if (parentHash != null) {
            fileMap.putAll(parent1.getFileMap());
        }
        hashConstructor();
    }

    public boolean isMergeCommit() {
        return parentHash2 != null;
    }

    public String hash() {
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

    public String[] parentHashes() {
        if (parentHash == null) {
            return null;
        }
        if (isMergeCommit()) {
            return new String[] {parentHash, parentHash2};
        } else {
            return new String[] {parentHash};
        }
    }

    public boolean containsFile(String fileName) {
        return fileMap.containsKey(fileName);
    }

    /**
     * @param fileName the file you want to get hashCode
     * @return hashCode of the input file, return null when not exist
     */
    public String find(String fileName) {
        return parentHash == null ? null : fileMap.get(fileName);
    }

    /**
     * Determines if the specified file and its hash code exist in the file map
     * and matches the given hash. Returns false if the parent hash is null or
     * if the file is not found or does not match the given hash.
     *
     * @param fileName the name of the file whose hash is to be verified
     * @param hash the expected hash value to be compared
     * @return true if the file exists in the file map and its hash matches the
     *         provided hash, false otherwise
     */
    public boolean find(String fileName, String hash) {
        if (parentHash == null) {
            return false;
        }
        if (fileMap.containsKey(fileName)) {
            return fileMap.get(fileName).equals(hash);
        } else {
            return false;
        }
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

    public List<String> getDiffFiles(Commit commit) {
        if (commit == null) {
            return null;
        }
        List<String> diffFiles = new ArrayList<>();
        Map<String, String> fileMap1 = getFileMap();
        Map<String, String> fileMap2 = commit.getFileMap();
        for (String fileName : fileMap1.keySet()) {
            if (fileMap2.containsKey(fileName) && !fileMap2.get(fileName).equals(fileMap1.get(fileName)) ) {
                diffFiles.add(fileName);
            }
        }
        return diffFiles.isEmpty() ? null : diffFiles;
    }

    public List<String> getBonusFiles(Commit commit) {
        if (commit == null) {
            return null;
        }
        List<String> bonusFiles = new ArrayList<>();
        Map<String, String> fileMap1 = getFileMap();
        Map<String, String> fileMap2 = commit.getFileMap();
        for (String fileName : fileMap1.keySet()) {
            if (!fileMap2.containsKey(fileName)) {
                bonusFiles.add(fileName);
            }
        }
        return bonusFiles.isEmpty() ? null : bonusFiles;
    }

    public boolean hasSameFile(Commit commit,String fileName) {
        if (commit == null || fileName == null || !fileMap.containsKey(fileName)) {
            return false;
        }
        return find(fileName).equals(commit.find(fileName));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Date date = new Date(timestamp);
        Formatter formatter = new Formatter(Locale.ENGLISH);
        formatter.format("Date: %1$ta %1$tb %1$td %1$tT %1$tY %1$tz",date);
        sb.append("===\ncommit " + commitHash + "\n");
        if (isMergeCommit()) {
            sb.append("Merge: " + parentHash.substring(0,7) + " " + parentHash2.substring(0,7) + "\n");
        }
        sb.append(formatter + "\n");
        sb.append(message);
        sb.append("\n\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Commit && ((Commit) o).hash().equals(this.hash());
    }
}
