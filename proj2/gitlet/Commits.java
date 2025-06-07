package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import static gitlet.Utils.readObject;

public class Commits implements Serializable {
    private Map<String, Commit> commits;
    private Commit Head;

    public Commits() {
        Head = new Commit("init", null);
        commits = new HashMap<String, Commit>();
        commits.put(Head.getHash(), Head);
    }

    public void commit(String message, File STAGED){
        Commit newCommit = new Commit(message, Head);
        HashMap<String, String> stagedFile = readObject(STAGED, HashMap.class);
        for (Map.Entry<String, String> entry : stagedFile.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            newCommit.put(key, val);
        }
        Head = newCommit;
    }



}
