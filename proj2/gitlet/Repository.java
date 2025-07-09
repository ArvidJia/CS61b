package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import java.io.IOException;
import java.io.Serializable;
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
            writeObject(HEAD, head);
            writeObject(BLOBS, blobs);
            writeObject(COMMITS, (Serializable) commits);
            writeObject(RMSTAGE, (Serializable) rmStage);
            writeObject(ADDSTAGE, (Serializable) addStage);
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public void add(String fileName) {
        File file = join(CWD, fileName);
        blobs = readObject(BLOBS, Blobs.class);
        addStage = readObject(ADDSTAGE, HashMap.class);
        Commit head = readObject(HEAD, Commit.class);
        String oldFileHash = head.find(fileName);
        String newFileHash = blobs.add(file);
        if (oldFileHash != null && newFileHash == null) {
            System.out.println("This file " + fileName + "is not changed");
            System.exit(0);
        }
        writeObject(BLOBS, blobs);
        addStage.put(fileName, newFileHash);
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
        addStage = readObject(ADDSTAGE, HashMap.class);
        rmStage = readObject(RMSTAGE, HashMap.class);
        if (addStage.isEmpty() && rmStage.isEmpty()) {
            System.out.println("Nothing to commit");
            System.exit(0);
        }

        commits = readObject(COMMITS, Commits.class);
        commits.commit(message, addStage, rmStage);
        head = commits.getHead();

        writeObject(HEAD, head);
        writeObject(COMMITS, commits);
        writeObject(ADDSTAGE, clearMap);
        writeObject(RMSTAGE, clearMap);
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
        addStage = readObject(ADDSTAGE, HashMap.class);
        rmStage = readObject(RMSTAGE, HashMap.class);

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
        builder.append(this.printNotStaged(commits.getHead(), cwdFileList));
        builder.append("\n");
        builder.append(this.printUnTracked(commits.getHead(), cwdFileList));
        builder.append("\n");

        System.out.println(builder);
    }

    /**
     * checkout to a branch
     * @param branchName the branch to checkout
     */
    public void checkoutBranch(String branchName) {
        commits = readObject(COMMITS, Commits.class);
        commits.checkout(branchName);
        writeObject(COMMITS, commits);
    }

    public void checkoutId(String commitId, String fileName) {
        commits = readObject(COMMITS, Commits.class);
        Commit wanted = commits.find(commitId);
        if (wanted == null) {
            System.out.println("No commit with id exists");
            System.exit(0);
        }
        String fileHash = wanted.find(fileName);
        if (fileHash == null) {
            System.out.println("File does not exist in that commit");
            System.exit(0);
        }
        blobs = readObject(BLOBS, Blobs.class);
        String contents = blobs.get(fileHash);

        File file = join(CWD, fileName);
        writeContents(file, contents);
    }

    public void checkoutFile(String fileName) {
        head = readObject(HEAD, Commit.class);
        String hash = head.find(fileName);
        if (hash == null) {
            System.out.println("File does not exist in that commit");
            System.exit(0);
        }
        blobs = readObject(BLOBS, Blobs.class);
        String contents = blobs.get(hash);

        File file = join(CWD, fileName);
        writeContents(file, contents);
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

    private String printUnTracked(Commit head, List<String> cwdFileList) {
        List<String> unTrackedList = new ArrayList<>();
        Map<String,String> headMap = head.getFileMap();
        for (String fileName : cwdFileList) {
            if (!headMap.containsKey(fileName) && !addStage.containsKey(fileName)) {
                //UnTracked: File exists in CWD but not in HEAD.
                //And it's also not staged.
                unTrackedList.add(fileName);
            }
        }
        Collections.sort(unTrackedList);
        StringBuilder unTracked = new StringBuilder("=== Untracked Files ===\n");
        for (String fileName : unTrackedList) {
            unTracked.append(fileName);
            unTracked.append("\n");
        }
        return unTracked.toString();
    }

    public String printNotStaged(Commit head, List<String> cwdFileList) {
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





}
