package gitlet;

import java.io.IOException;
import java.util.Date;

import static gitlet.Commit.*;
import static gitlet.Repository.*;
import static gitlet.Utils.error;
import static gitlet.Utils.readObject;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *
 *  @author everlasting
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *
     *  init -- init the gitlet repository
     *
     *  add [file name] -- add a file in the coming commit
     *
     *  commit [message] -- commit a new submit
     *
     *  rm [file name] -- remove a file
     */

    public static boolean isInited = Repository.GITLET_DIR.exists();

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            throw error("Please enter a command.");
        }
        String firstArg = args[0];
        if (!firstArg.equals("init") && !isInited) {
            throw error("Not in an initialized Gitlet directory.");
        } else if (firstArg.equals("init") && isInited) {
            throw error("A Gitlet version-control system already exists in the current directory.");
        }
        switch(firstArg) {
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
                global_log(args);
                break;
            case "find":
                find(args);
                break;
            case "checkout":
                checkout(args);
                break;
            default:
                throw error("No command with that name exists.");
        }
    }

    public static void init(String[] args) throws IOException {
        if (args.length != 1) {
            throw error("Incorrect operands.");
        }
        initGitlet();
    }

    public static void add(String[] args) throws IOException {
        if (args.length != 2) {
            throw error("Incorrect operands.");
        }
        String addFileName = args[1];
        Staging stagingAdd = new Staging();
        stagingAdd.addFile(addFileName);
    }

    public static void commit(String[] args) throws IOException {
        if (args.length == 1 || args[1].isEmpty()) {
            throw error("Please enter a commit message.");
        }
        if (args.length > 2) {
            throw error("Incorrect operands.");
        }

        Staging stagingCommit = new Staging();
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
        Staging stagingRm = new Staging();
        String rmFilesName = args[1];
        stagingRm.rmFiles(rmFilesName);
    }

    public static void log(String[] args) {
        if (args.length != 1) {
            throw error("Incorrect operands.");
        }
        readLog();
    }

    public static void global_log(String[] args) {
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

    public static void checkout(String[] args) throws IOException {
        if (args.length == 2) {
            String branchName = args[1];
            throw error("No command with that name exists.");
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
            String CommitID = args[1];
            String fileName = args[3];
            coverFile(getCommit(CommitID), fileName);
        } else {
            throw error("Incorrect operands.");
        }
    }


}
