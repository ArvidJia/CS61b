package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static gitlet.Utils.*;
import static gitlet.Repository.*;

/** Stores the file content and its SHA-1 */
public class Blobs implements Serializable {
    // Map<HashCode, FileContent>
    private Map<String, String> blobs;
    public static File BLOBS = join(GITLET_DIR, "blobs");

    public Blobs() {
        blobs = new java.util.HashMap<String, String>();
    }

    public static String getContentHash(File file) {
        Blobs blobs = new Blobs();
        return blobs.add(file);
    }

    /**
     * pass in a path, pull out the file inside and get the fileContentHash
     * @param path the pass want to pull fileContentHash out
     * @return hashmap contains <fileName,fileContentHash> inside the path pass in
     */
    public static HashMap<String,String> getFileHashFromPath(File path) {
        Blobs blobs = new Blobs();
        HashMap<String,String> fileMap = new java.util.HashMap<String, String>();
        List<String> fileNames = plainFilenamesIn(path);
        if (fileNames == null) {
            return fileMap;
        }
        for (String fileName : fileNames) {
            File file = new File(BLOBS, fileName);
            if (file.exists() &&  file.isFile()) {
                String hashkey = blobs.add(file);
                if (hashkey != null) {
                    fileMap.put(fileName, hashkey);
                }
            }
        }
        return fileMap;
    }

    /**
     * Add @file to blobs, return its hashcode
     * @param file file to add into blob
     * @return return hashCode
     */
    public String add(File file) {
        String contents = readContentsAsString(file);
        String hashKey = Utils.sha1(contents);
        if (blobs.containsKey(hashKey)) {
            return hashKey;
        } else {
            String filerContents = Utils.readContentsAsString(file);
            blobs.put(hashKey, filerContents);
        }
        return hashKey;
    }

    public String get(String hash_key) {
        return blobs.get(hash_key);
    }

    public String remove(String hashKey) {
        if (blobs.containsKey(hashKey)) {
            blobs.remove(hashKey);
            return hashKey;
        } else {
            return null;
        }
    }
}
