package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Branch has:
 *  1. Branch name -> name
 *  2. Branch's head commit -> headCommitString
 *
 *  Methods:
 *  1.
 * */
public class Branch implements Serializable {
    private String name;
    private String headCommitString;

    public Branch(String name) {
        this.name = name;
    }

    public Branch(String name, String headCommitString) {
        this.name = name;
        this.headCommitString = headCommitString;
    }

    public String headCommitString() {
        return headCommitString;
    }

    public Commit headCommit() {
        File headCommitFile = join(COMMIT_DIR, headCommitString);
        Commit headCommit = readObject(headCommitFile, Commit.class);
        return headCommit;
    }

    public String getHeadBranchName() {
        return name;
    }

    public static Branch getHeadBranch() {
        String headBranchName = readContentsAsString(HEAD);
        File headBranchFile = join(BRANCH_DIR, headBranchName);
        Branch headBranch = readObject(headBranchFile, Branch.class);
        return headBranch;
    }
}
