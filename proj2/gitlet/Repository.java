package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import java.io.IOException;
import static gitlet.Commit.*;
import static gitlet.Blobs.*;
import static gitlet.Staged.*;
// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Arvid Jia
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    private gitlet.Blobs blobs = new gitlet.Blobs();
    private Commit commit = new gitlet.Commit();
    private gitlet.Staged staged = new gitlet.Staged();

    /** TODO: fill in the rest of this class. */
    public void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdirs();
            try {
                BLOBS.createNewFile();
                COMMITS.createNewFile();
                STAGED.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public void add(String fileName) {
        staged.addStage(fileName);
        writeObject(STAGED, staged);
    }


    public void commit(String message) {
        commit.newCommit(message);
        writeObject(COMMITS, commit);
    }









}
