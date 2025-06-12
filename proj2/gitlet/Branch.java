package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Branch implements Serializable {
    private Map<String, Commit> commits = new HashMap<>();
    private Commit head;

    public Branch() {
        head = new Commit("initial commit", null);
        commits.put(head.commitHash(), head);
    }

    public Branch(Commit head) {
        this.head = head;
        commits.put(head.commitHash(), head);
    }

    public void commit(String message, HashMap<String, String> addStage, HashMap<String, String> rmStage) {
        Commit newCommit = new Commit(message, head);
        for (Map.Entry<String, String> entry : addStage.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            newCommit.add(key, val);
        }
        for (Map.Entry<String, String> entry : rmStage.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            newCommit.remove(key, val);
        }
        head = newCommit;
        commits.put(head.commitHash(), head);
    }

    public void addCommit(Commit commit) {
        commits.put(commit.commitHash(), commit);
        head = commit;
    }

    public Commit getHead() {
        return head;
    }

    public String toString() {
        return toStringHelper(head).toString();
    }

    public Commit parent(Commit commit){
        String parentHash = commit.parentHash();
        return parentHash == null ? null : commits.get(parentHash);
    }

    private StringBuilder toStringHelper(Commit commit) {
        if (commit == null){
            StringBuilder sb = new StringBuilder();
            return sb;
        } else {
            StringBuilder sb = toStringHelper(parent(commit));
            sb.append(commit);
            sb.append("---\n");
            return sb;
        }
    }
}
