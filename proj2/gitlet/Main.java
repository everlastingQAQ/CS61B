package gitlet;

import java.util.Date;

import static gitlet.Branch.*;
import static gitlet.Commit.*;
import static gitlet.Repository.*;
import static gitlet.Utils.error;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *
 *  @author everlasting
 */
public class Main {

    /** Usage: java gitlet.Main ARGS
     *
     *  init -- init the gitlet repository
     *
     *  add [file name] -- add a file in the coming commit
     *
     *  commit [message] -- commit a new submit
     *
     *  rm [file name] -- remove a file
     *
     *  log -- go through from head commit to the first commit
     *
     *  global-log -- show all the logs
     *
     *  find [commit message] -- find the commits with the commit message
     *
     *  checkout -- [file name] -- make the file to the head commit's file
     *
     *  checkout [commit id] -- [filename] -- make the file to the commitID's commit's file
     *
     *  checkout [branch name] -- switch to a branch
     *
     *  status -- show the gitlet status
     *
     *  branch [branch name] -- create a new branch
     *
     *  rm-branch [branch name] -- remove the branch
     *
     *  reset [commit id] -- reset the user dir to the commit
     */

    /** judge whether it is inited */
    public static boolean isInited = Repository.GITLET_DIR.exists();

    /** SHA1's max length */
    public static final int MAXLEN = 40;

    public static void main(String[] args) {
        if (args.length <= 0) {
            throw error("Please enter a command.");
        }
        String firstArg = args[0];
        if (!firstArg.equals("init") && !isInited) {
            throw error("Not in an initialized Gitlet directory.");
        } else if (firstArg.equals("init") && isInited) {
            throw error("A Gitlet version-control system already exists in the current directory.");
        }
        switch (firstArg) {
            case "init":
                init(args);
                break;
            case "add":
                add(args);
                break;
            case "commit":
                commit(args);
                break;
            case "rm":
                remove(args);
                break;
            case "log":
                log(args);
                break;
            case "global-log":
                globalLog(args);
                break;
            case "find":
                find(args);
                break;
            case "checkout":
                checkout(args);
                break;
            case "branch":
                branch(args);
                break;
            case "status":
                status(args);
                break;
            case "rm-branch":
                rmBranch(args);
                break;
            case "reset":
                reset(args);
                break;
            default:
                throw error("No command with that name exists.");
        }
    }

    public static void init(String[] args) {
        if (args.length != 1) {
            throw error("Incorrect operands.");
        }
        initGitlet();
    }

    public static void add(String[] args) {
        if (args.length != 2) {
            throw error("Incorrect operands.");
        }
        String addFileName = args[1];
        Staging stagingAdd = new Staging(false);
        stagingAdd.addFile(addFileName);
    }

    public static void commit(String[] args) {
        if (args.length == 1 || args[1].isEmpty()) {
            throw error("Please enter a commit message.");
        }
        if (args.length > 2) {
            throw error("Incorrect operands.");
        }

        Staging stagingCommit = new Staging(false);
        if (stagingCommit.isEmpty()) {
            throw error("No changes added to the commit.");
        }

        String message = args[1];
        new Commit(message, new Date(), null);
    }

    public static void remove(String[] args) {
        if (args.length != 2) {
            throw error("Incorrect operands.");
        }
        Staging stagingRm = new Staging(false);
        String rmFilesName = args[1];
        stagingRm.removeFiles(rmFilesName);
    }

    public static void log(String[] args) {
        if (args.length != 1) {
            throw error("Incorrect operands.");
        }
        readLog();
    }

    public static void globalLog(String[] args) {
        if (args.length != 1) {
            throw error("Incorrect operands.");
        }
        readGlobalLog();
    }

    public static void find(String[] args) {
        if (args.length != 2) {
            throw error("Incorrect operands.");
        }
        String commitMessage = args[1];
        findCommit(commitMessage);
    }

    public static void checkout(String[] args) {
        if (args.length == 2) {
            String branchName = args[1];
            switchBranch(branchName);
        } else if (args.length == 3) {
            if (!args[1].equals("--")) {
                throw error("Incorrect operands.");
            }
            String fileName = args[2];
            coverFile(getHeadCommit(), fileName);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                throw error("Incorrect operands.");
            }
            String commitID = args[1];
            String fileName = args[3];
            coverFile(getCommit(commitID), fileName);
        } else {
            throw error("Incorrect operands.");
        }
    }

    public static void branch(String[] args) {
        if (args.length == 1 || args.length > 2) {
            throw error("Incorrect operands.");
        }
        String branchName = args[1];
        newBranch(branchName);
    }

    public static void status(String[] args) {
        if (args.length != 1) {
            throw error("Incorrect operands.");
        }
        showStatus();
    }

    public static void rmBranch(String[] args) {
        if (args.length == 1 || args.length > 2) {
            throw error("Incorrect operands.");
        }
        String branchName = args[1];
        removeBranch(branchName);
    }

    public static void reset(String[] args) {
        if (args.length == 1 || args.length > 2) {
            throw error("Incorrect operands.");
        }
        String commitString = args[1];
        resetCommit(commitString);
    }
}
