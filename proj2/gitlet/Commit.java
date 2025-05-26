package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Iterator;
import java.util.Map;
import static gitlet.Repository.*;
import static gitlet.Staged.*;
import static gitlet.Utils.*;
import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

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

    private class Node {
        /** The message of this Commit. */
        private String message;
        /** The timestamp for this Commit. */
        private long timestamp;
        /** The parent of this Commit */
        private Commit parent;
        /** The file structure and its reference to blob */
        private Map<String, String> files;
        private String Hash;

        public Node(String message, Commit parent, Map<String, String> files) {
            this.message = message == null ? "" : message;
            this.parent = parent;
            this.files = files;
            Hash = sha1(this);
            timestamp = new Date().getTime();
        }

        public Map<String,String> getMap() {
            return files;
        }

        public boolean fileExists(String file) {
            return files.containsKey(file);
        }

        public boolean fileExists(String file, String hashKey) {
            return files.containsKey(file) && files.get(file).equals(hashKey);
        }
    }


    /** The COMMITS FILE */
    public static final File COMMITS = join(GITLET_DIR, "commits");
    private Node root;
    private Node Master;
    private Node Head;
    /** The Master pointer */


    public Commit() {
        root = new Node("Init", null, null);
        Head = Master = root;
    }

    public void newCommit(String message) {
        if (!STAGED.exists()) {
            System.out.println("Staging Area Not Exist ");
            System.exit(1);
        }
        Staged staged = readObject(STAGED, Staged.class);
        Map<String, String> addStage = staged.getAddStage();
        Map<String, String> removeStage = staged.getRemoveStage();
        Node newCommit = new Node(message, this, this.Head.files);

        Iterator<String> addition = addStage.keySet().iterator();
        while(addition.hasNext()) {
            String key = addition.next();
            newCommit.files.put(key, addStage.get(key));
        }

        Iterator<String> removal = removeStage.keySet().iterator();
        while(removal.hasNext()) {
            newCommit.files.remove(removal.next());
        }

        Master = newCommit;
        Head = newCommit;
    }


    public Map<String,String> Head() {
        return Head.getMap();
    }

    public boolean fileInHead(String file, String hashKey) {
        return Head.fileExists(file, hashKey);
    }
}




