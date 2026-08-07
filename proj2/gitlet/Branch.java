package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gitlet.Commit.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Branch has:
 *  1. Branch name -> name
 *  2. Branch's head commit's SHA1 -> headCommitString
 *
 *  Methods:
 *  1.
 * */
public class Branch implements Serializable {
    private String name;
    private String headCommitString;

    public Branch(String name, String headCommitString) {
        this.name = name;
        this.headCommitString = headCommitString;
    }

    /** -- branch [branch name]
     *  1. check whether branch name exists
     *  2. create a new branch with the new head commit in the new branch
     *  3. save the branch
     * */
    public static void newBranch(String branchName) {
        // check exist
        if (branchNameExists(branchName)) {
            throw error("A branch with that name already exists.");
        }

        // create the new branch
        Branch newBranch = new Branch(branchName, getHeadCommitString());
        File newBranchFile = join(BRANCH_DIR, branchName);

        // save the branch
        writeObject(newBranchFile, newBranch);
    }


    /** -- checkout [branch name]
     *  1. check whether branch name exists
     *  2. check whether branch is the head branch
     *  3. check the untracked files whether exist in the new branch's head commit
     *      - if exists, throw error
     *  4. delete the files from the pre head commit
     *  5. cover the files with the branch's head commit
     *  6. clear the stage
     *  7. move the head branch to the new branch
     */
    public static void switchBranch(String branchName) {
        // check the branch name
        if (!branchNameExists(branchName)) {
            throw error("No such branch exists.");
        }

        // check the head branch
        Branch preHeadBranch = getHeadBranch();
        if (preHeadBranch.getName().equals(branchName)) {
            throw error("No need to checkout the current branch.");
        }

        // get the pre head commit and cur head commit
        Commit preHeadCommit = getHeadCommit();
        Map<String, String> preTrackedFiles = preHeadCommit.getBlobs();
        Branch newHeadBranch = getBranch(branchName);
        Commit curHeadCommit = getCommit(newHeadBranch.headCommitString());
        Map<String, String> curTrackedFiles = curHeadCommit.getBlobs();

        // check the untracked files
        //TODO
        List<String> cwdFilesNames = plainFilenamesIn(CWD);
        if (cwdFilesNames != null) {
            for (String cwdFileName : cwdFilesNames) {
                if (!preTrackedFiles.containsKey(cwdFileName) && curTrackedFiles.containsKey(cwdFileName)) {
                    if (!fileInStage(cwdFileName)) {
                        throw error("There is an untracked file in the way; delete it, or add and commit it first.");
                    }
                }
            }
        }

        // delete the files from the pre head commit
        if (preTrackedFiles != null) {
            for (Map.Entry<String, String> entry : preTrackedFiles.entrySet()) {
                if (!curTrackedFiles.containsKey(entry.getKey())) {
                    File file = join(CWD, entry.getKey());
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }

        // cover the files with the cur tracked files
        for (Map.Entry<String, String> entry : curTrackedFiles.entrySet()) {
            File file = join(BLOB_DIR, entry.getValue());
            byte[] contents = readContents(file);
            File newFile = join(CWD, entry.getKey());
            writeContents(newFile, contents);
        }

        // clear the stage
        Staging stage = new Staging(true);
        writeObject(STAGING, stage);

        // change the head branch
        writeContents(HEAD, branchName);
    }


    /** -- rm-branch [branch name]
     *  1. check the branch name exists
     *  2. check the branch is head branch or not
     *  3. delete the branch
     */
    public static void removeBranch(String branchName) {
        if (!branchNameExists(branchName)) {
            throw error("A branch with that name does not exist.");
        }
        if (getHeadBranch().getName().equals(branchName)) {
            throw error("Cannot remove the current branch.");
        }
        File branchFile = join(BRANCH_DIR, branchName);
        branchFile.delete();
    }

    /* Assisted Function */
    /** check the branch name exists */
    public static boolean branchNameExists(String branchName) {
        List<String> allBranches = plainFilenamesIn(BRANCH_DIR);
        boolean branchExist = false;
        for (String existBranchName : allBranches) {
            if (existBranchName.equals(branchName)) {
                branchExist = true;
                break;
            }
        }
        return branchExist;
    }

    /** check the file whether tracked by stage */
    public static boolean fileInStage(String fileName) {
        Staging stage = new Staging(false);
        Map<String, String> addFiles = stage.viewAddFiles();
        if (addFiles.containsKey(fileName)) {
            return true;
        }
        return false;
    }

    /** get this branch's head commit */
    public Commit headCommit() {
        File headCommitFile = join(COMMIT_DIR, headCommitString);
        Commit headCommit = readObject(headCommitFile, Commit.class);
        return headCommit;
    }

    /** get the head branch */
    public static Branch getHeadBranch() {
        String headBranchName = readContentsAsString(HEAD);
        File headBranchFile = join(BRANCH_DIR, headBranchName);
        Branch headBranch = readObject(headBranchFile, Branch.class);
        return headBranch;
    }

    /** get the branch name's branch */
    public static Branch getBranch(String branchName) {
        if (!branchNameExists(branchName)) {
            return null;
        }
        File branchFile = join(BRANCH_DIR, branchName);
        Branch branch = readObject(branchFile, Branch.class);
        return branch;
    }

    /* get the branch's name */
    public String getName() {
        return name;
    }

    /* get the branch's head commit String */
    public String headCommitString() {
        return headCommitString;
    }
}
