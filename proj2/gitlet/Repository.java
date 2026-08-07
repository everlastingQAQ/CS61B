package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static gitlet.Utils.*;
import static gitlet.Main.isInited;

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

    /** The diary path */
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
    private static void initRepository() throws IOException {
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        BRANCH_DIR.mkdir();

        if (!MASTER.createNewFile()) {
            throw error("A Gitlet version-control system already exists in the current directory.");
        }

        if (!HEAD.createNewFile()) {
            throw error("A Gitlet version-control system already exists in the current directory.");
        }

        if (!STAGING.createNewFile()) {
            throw error("A Gitlet version-control system already exists in the current directory.");
        }
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
    public static void initGitlet() throws IOException {
        // init the repository
        initRepository();

        // create the commit
        Commit firstCommit = new Commit("initial commit", new Date(0), null);

        // create the master branch
        Branch master = new Branch("master", firstCommit.getSHA1());
        writeObject(MASTER, master);

        // update head
        writeContents(HEAD, "master");

        isInited = true;
    }

    public static void coverFile(Commit commit, String fileName) throws IOException {
        Map<String, String> files = commit.getBlobs();
        if (!files.containsKey(fileName)) {
            throw error("File does not exist in that commit.");
        }
        File file = join(BLOB_DIR, files.get(fileName));
        File CWDFile = join(CWD, fileName);
        if (!CWDFile.exists()) {
            CWDFile.createNewFile();
        }
        byte[] fileContents = readContents(file);
        writeContents(CWDFile, fileContents);
    }

}
