package gitlet;

import java.io.File;
import java.io.Serializable;
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

    /**
     * Add @file to blobs, return its hashcode when successfully
     * return null when it exists
     * @param file file to add into blob
     * @return if exists, return null; else return hashCode
     */
    public String add(File file) {
        String contents = readContentsAsString(file);
        String hashKey = Utils.sha1(contents);
        if (blobs.containsKey(hashKey)) {
            hashKey = null;
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
