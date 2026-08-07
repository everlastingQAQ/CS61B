package gitlet;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Formatter;

import static gitlet.Branch.getHeadBranch;
import static gitlet.Main.commit;
import static gitlet.Main.isInited;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/**
 *  Every Commit has these information:
 *  1. message
 *  2. timestamp
 *  3. parent commit string
 *  4. file name and SHA1 name
 *  5. second parent commit string
 *
 *  Every Commit use these five things to create SHA1
 *
 *  Commit include these methods:
 *  1. private void getHeadCommitString() -- to get the head commit
 *  2. public void saveCommit() --
 *  3.
 *
 *
 *  @author everlasting
 */
public class Commit implements Serializable {

    /** The message of this Commit. */
    private String message;

    /** The timestamp of this Commit. */
    private Date timestamp;

    /** The parent Commit String of the Commit. */
    private String parentCommitString = null;

    /** All the Blobs */
    private Map<String, String> blobs = new HashMap<>();

    /** The second parent Commit String of the Commit. */
    private String secondParentCommitString = null;

    private String SHA1 = "";

    /** Creat a new commit
     *  1. sync the initial information
     *  2. read the head commit
     *  3. introduce the file as:
     *      parent commit -> rm files -> add files
     *  4. save the commit in the COMMIT_DIR
     *  5. update the branch newest commit
     *  6. remove staging
     * */
    public Commit (String message, Date timestamp, String secondParentCommitString) {
        // Sync the initial information
        this.message = message;
        this.timestamp = timestamp;
        this.secondParentCommitString = secondParentCommitString;

        if (isInited) {
            // Read the head commit
            updateHeadCommitString();

            // introduce the staging files: commit -> rm files -> add files
            this.blobs = new HashMap<>(getHeadCommit().getBlobs());

            Staging stage = readObject(STAGING, Staging.class);
            for (String rmFile : stage.viewRmFiles()) {
                this.blobs.remove(rmFile);
            }

            this.blobs.putAll(stage.viewAddFiles());

            // update branch
            String commitSHA1 = saveCommit();
            String branchName = getHeadBranch().getHeadBranchName();
            Branch headBranch = new Branch(branchName, commitSHA1);
            File headBranchFile = join(BRANCH_DIR, branchName);
            writeObject(headBranchFile, headBranch);

            // remove staging
            Staging emptyStage = new Staging(true);
            writeObject(STAGING, emptyStage);
        } else {
            this.SHA1 = saveCommit();
        }

    }

    /** get the parentCommitString commit */
    private void updateHeadCommitString() {
        this.parentCommitString = getHeadCommitString();
    }

    public String saveCommit() {
        this.SHA1 = sha1(serialize(this));
        File commitFile = join(COMMIT_DIR, this.SHA1);
        writeObject(commitFile, this);
        return this.SHA1;
    }

    public Map<String, String> getBlobs() {
        return this.blobs;
    }

    public String getSHA1() {
        return this.SHA1;
    }

    public static Commit getHeadCommit() {
        return getHeadBranch().HeadCommit();
    }

    public static String getHeadCommitString() {
        return getHeadBranch().HeadCommitString();
    }


    /** -- log
     *  get the commit from the head commit to the first commit
     */
    public static void readLog() {
        Commit curHeadCommit = getHeadCommit();
        while (curHeadCommit.hasParentCommit()) {
            printOutLog(curHeadCommit);
            curHeadCommit = curHeadCommit.getParentCommit();
        }
        printOutLog(curHeadCommit);
    }

    public boolean hasParentCommit() {
        if (this.parentCommitString != null) {
            return true;
        }
        return false;
    }

    public static void printOutLog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getSHA1());

        Date date = commit.timestamp;
        Formatter dateString = new Formatter();
        dateString.format("%1$ta %1$tb %1$te %1$tH:%1$tM:%1$tS %1$tY %1$tz", date);
        System.out.println("Date: " + dateString.toString());
        dateString.close();

        System.out.println(commit.message);
        System.out.println();
    }

    public Commit getParentCommit() {
        String parentCommitString = this.parentCommitString;
        File parentCommitStringFile = join(COMMIT_DIR, parentCommitString);
        Commit parentCommit = readObject(parentCommitStringFile, Commit.class);
        return parentCommit;
    }

    /** -- global-log
     *
     * */
    public static void readGlobalLog() {
        List<String> allFileNames = plainFilenamesIn(COMMIT_DIR);
        for (String fileName : allFileNames) {
            printOutLog(getCommit(fileName));
        }
    }

    public static Commit getCommit(String commitID) {
        if (commitID.length() < 40 && !commitID.isEmpty()) {
            List<String> allFileNames = plainFilenamesIn(COMMIT_DIR);
            int length = commitID.length();
            for (String fileName : allFileNames) {
                String fileNameLen = fileName.substring(0, length);
                if (fileNameLen.equals(commitID)) {
                    return getCommit(fileName);
                }
            }
        } else if (commitID.length() == 40) {
            File file = join(COMMIT_DIR, commitID);
            if (file.exists()) {
                return readObject(file, Commit.class);
            }
        }
        throw error("No commit with that id exists.");
    }

    /** -- find
     *
     */
    public static void findCommit(String commitID) {
        List<String> allFileNames = plainFilenamesIn(COMMIT_DIR);
        boolean hasMessage = false;
        for (String fileName : allFileNames) {
            Commit commit = getCommit(fileName);
            if (commit.message.equals(commitID)) {
                System.out.println(commit.getSHA1());
                hasMessage = true;
            }
        }
        if (!hasMessage) {
            throw error("Found no commit with that message.");
        }
    }

}
