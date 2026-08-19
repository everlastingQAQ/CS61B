package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

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

    /** -- merge [branch name]
     *  1. check the staging exists files or not
     *  2. check the branch exists or not
     *  3. check the branch name is head branch or not
     *  4. find the split commit
     *      - if lca commit is the given branch's head commit, print ... , return
     *      - if lca commit is the current branch's head commit, reset the given commit, return
     *  5. check the CWD has the untracked files that will be deleted or covered or not
     *      - if it has, throw error
     *  6. go through the given commit's blobs:
     *      - if the file doesn't exist in the current commit's blobs:
     *          - if the file exists in the split commit's blobs:
     *              - if the file is different from the split's file:
     *                  conflict happens
     *          - else the file doesn't exist in the split commit's blobs:
     *              checkout the given's file to the CWD and add the file to the addition stage
     *  7. go through the current commit's blobs:
     *      - if the file exists in the given commit's blobs:
     *          - if the file doesn't exist in the split commit's blobs:
     *              - if the file is different from the given's file:
     *                  conflict happens
     *          - else the file exists in the split commit's blobs:
     *              - if the current's file is the same as the split's file:
     *                  cover the file with the given's file in the stage
     *              - else the current's file is different from the split's file:
     *                  - if the given's file is different from the split's file:
     *                      - if the given's file is different from the current's file:
     *                          conflict happens
     *      - else the file doesn't exist in the given commit's blobs:
     *          - if the file exists in the split commit's blobs:
     *              - if the file is the same as the split file:
     *                  remove the file and stage it for removal
     *              - else the file is different from the split file:
     *                  conflict happens
     *  8. create a commit
     *  9. If conflict happens, print ...
     * */
    public static void mergeBranch(String branchName) {
        // check the stage
        Staging curStage = new Staging(false);
        if (!curStage.isEmpty()) {
            throw error("You have uncommitted changes.");
        }

        // check the branch name exists
        if (!branchNameExists(branchName)) {
            throw error("A branch with that name does not exist.");
        }

        // check the head branch name
        Branch headBranch = getHeadBranch();
        String headBranchName = headBranch.getName();
        if (headBranchName.equals(branchName)) {
            throw error("Cannot merge a branch with itself.");
        }

        // find the split commit
        Branch givenBranch = getBranch(branchName);
        Commit givenCommit = givenBranch.headCommit();
        Commit curCommit = getHeadCommit();
        Commit splitCommit = getSplitPoint(curCommit, givenCommit);

        // check the split commit
        if (splitCommit.getSHA1().equals(givenCommit.getSHA1())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitCommit.getSHA1().equals(curCommit.getSHA1())) {
            resetCommit(givenCommit.getSHA1());
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        // check the untracked files
        Map<String, String> curFiles = curCommit.getBlobs();
        Map<String, String> givenFiles = givenCommit.getBlobs();
        Map<String, String> splitFiles = splitCommit.getBlobs();
        checkUntrackedFiles(curFiles, givenFiles, splitFiles);

        boolean conflictHappened = false;
        conflictHappened = goThroughGivenFiles(givenFiles, splitFiles, curFiles);
        if (goThroughCurFiles(givenFiles, splitFiles, curFiles)) {
            conflictHappened = true;
        }

        // create a commit
        String commitMessage = "Merged " + branchName + " into " + headBranch.getName() + ".";
        new Commit(commitMessage, new Date(), givenBranch.headCommit().getSHA1());

        if (conflictHappened) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /* Assisted Function */
    /** find the lca commit
     *  1. get all the parents commit from cur commit and given commit
     *  2. find the common commits
     *  3. select the commit which isn't the parent's commit from the common commits
     *  */
    public static Commit getSplitPoint(Commit curCommit, Commit givenCommit) {
        Set<String> curParents = getParentCommits(curCommit);
        Set<String> givenParents = getParentCommits(givenCommit);
        Set<String> commonParents = new HashSet<>(curParents);
        commonParents.retainAll(givenParents);
        Set<String> notLatest = new HashSet<>();
        for (String commitName : commonParents) {
            Commit commit = getCommit(commitName);
            if (commit.hasParentCommit()) {
                String parentName = commit.getParentCommit().getSHA1();
                if (commonParents.contains(parentName)) {
                    notLatest.add(parentName);
                }
            }
            if (commit.hasSecondParentCommit()) {
                String secondParentName = commit.getSecondParentCommit().getSHA1();
                if (commonParents.contains(secondParentName)) {
                    notLatest.add(secondParentName);
                }
            }
        }
        for (String commitName : commonParents) {
            if (!notLatest.contains(commitName)) {
                return getCommit(commitName);
            }
        }
        return null;
    }

    /** get all the parent commits */
    public static Set<String> getParentCommits(Commit initialCommit) {
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new TreeSet<>();
        queue.add(initialCommit.getSHA1());
        while (!queue.isEmpty()) {
            String commitName = queue.remove();
            if (visited.contains(commitName)) {
                continue;
            }
            visited.add(commitName);

            Commit commit = getCommit(commitName);
            if (commit.hasParentCommit()) {
                queue.add(commit.getParentCommit().getSHA1());
            }
            if (commit.hasSecondParentCommit()) {
                queue.add(commit.getSecondParentCommit().getSHA1());
            }
        }
        return visited;
    }

    /** check the CWD has the untracked files that will be deleted or covered or not */
    public static void checkUntrackedFiles(Map<String, String> curFiles,
                                           Map<String, String> givenFiles, Map<String, String> splitFiles) {
        List<String> cwdFiles = plainFilenamesIn(CWD);
        if (cwdFiles != null) {
            for (String fileName : cwdFiles) {
                // check the file is untracked or not
                if (curFiles.containsKey(fileName)) {
                    continue;
                }

                if (givenFiles.containsKey(fileName)) {
                    if (!givenFiles.get(fileName).equals(splitFiles.get(fileName))) {
                        throw error("There is an untracked file in the way; " +
                                            "delete it, or add and commit it first.");
                    }
                }
            }
        }
    }

    /** deal with the conflict */
    public static void conflict(String curSHA1, String givenSHA1, String fileName) {
        String curContents = "";
        String givenContents = "";
        if (curSHA1 != null) {
            File curFile = join(BLOB_DIR, curSHA1);
            curContents = readContentsAsString(curFile);
        }
        if (givenSHA1 != null) {
            File givenFile = join(BLOB_DIR, givenSHA1);
            givenContents = readContentsAsString(givenFile);
        }

        String conflictContents = "<<<<<<< HEAD\n" + curContents  + "=======\n" + givenContents + ">>>>>>>\n";

        File cwdFile = join(CWD, fileName);
        writeContents(cwdFile, conflictContents);

        Staging stage = new Staging(false);
        stage.addFile(fileName);
    }

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

    /** go through the given files */
    public static boolean goThroughGivenFiles(Map<String, String> givenFiles,
                                    Map<String, String> splitFiles, Map<String, String> curFiles) {
        boolean conflictHappened = false;
        for (Map.Entry<String, String> entry : givenFiles.entrySet()) {
            String fileName = entry.getKey();
            String fileSHA1 = entry.getValue();

            if (!curFiles.containsKey(fileName)) {
                if (splitFiles.containsKey(fileName)) {
                    if (!fileSHA1.equals(splitFiles.get(fileName))) {
                        conflict(null, fileSHA1, fileName);
                        conflictHappened = true;
                    }
                } else {
                    File cwdFile = join(CWD, fileName);
                    File givenFile = join(BLOB_DIR, fileSHA1);
                    writeContents(cwdFile, readContents(givenFile));
                    Staging stage = new Staging(false);
                    stage.addFile(fileName);
                }
            }
        }
        return conflictHappened;
    }

    /** go through the cur files */
    public static boolean goThroughCurFiles(Map<String, String> givenFiles,
                                            Map<String, String> splitFiles, Map<String, String> curFiles) {
        boolean conflictHappened = false;
        for (Map.Entry<String, String> entry : curFiles.entrySet()) {
            String fileName = entry.getKey();
            String fileSHA1 = entry.getValue();
            File cwdFile = join(CWD, fileName);

            if (givenFiles.containsKey(fileName)) {
                if (!splitFiles.containsKey(fileName)) {
                    if (!fileSHA1.equals(givenFiles.get(fileName))) {
                        conflict(fileSHA1, givenFiles.get(fileName), fileName);
                        conflictHappened = true;
                    }
                } else {
                    if (curFiles.get(fileName).equals(splitFiles.get(fileName))) {
                        String givenSHA1 = givenFiles.get(fileName);
                        File givenFile = join(BLOB_DIR, givenSHA1);
                        writeContents(cwdFile, readContents(givenFile));
                        Staging stage = new Staging(false);
                        stage.addFile(fileName);
                    } else if (!fileSHA1.equals(givenFiles.get(fileName)) && !fileSHA1.equals(splitFiles.get(fileName))
                            && !givenFiles.get(fileName).equals(splitFiles.get(fileName))) {
                        conflict(fileSHA1, givenFiles.get(fileName), fileName);
                        conflictHappened = true;
                    }
                }
            } else {
                if (splitFiles.containsKey(fileName)) {
                    if (fileSHA1.equals(splitFiles.get(fileName))) {
                        cwdFile.delete();
                        Staging stage = new Staging(false);
                        stage.removeFiles(fileName);
                    } else {
                        conflict(fileSHA1, givenFiles.get(fileName), fileName);
                        conflictHappened = true;
                    }
                }
            }
        }
        return conflictHappened;
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
