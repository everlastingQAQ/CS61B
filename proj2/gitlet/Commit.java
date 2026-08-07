package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Branch.getHeadBranch;
import static gitlet.Main.*;
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
 *
 *  @author everlasting
 */
public class Commit implements Serializable {

    /** The message of this Commit. */
    private final String message;

    /** The timestamp of this Commit. */
    private final Date timestamp;

    /** The parent Commit String of the Commit. */
    private String parentCommitString = null;

    /** All the Blobs */
    private Map<String, String> blobs = new TreeMap<>();

    /** The second parent Commit String of the Commit. */
    private String secondParentCommitString = null;

    /** Commit's SHA1 */
    private String SHA1 = "";

    /** -- commit [message]
     *  Creat a new commit
     *  1. sync the initial information
     *  2. read the head commit
     *  3. introduce the file as:
     *      parent commit -> rm files -> add files
     *  4. update SHA1
     *  5. save the commit in the COMMIT_DIR
     *  6. update the branch newest commit
     *  7. remove staging
     * */
    public Commit(String message, Date timestamp, String secondParentCommitString) {
        // Sync the initial information
        this.message = message;
        this.timestamp = timestamp;
        this.secondParentCommitString = secondParentCommitString;

        if (!isInited) {
            this.SHA1 = sha1(serialize(this));
            saveCommit();
            return;
        }

        // Read the head commit
        this.parentCommitString = getHeadCommitString();

        // introduce the staging files: commit -> rm files -> add files
        this.blobs = new TreeMap<>(getHeadCommit().getBlobs());

        Staging stage = readObject(STAGING, Staging.class);
        for (String rmFile : stage.viewRmFiles()) {
            this.blobs.remove(rmFile);
        }

        this.blobs.putAll(stage.viewAddFiles());

        // update SHA1
        this.SHA1 = sha1(serialize(this));
        saveCommit();

        // update branch
        String branchName = getHeadBranch().getName();
        Branch headBranch = new Branch(branchName, this.getSHA1());
        File headBranchFile = join(BRANCH_DIR, branchName);
        writeObject(headBranchFile, headBranch);

        // remove staging
        Staging emptyStage = new Staging(true);
        writeObject(STAGING, emptyStage);
    }

    /** save the commit in the commit folder */
    public void saveCommit() {
        File commitFile = join(COMMIT_DIR, this.SHA1);
        writeObject(commitFile, this);
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

    /** print out the log information like:
     *  ===
     *  commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     *  Date: Thu Nov 9 20:00:05 2017 -0800
     *  A commit message.
     *
     * */
    public static void printOutLog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getSHA1());

        Date date = commit.timestamp;
        Formatter dateString = new Formatter();
        dateString.format("%1$ta %1$tb %1$te %1$tH:%1$tM:%1$tS %1$tY %1$tz", date);
        System.out.println("Date: " + dateString.toString());
        dateString.close();

        System.out.println(commit.getMessage());
        System.out.println();
    }

    /** -- global-log
     *  print out all the commit information
     * */
    public static void readGlobalLog() {
        List<String> allFileNames = plainFilenamesIn(COMMIT_DIR);
        for (String fileName : allFileNames) {
            printOutLog(getCommit(fileName));
        }
    }

    /** get commit by the commit SHA1
     *  If the input ID's length is 40:
     *      - find the file directly
     *  else
     *      - find the file one by one
     * */
    public static Commit getCommit(String commitID) {
        if (commitID.length() < MAXLEN && !commitID.isEmpty()) {
            // get all the file names
            List<String> allFileNames = plainFilenamesIn(COMMIT_DIR);
            int length = commitID.length();

            // go through all the files
            for (String fileName : allFileNames) {
                String fileNameLen = fileName.substring(0, length);
                if (fileNameLen.equals(commitID)) {
                    return getCommit(fileName);
                }
            }
        } else if (commitID.length() == MAXLEN) {
            // find the file directly
            File file = join(COMMIT_DIR, commitID);
            if (file.exists()) {
                return readObject(file, Commit.class);
            }
        }
        throw error("No commit with that id exists.");
    }

    /** -- find [commit message]
     *  find the commit by the commit message
     */
    public static void findCommit(String commitMessage) {
        List<String> allFileNames = plainFilenamesIn(COMMIT_DIR);
        boolean hasMessage = false;
        for (String fileName : allFileNames) {
            Commit commit = getCommit(fileName);
            if (commit.getMessage().equals(commitMessage)) {
                System.out.println(commit.getSHA1());
                hasMessage = true;
            }
        }
        if (!hasMessage) {
            throw error("Found no commit with that message.");
        }
    }

    public static void resetCommit(String commitString) {
        File commitFile = join(COMMIT_DIR, commitString);
        if (!commitFile.exists()) {
            throw error("No commit with that id exists.");
        }

    }

    /* Assisted Function */
    /** get the head commit */
    public static Commit getHeadCommit() {
        return getHeadBranch().headCommit();
    }

    /** get the head commit sha1 */
    public static String getHeadCommitString() {
        return getHeadBranch().headCommitString();
    }

    /** judge whether the commit has parent commit */
    public boolean hasParentCommit() {
        if (this.parentCommitString != null) {
            return true;
        }
        return false;
    }

    /** get this commit's parent commit */
    public Commit getParentCommit() {
        File parentCommitStringFile = join(COMMIT_DIR, this.parentCommitString);
        Commit parentCommit = readObject(parentCommitStringFile, Commit.class);
        return parentCommit;
    }

    /** get this commit's message */
    public String getMessage() {
        return this.message;
    }

    /** get this commit's blobs */
    public Map<String, String> getBlobs() {
        return this.blobs;
    }

    /** get this commit's SHA1 */
    public String getSHA1() {
        return this.SHA1;
    }
}
