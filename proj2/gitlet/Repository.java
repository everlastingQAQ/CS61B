package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Branch.getHeadBranch;
import static gitlet.Commit.getHeadCommit;
import static gitlet.Utils.*;

/** The Repository class is responsible for the
 *      init, log command.
 *
 *  Here is the repository structure:
 *  .gitlet/ -- folder contains all the Cache
 *      - commits/ -- folder contains all the commits SHA1 string
 *          - 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff -- file contains the serializable commit with the SHA1 name
 *      - blobs/ -- folder contains all the files with the SHA1 file name
 *          - a0da1ea5a15ab613bf9961fd86f010cf74c7ee48 -- file contains the raw file content with the SHA1 name
 *      - staging/ -- folder contains the addition and removal folder
 *      - branches/ -- folder contains the branches files
 *          - master -- file contains the branch file
 *      - head -- file contains the head branch only
 *
 *  @author everlasting
 */
public class Repository {

    /* The diary path */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The .gitlet/commits directory. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");

    /** The .gitlet/blobs directory. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");

    /** The .gitlet/blobs directory. */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");

    /** The .gitlet/branches/master directory. */
    public static final File MASTER = join(BRANCH_DIR, "master");

    /** The .gitlet/staging directory. */
    public static final File STAGING = join(GITLET_DIR, "staging");

    /** The .gitlet/head directory. */
    public static final File HEAD = join(GITLET_DIR, "head");

    /** Initialize the designed folders and files */
    private static void initRepository() {
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        BRANCH_DIR.mkdir();
        Staging initStaging = new Staging(true);
        writeObject(STAGING, initStaging);
    }

    /** -- init
     *  1. init all the repository
     *  2. create the first commit
     *  3. create the master branch
     *  4. write master in the head
     *
     * */
    public static void initGitlet() {
        // init the repository
        initRepository();

        // create the commit
        Commit firstCommit = new Commit("initial commit", new Date(0), null);

        // create the master branch
        Branch master = new Branch("master", firstCommit.getSHA1());
        writeObject(MASTER, master);

        // update head
        writeContents(HEAD, "master");
    }

    /** -- checkout -- [file name]
     *  -- checkout [commit id] -- [file name]
     *
     *  replace the current file with the certain commit's file
     *  1. check the commit has the file or not
     *  2. cover the file
     */
    public static void coverFile(Commit commit, String fileName) {
        Map<String, String> files = commit.getBlobs();
        // check the commit
        if (!files.containsKey(fileName)) {
            throw error("File does not exist in that commit.");
        }

        // replace the file
        File file = join(BLOB_DIR, files.get(fileName));
        File CWDFile = join(CWD, fileName);
        byte[] fileContents = readContents(file);
        writeContents(CWDFile, fileContents);
    }

    /** -- status
     *  1. show the branches status
     *  2. show the staged files
     *  3. show the removed files
     *  4. show the modified but not staged files
     *      - The file is tracked by head commit but the file in the CWD changed or deleted
     *      - The file is in the addFiles but is different from the CWD file, including the file was deleted
     *      if the file was deleted, show (deleted)
     *      else show (modified)
     *  5. show the untracked files
     */
    public static void showStatus() {
        // show the branch status
        System.out.println("===" + " Branches " + "===");
        String headBranchString = getHeadBranch().getName();
        List<String> allBranchesStrings = plainFilenamesIn(BRANCH_DIR);
        ArrayList<String> branchList = new ArrayList<>(allBranchesStrings);
        branchList.sort(Comparator.naturalOrder());
        for (String branchName : branchList) {
            if (branchName.equals(headBranchString)) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println();

        // show the stage files
        // (Too shit to add comment)
        ArrayList<String> addFilesList = new ArrayList<>();
        System.out.println("===" + " Staged Files " + "===");
        Staging stage = new Staging(false);
        Map<String, String> addFiles = stage.viewAddFiles();
        for (Map.Entry<String, String> entry : addFiles.entrySet()) {
            addFilesList.add(entry.getKey());
        }
        addFilesList.sort(Comparator.naturalOrder());
        for (String fileName : addFilesList) {
            System.out.println(fileName);
        }
        System.out.println();

        // show the removed files
        System.out.println("===" + " Removed Files " + "===");
        Set<String> rmFilesNames = stage.viewRmFiles();
        ArrayList<String> rmFilesList = new ArrayList<>(rmFilesNames);
        rmFilesList.sort(Comparator.naturalOrder());
        for (String fileName : rmFilesList) {
            System.out.println(fileName);
        }
        System.out.println();

        // show the modified but not staged files
        System.out.println("===" + " Modifications Not Staged For Commit " + "===");
        ArrayList<String> mNSFilesList = new ArrayList<>();
        Commit headCommit = getHeadCommit();
        Map<String, Boolean> deleted = new TreeMap<>();
        Map<String, String> trackedFiles = headCommit.getBlobs();
        // filter the trackedFiles
        for (Map.Entry<String, String> entry : trackedFiles.entrySet()) {
            String fileName = entry.getKey();
            String fileSHA1 = entry.getValue();
            File file = join(CWD, fileName);

            if (rmFilesNames.contains(fileName)) {
                continue;
            }

            if (addFiles.containsKey(fileName)) {
                continue;
            }

            if (!file.exists()) {
                mNSFilesList.add(fileName);
                deleted.put(fileName, true);
            } else if (!sha1(readContents(file)).equals(fileSHA1)) {
                mNSFilesList.add(fileName);
                deleted.put(fileName, false);
            }
        }

        // filter the addFiles
        for (Map.Entry<String, String> entry : addFiles.entrySet()) {
            String fileName = entry.getKey();
            String fileSHA1 = entry.getValue();
            File file = join(CWD, fileName);
            if (!file.exists()) {
                mNSFilesList.add(fileName);
                deleted.put(fileName, true);
            } else if (!sha1(readContents(file)).equals(fileSHA1)) {
                mNSFilesList.add(fileName);
                deleted.put(fileName, false);
            }
        }

        mNSFilesList.sort(Comparator.naturalOrder());
        for (String fileName : mNSFilesList) {
            if (deleted.get(fileName)) {
                System.out.println(fileName + " (deleted)");
            } else {
                System.out.println(fileName + " (modified)");
            }
        }
        System.out.println();

        // show the file is untracked
        System.out.println("===" + " Untracked Files " + "===");
        ArrayList<String> untrackedFilesList = new ArrayList<>();
        List<String> fileNames = plainFilenamesIn(CWD);
        if (fileNames != null) {
            for (String fileName : fileNames) {
                if ((!addFiles.containsKey(fileName)
                  && !trackedFiles.containsKey(fileName))
                  || rmFilesNames.contains(fileName)) {
                    untrackedFilesList.add(fileName);
                }
            }
        }
        untrackedFilesList.sort(Comparator.naturalOrder());
        for (String fileName : untrackedFilesList) {
            System.out.println(fileName);
        }
        System.out.println();

    }


    /* Assisted Function */

}
