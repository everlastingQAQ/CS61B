package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
     *  3. update the working directory
     */
    public static void switchBranch(String branchName) {
        // check the branch name
        if (!branchNameExists(branchName)) {
            throw error("No such branch exists.");
        }

        // check the head branch
        Branch curHeadBranch = getHeadBranch();
        if (curHeadBranch.getName().equals(branchName)) {
            throw error("No need to checkout the current branch.");
        }

        // get the pre head commit and cur head commit
        Commit curHeadCommit = getHeadCommit();
        Branch newHeadBranch = getBranch(branchName);
        Commit tarHeadCommit = getCommit(newHeadBranch.headCommitString());

        updateWorkingDirectory(curHeadCommit, tarHeadCommit);

        // update the head branch
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
