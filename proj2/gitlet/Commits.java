package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Commits implements Serializable {
    private Map<String, Commit> commits = new HashMap<>();
    private Branch branch = new Branch();
    private String headHash;
    private String headBranch;

    public Commits() {
        Commit init = new Commit("initial commit", null);
        headHash = init.commitHash();
        commits.put(headHash, init);
        headHash = init.commitHash();
        headBranch = "master";
        branch.add(headBranch, init);
    }

    public Commit find(String commitId) {
        return commits.get(commitId);
    }

    public void commit(String message, HashMap<String, String> addStage, HashMap<String, String> rmStage) {
        Commit newCommit = new Commit(message, this.getHead());
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
        headHash = newCommit.commitHash();
        commits.put(headHash, newCommit);
        branch.update(headBranch, headHash);
    }

    public String branch(String branchName) {
        Commit head = this.getHead();
        String hash = branch.add(branchName, head);
        return hash;
    }

    public String removeBranch(String branchName) {
        String hash = branch.remove(branchName);
        return hash;
    }

    public String checkout(String branchName) {
        String hash = branch.get(branchName);
        if (hash == null) {
            return null;
        }
        headHash = hash;
        headBranch = branchName;
        return hash;
    }

    public void addCommit(Commit commit) {
        headHash = commit.commitHash();
        commits.put(headHash, commit);
    }

    public Commit getHead() {
        return commits.get(headHash);
    }

    public Commit parent(Commit commit){
        String parentHash = commit.parentHash();
        return parentHash == null ? null : commits.get(parentHash);
    }

    public String printBranch() {
        return branch.toString();
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        return toStringHelper(getHead(), sb).toString();
    }

    private StringBuilder toStringHelper(Commit commit, StringBuilder sb) {
        if (commit == null){
            return sb;
        } else {
            sb.append(commit);
            sb = toStringHelper(parent(commit), sb);
            return sb;
        }
    }

    private class Branch implements Serializable {
        private Map<String, String> nameHash;
        private Map<String, String> hashName;
        public Branch() {
            nameHash = new HashMap<>();
            hashName = new HashMap<>();
        }

        public String update(String Name, String Hash) {
           if (nameHash.containsKey(Name)) {
               String oldhash = nameHash.put(Name, Hash);
               hashName.remove(oldhash, Name);
               hashName.put(Hash, Name);
               return Hash;
           }
           return null;
        }

        public String remove(String Name) {
            if (nameHash.containsKey(Name) && hashName.containsKey(nameHash.get(Name))) {
                String hash = nameHash.remove(Name);
                hashName.remove(hash);
                return hash;
            }
            return null;
        }

        public String add(String branchName, Commit commit) {
            if (nameHash.containsKey(branchName)) {
                return null;
            }
            String hash = commit.commitHash();
            nameHash.put(branchName, hash);
            hashName.put(hash, branchName);
            return hash;
        }

        public String get(String key) {
            if (nameHash.containsKey(key)) {
                return nameHash.get(key);
            }
            return hashName.get(key);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = nameHash.keySet().iterator();
            if (!it.hasNext()) {
                String name = it.next();
                if (name == headBranch){
                    sb.append("*");
                }
                sb.append(it.next());
                sb.append("\n");
            }
            return sb.toString();
        }
    }
}
