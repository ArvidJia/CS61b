package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Commits implements Serializable {
    /**
     * stores all the commits exist in the repo
     * no matter which branch it's in
     */
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

    public String findMeassgae(String message) {
        StringBuilder result = new StringBuilder();
        for (Commit commit : commits.values()) {
            String messageToFind = commit.message();
            if (commit.message().equals(message)) {
                result.append(commit.commitHash());
                result.append("\n");
            }
        }
        if (result.length() == 0) {
            return null;
        }
        return result.toString();
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
        branch.update(headBranch, newCommit);
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

    /**
     * move the current HEAD pointer to given branch
     * @param branchName the branch to check out
     * @return head commit of @param branchName
     */
    public Commit checkout(String branchName) {
        headHash = branch.get(branchName);
        headBranch = branchName;
        return getHead();
    }

    public void checkoutSafeHelper(String branchName, Map<String, String> untrackedMap) {
        String hash = branch.get(branchName);
        if (hash == null) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (branchName.equals(headBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        } else if (!untrackedMap.isEmpty()) {
            Commit branchHead = commits.get(hash);
            for (Map.Entry<String, String> entry : untrackedMap.entrySet()) {
                String untrackedFile = entry.getKey();
                String contentHash = entry.getValue();
                if (!branchHead.find(untrackedFile,contentHash)) {
                    System.out.println("There is an untracked file in the way;" +
                            " delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
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

    /**
     * Build a string contains ALL the commits
     * @return string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Commit> iterator = commits.values().iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
        }
        return sb.toString();
    }

    /**
     * print commits from head orderly
     * @return commit string from head
     */
    public String log() {
        StringBuilder sb = new StringBuilder();
        return logHelper(getHead(), sb).toString();
    }

    private StringBuilder logHelper(Commit commit, StringBuilder sb) {
        if (commit == null){
            return sb;
        } else {
            sb.append(commit);
            sb = logHelper(parent(commit), sb);
            return sb;
        }
    }

    /**
     * maintaining a bidirectional map <branchName, CommitHashPointsTo>
     * manage branches and the commit it points to.
     * !!!!FAILED!!!! can't make two branches points to one commit
     */
    private class Branch implements Serializable {
        private Map<String, String> nameHash;
        public Branch() {
            nameHash = new HashMap<>();
        }

        public boolean contains(String Name) {
            return nameHash.containsKey(Name);
        }

        //Update the commit branch points to.
        public String update(String Name, Commit commit) {
           if (nameHash.containsKey(Name)) {
               return nameHash.put(Name, commit.commitHash());
           }
           return null;
        }

        public String remove(String Name) {
            if (nameHash.containsKey(Name) ) {
                return nameHash.remove(Name);
            }
            return null;
        }

        //add a new branch to the repo, it points to the given commit.
        public String add(String branchName, Commit commit) {
            if (contains(branchName)) {
                System.out.println("A branch with that name already exists.");
                return null;
            }
            String hash = commit.commitHash();
            nameHash.put(branchName, hash);
            return hash;
        }

        public String get(String key) {
            return nameHash.get(key);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String name : nameHash.keySet()) {
                if (name.equals(headBranch)) {
                    sb.append("*");
                }
                sb.append(name);
                sb.append("\n");
            }
            return sb.toString();
        }
    }
}
