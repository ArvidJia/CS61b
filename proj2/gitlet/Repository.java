package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import java.io.IOException;
import java.util.*;

/** Represents a gitlet repository.
 *  Interacts with lower level classes like Commit, Blob or Branch
 *  to make true gitlet command happen.
 *  Central brain of the gitlet system.
 *  @author Arvid Jia
 */
public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS = join(GITLET_DIR, "commits");
    public static final File ADDSTAGE = join(GITLET_DIR, "stage");
    public static final File RMSTAGE = join(GITLET_DIR, "rmstage");
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    private Blobs blobs;
    private Commits commits;
    private Commit head;
    //HashMap<FileName, HashCode>
    private HashMap<String, String> addStage = new HashMap<>();
    private HashMap<String, String> rmStage = new HashMap<>();
    private final HashMap<String, String> clearMap = new HashMap<>();

    public Repository() {
        blobs = new Blobs();
        commits = new Commits();
        head = commits.getHead();
    }

    public void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdirs();
            try {
                BLOBS.createNewFile();
                COMMITS.createNewFile();
                ADDSTAGE.createNewFile();
                RMSTAGE.createNewFile();
                HEAD.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeObject(BLOBS, blobs);
            saveCommitsHead();
            clearStage();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public void add(String fileName) {
        File file = join(CWD, fileName);
        blobs = readObject(BLOBS, Blobs.class);
        head = readObject(HEAD, Commit.class);
        String headFileHash = head.find(fileName);
        String cwdFileHash = blobs.add(file);
        if (cwdFileHash.equals(headFileHash)) {
            System.out.println("This file " + fileName + "is not changed");
            System.exit(0);
        }
        addStage = readObject(ADDSTAGE, HashMap.class);
        writeObject(BLOBS, blobs);
        addStage.put(fileName, cwdFileHash);
        writeObject(ADDSTAGE, addStage);
    }

    public void rm(String fileName) {
        addStage = readObject(ADDSTAGE, HashMap.class);
        Commit head = readObject(HEAD, Commit.class);
        String fileHash = addStage.remove(fileName);
        fileHash = fileHash == null ? head.find(fileHash) : fileHash;
        if (fileHash != null) {
            File rmFile = new File(GITLET_DIR, fileName);
            if (rmFile.exists()) {
                rmFile.delete();
            }
            rmStage = readObject(RMSTAGE, HashMap.class);
            rmStage.put(fileName, fileHash);
        }
    }

    public void commit(String message) {
        readStages();
        if (stageEmpty()) {
            System.out.println("Nothing to commit");
            System.exit(0);
        }

        commits = readObject(COMMITS, Commits.class);
        commits.commit(message, addStage, rmStage);
        head = commits.getHead();

        saveCommitsHead();
        clearStage();
    }

    /**
     * Add a new branch
     * @param branchName The name of branch
     */
    public void branch(String branchName) {
       commits = readObject(COMMITS, Commits.class);
       commits.branch(branchName);
       writeObject(COMMITS, commits);
    }

    public void rmBranch(String branchName) {
        commits = readObject(COMMITS, Commits.class);
        commits.removeBranch(branchName);
        writeObject(COMMITS, commits);
    }

    public void merge(String branchName) {
        readStages();
        readCommitsHead();

        mergeCheckoutBranchSafe(head, branchName, "No branch with that name exists",
                "Can not merge a branch with itself");
        if (!stageEmpty()) {
            System.out.println("Stage is not empty");
            System.exit(0);
        }
        Commit branch = commits.getBranchHead(branchName);
        Commit splitPoint = commits.getSplitPoint(head, branch);

        if (splitPoint.equals(branch)) {
            System.out.println("Given branch is an ancestor of the current branch");
        } else if (splitPoint.equals(head)) {
            System.out.println("Current branch fast-forwarded");
            checkoutBranch(branchName);
        } else {
            normalMerge(head, branch, splitPoint);
            commits.mergeCommit(branchName, addStage, rmStage);
            head = commits.getHead();
            saveCommitsHead();
            clearStage();
        }
    }

    private void normalMerge(Commit head, Commit branch, Commit splitPoint) {
        Map<String,String> headMap = head.getFileMap();
        blobs = readObject(BLOBS, Blobs.class);
        boolean isConflict = false;

        for (String fileName : headMap.keySet()) {
            if (branch.containsFile(fileName)) {
                // the branch has the file
                if (splitPoint.hasSameFile(head, fileName) && !splitPoint.hasSameFile(branch, fileName)) {
                    // branch updated it since splitPoint while head not
                    checkoutId(branch.hash(), fileName);
                    add(fileName);
                } else if (!head.hasSameFile(branch, fileName)) {
                    // Confilict Case: both changed it
                    isConflict = dealWithConflict(head, branch, fileName);
                }
                // Other cases: 1. splitPoint does not hold it:
                //                - Current file equals branch file, remain.
                //                - Confilct Case. Done before.
                //              2. splitPoint holds it:
                //                - Current file changed it, remain.
                //                - Confilct Case. Done before.
            } else {
                // the branch did not have the file
                if (splitPoint.hasSameFile(head, fileName)) {
                    deleteFileSafely(fileName);
                } else if (!splitPoint.hasSameFile(head, fileName)) {
                    // Confilict case
                    isConflict = dealWithConflict(head, branch, fileName);
                }
                // else: current track added or changed the file. keep it.
            }
        }

        for (String fileName : branch.getFileMap().keySet()) {
            if (!head.containsFile(fileName)) {
                // the branch holds the file but head does not
                if (splitPoint.containsFile(fileName)) {
                    if (!splitPoint.hasSameFile(branch, fileName)) {
                        //branch change, head delete, conflict
                        isConflict = dealWithConflict(head, branch, fileName);
                    }
                } else {
                    checkoutId(branch.hash(), fileName);
                    add(fileName);
                }
            }
        }

        if (isConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private boolean dealWithConflict(Commit head, Commit branch, String fileName) {
        String headHash = head.find(fileName);
        String branchHash = branch.find(fileName);
        String headContent = headHash == null ? "" : blobs.get(headHash);
        String branchContent = branchHash == null ? "" : blobs.get(branchHash);
        String confilctContent = "<<<<<<< HEAD\n"
                               + headContent
                               + "=======\n"
                               + branchContent
                               + ">>>>>>>\n";

        File file = join(CWD, fileName);
        writeContents(file, confilctContent);
        add(fileName);
        return true;
    }

    public void find(String message) {
        commits = readObject(COMMITS, Commits.class);
        String id = commits.findMeassgae(message);
        System.out.println(id);
    }

    public void log() {
        commits = readObject(COMMITS, Commits.class);
        System.out.println(commits.log());
        System.exit(0);
    }

    public void globalLog() {
        commits = readObject(COMMITS, Commits.class);
        System.out.println(commits);
        System.exit(0);
    }

    public void status() {
        //update group variables
        commits = readObject(COMMITS, Commits.class);
        head = commits.getHead();
        readStages();

        StringBuilder builder = new StringBuilder();
        builder.append("=== Branches ===\n");
        builder.append(commits.printBranch());
        builder.append("\n");

        builder.append("=== Staged Files ===\n");
        builder.append(this.printStages());
        builder.append("\n");

        builder.append("=== Removed Files ===\n");
        builder.append(this.printRmStages());
        builder.append("\n");

        List<String> cwdFileList = plainFilenamesIn(CWD);
        builder.append(this.printNotStaged(head, cwdFileList));
        builder.append("\n");
        builder.append(this.printUnTracked(head, cwdFileList));
        builder.append("\n");

        System.out.println(builder);
    }

    /**
     * checkout to a branch
     * @param branchName the branch to checkout
     */
    public void checkoutBranch(String branchName) {
        commits = readObject(COMMITS, Commits.class);
        blobs = readObject(BLOBS, Blobs.class);
        Commit oldHead = readObject(HEAD, Commit.class);

        mergeCheckoutBranchSafe(oldHead, branchName, "No such branch exists",
                "No need to checkout the current branch");
        //Passed the safety check
        //Now head is the new head of branch to checkout
        head = commits.checkout(branchName);
        Map<String,String> headMap = head.getFileMap();
        for (String fileName : headMap.keySet()) {
            rewriteFile(fileName, head, blobs);
        }

        for (String fileName : oldHead.getFileMap().keySet()) {
           deleteFileNotInCommit(fileName, head);
        }
        saveCommitsHead();
        clearStage();
    }

    public void checkoutId(String commitId, String fileName) {
        commits = readObject(COMMITS, Commits.class);
        Commit wanted = commits.findFuzzySafely(commitId);
        blobs = readObject(BLOBS, Blobs.class);
        rewriteFile(fileName, wanted, blobs);
    }

    public void checkoutFile(String fileName) {
        head = readObject(HEAD, Commit.class);
        blobs = readObject(BLOBS, Blobs.class);
        rewriteFile(fileName, head, blobs);
    }

    public void reset(String commitId) {
        commits = readObject(COMMITS, Commits.class);
        head = readObject(HEAD, Commit.class);
        blobs = readObject(BLOBS, Blobs.class);

        Commit commit  = commits.findFuzzySafely(commitId);
        for (String fileName : commit.getFileMap().keySet()) {
            rewriteFile(fileName, commit, blobs);
        }
        for (String fileName : head.getFileMap().keySet()) {
            deleteFileNotInCommit(fileName, commit);
        }
        commits.resetHead(commitId);

        saveCommitsHead();
        clearStage();
    }

    /**
     * rewrite file from the input Commit to CWD
     * @param fileName file to rewrite
     * @param commit commit holds the file
     * @param blobs container holds the content
     */
    private void rewriteFile(String fileName, Commit commit, Blobs blobs) {
        String hash = commit.find(fileName);
        if (hash == null) {
            System.out.println("File does not exist in that commit");
            System.exit(0);
        }
        String contents = blobs.get(hash);

        File file = join(CWD, fileName);
        writeContents(file, contents);
    }

    private void deleteFileNotInCommit(String fileName, Commit commit) {
        if (!commit.containsFile(fileName)) {
           deleteFileSafely(fileName);
        }
    }

    private String printStages() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> addIter = addStage.keySet().iterator();
        List<String> addStagedList = new ArrayList<>();
        while (addIter.hasNext()) {
            addStagedList.add(addIter.next());
        }
        Collections.sort(addStagedList);
        for (String fileName : addStagedList) {
            builder.append(fileName);
            builder.append("\n");
        }
        return  builder.toString();
    }

    private String printRmStages() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> rmIter = rmStage.keySet().iterator();
        List<String> rmStagedList = new ArrayList<>();
        while (rmIter.hasNext()) {
            rmStagedList.add(rmIter.next());
        }
        Collections.sort(rmStagedList);
        for (String fileName : rmStagedList) {
            builder.append(fileName);
            builder.append("\n");
        }
        return  builder.toString();
    }

    private List<String> getUnTracked(Commit head, List<String> cwdFileList) {
        List<String> unTrackedList = new ArrayList<>();
        Map<String,String> headMap = head.getFileMap();
        addStage = readObject(ADDSTAGE, HashMap.class);
        for (String fileName : cwdFileList) {
            if (!headMap.containsKey(fileName) && !addStage.containsKey(fileName)) {
                //UnTracked: File exists in CWD but not in HEAD.
                //And it's also not staged.
                unTrackedList.add(fileName);
            }
        }
        return unTrackedList;
    }

    private String printUnTracked(Commit head, List<String> cwdFileList) {
        List<String> unTrackedList = getUnTracked(head, cwdFileList);
        Collections.sort(unTrackedList);
        StringBuilder unTracked = new StringBuilder("=== Untracked Files ===\n");
        for (String fileName : unTrackedList) {
            unTracked.append(fileName);
            unTracked.append("\n");
        }
        return unTracked.toString();
    }

    private String printNotStaged(Commit head, List<String> cwdFileList) {
        Map<String,String> headMap = head.getFileMap();
        List<String> notStagedList = new ArrayList<>();
        for (String fileName : headMap.keySet()) {
            if (!cwdFileList.contains(fileName)) {
                //Deleted: File exists in HEAD but not in CWD
                if (!rmStage.containsKey(fileName)) {
                    fileName += " (deleted)";
                    notStagedList.add(fileName);
                }
            } else {
                String fileHash = Blobs.getContentHash(join(CWD, fileName));
                String fileHashInHead = headMap.get(fileName);
                String fileHashInStage = addStage.get(fileName);
                if (!fileHash.equals(fileHashInHead)) {
                    //Tracked File != Current File
                    if (!fileHash.equals(fileHashInStage)) {
                        //Current File != Staged File
                        fileName += " (modified)";
                        notStagedList.add(fileName);
                    }
                }
            }
        }
        Collections.sort(notStagedList);
        StringBuilder notStaged = new StringBuilder("=== Modifications Not Staged For Commit ===\n");
        for (String fileName : notStagedList) {
            notStaged.append(fileName);
            notStaged.append("\n");
        }
        return notStaged.toString();
    }

    private Map<String,String> getFileMapFromList( List<String> fileList) {
        Map<String,String> fileMap = new HashMap<>();
        for (String fileName : fileList) {
            String fileHash = Blobs.getContentHash(join(CWD, fileName));
            fileMap.put(fileName, fileHash);
        }
        return fileMap;
    }

    private void saveCommitsHead() {
        writeObject(HEAD, head);
        writeObject(COMMITS, commits);
    }

    private void clearStage() {
        writeObject(ADDSTAGE, clearMap);
        writeObject(RMSTAGE, clearMap);
    }

    private void readStages() {
        addStage = readObject(ADDSTAGE, HashMap.class);
        rmStage = readObject(RMSTAGE, HashMap.class);
    }

    private boolean stageEmpty() {
        return addStage.isEmpty() && rmStage.isEmpty();
    }

    private void readCommitsHead() {
        commits = readObject(COMMITS, Commits.class);
        head = readObject(HEAD, Commit.class);
    }

    private void mergeCheckoutBranchSafe(Commit head, String branchName,
                                         String notExistPrompt, String sameWithHeadPrompt) {
        List<String> unTracked = getUnTracked(head, plainFilenamesIn(CWD));
        Map<String,String> unTrackedFileMap = new HashMap<>();
        if (!unTracked.isEmpty()) {
            unTrackedFileMap = getFileMapFromList(unTracked);
        }
        commits.mergeCheckoutBranchSafe(branchName,unTrackedFileMap,
                notExistPrompt, sameWithHeadPrompt);
    }

    private void deleteFileSafely(String fileName) {
        File file = join(CWD, fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}
