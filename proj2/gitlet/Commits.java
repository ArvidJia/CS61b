package gitlet;

import java.io.Serializable;
import java.util.*;

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
        headHash = init.hash();
        commits.put(headHash, init);
        headHash = init.hash();
        headBranch = "master";
        branch.add(headBranch, init);
    }

    /**
     * Find Commit. Return null if NOT FOUND
     * @param commitId hashCode of Commit
     * @return Commit if FOUND
     */
    public Commit find(String commitId) {
        return commits.get(commitId);
    }

    /**
     * Find Commit. Exit when NOT FOUND
     * @param commitId hashCode of Commit
     * @return Commit if FOUND
     */
    public Commit findFuzzySafely(String commitId) {
        Commit commit = fuzzyFind(commitId);
        if (commit == null) {
            System.err.println("No commit with that id exists");
            System.exit(0);
        }
        return commit;
    }

    /**
     * Fuzzy find Commit by abbreviated id.
     * @param commitId (abbreviated) hashCode of Commit
     * @return found commit or null
     */
    public Commit fuzzyFind(String commitId) {
        if (commitId == null ||  commitId.isEmpty()) {
            System.err.println("No commit with that id exists");
            System.exit(0);
        }

        Commit commit = find(commitId);
        if (commit == null) {
            for (String fullId : commits.keySet()) {
                String lengthEqualId = fullId.substring(0,commitId.length());
                if (commitId.equals(lengthEqualId)) {
                    return commits.get(fullId);
                }
            }
        }
        return commit;
    }

    /**
     * Find Commit based on its message
     * @param message message inside commit metadata
     * @return Commit if FOUND
     */
    public String findMeassgae(String message) {
        StringBuilder result = new StringBuilder();
        for (Commit commit : commits.values()) {
            String messageToFind = commit.message();
            if (commit.message().equals(message)) {
                result.append(commit.hash());
                result.append("\n");
            }
        }
        if (result.length() == 0) {
            return null;
        }
        return result.toString();
    }

    /**
     * Commit data inside STAGE AREA
     * @param message message to commit
     * @param addStage fileMap in addStage
     * @param rmStage fileMap in removeStage
     */
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
        headHash = newCommit.hash();
        commits.put(headHash, newCommit);
        branch.update(headBranch, newCommit);
    }

    /**
     * Add a pointer named branchName to the head Commit
     * @param branchName the name of new branch
     * @return branchHeadHash
     */
    public String branch(String branchName) {
        Commit head = this.getHead();
        String hash = branch.add(branchName, head);
        return hash;
    }

    /**
     * Remove the branch pointer if exists.
     * @param branchName the name of branch to rm
     * @return branchHeadHash
     */
    public String removeBranch(String branchName) {
        String hash = branch.remove(branchName);
        return hash;
    }

    public Commit getBranchHead(String branchName) {
        String hash = branch.get(branchName);
        return findFuzzySafely(hash);
    }

    public Commit getSplitPoint(Commit commit1, Commit commit2) {
       Set<String> visited = new HashSet<>();
       return find(splitPointHelper(commit1.hash(), commit2.hash(), visited));
    }

    public String splitPointHelper(String hash1, String hash2, Set<String> visited) {
        if (visited.contains(hash1)) {
            return hash1;
        }
        visited.add(hash1);
        if (visited.contains(hash2)) {
            return hash2;
        }
        visited.add(hash2);

        String parentHash1 = find(hash1).parentHash();
        String parentHash2 = find(hash2).parentHash();
        parentHash1 = parentHash1 == null ? hash1 : parentHash1;
        parentHash2 = parentHash2 == null ? hash2 : parentHash2;

        return splitPointHelper(parentHash1, parentHash2, visited);
    }

    /**
     * make head commit of the given branch Commit HEAD
     * @param branchName the branch to check out
     * @return head commit of @param branchName
     */
    public Commit checkout(String branchName) {
        return setHeadBranch(branch.get(branchName), branchName);
    }

    public Commit resetHead(String commitId) {
        return setHeadBranch(commitId, headBranch);
    }

    private Commit setHeadBranch(String commitId, String branchName) {
        headHash = commitId;
        headBranch = branch.get(branchName);
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
        headHash = commit.hash();
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
               return nameHash.put(Name, commit.hash());
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
            String hash = commit.hash();
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
