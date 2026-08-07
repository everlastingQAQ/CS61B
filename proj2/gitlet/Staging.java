package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Commit.getHeadCommit;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Staging is responsible for the add, rm command, and create a blob in the blob diary
 *
 *
 * */

public class Staging implements Serializable {

    /** Key: file name, Value: file's SHA-1 */
    private Map<String, String> addFiles;
    private Set<String> rmFiles;

    /** create empty stage */
    public Staging(boolean isEmpty) {
        if (isEmpty) {
            this.addFiles = new HashMap<>();
            this.rmFiles = new HashSet<>();
        } else {
            Staging preStaging = readObject(STAGING, Staging.class);
            this.addFiles = preStaging.addFiles;
            this.rmFiles = preStaging.rmFiles;
        }
    }

    /** -- add [file name]
     *  1. check the file whether exists
     *      - If not exists, throw error
     *  2. check the file whether it has the same file in the previous addFiles
     *      - If it has the same file, return
     *  3. check the file whether it has the same file in the previous commit
     *      - If it has the same file, check the addition whether it has the same file name, then return
     *          - If the addFiles has the same file name, delete it, then return
     *  4. add the file in the addFiles
     *  5. add the file in the blobs with the SHA-1 file name
     *  6. update the addition file
     */
    public void addFile(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            throw error("File does not exist.");
        }
        String fileSHA1 = getFileSHA1(file);

        // remove the file from rmFiles
        boolean rmFilesChanged = rmFiles.remove(fileName);

        // check the file in the addFiles
        if (checkFilesExists(addFiles, fileName, fileSHA1)) {
            if (rmFilesChanged) {
                writeObject(STAGING, this);
            }
            return;
        }

        // check the file in the pre commit
        Commit headCommit = getHeadCommit();
        Map<String, String> preAddFiles = headCommit.getBlobs();
        if (checkFilesExists(preAddFiles, fileName, fileSHA1)) {
            // check the file in the addition
            if (addFiles.containsKey(fileName)) {
                addFiles.remove(fileName);
                writeObject(STAGING, this);
            }
            if (rmFilesChanged) {
                writeObject(STAGING, this);
            }
            return;
        }

        // add file to addFiles
        addFiles.put(fileName, fileSHA1);

        // add file to blobs
        addFileInBlobs(file, fileSHA1);

        // update the addition file
        writeObject(STAGING, this);
    }

    /** get file's SHA-1 by the contents */
    public String getFileSHA1(File file) {
        byte[] fileContents = readContents(file);
        String fileSHA1 = sha1(fileContents);
        return fileSHA1;
    }

    /** add file in the folder
     *  1. check whether there is a same file in the BLOB_DIR
     *      - If it exists, return
     *  2. write contents in the file
     * */
    private void addFileInBlobs(File file, String fileSHA1) {
        File addFile = join(BLOB_DIR, fileSHA1);
        if (addFile.exists()) {
            return;
        }
        byte[] fileContents = readContents(file);
        writeContents(addFile, fileContents);
    }

    /** check the files if they have the same file */
    private boolean checkFilesExists(Map<String, String> files, String fileName, String fileSHA1) {
        if (files.containsKey(fileName) && files.get(fileName).equals(fileSHA1)) {
            return true;
        }
        return false;
    }


    /** -- rm [file name]
     *  1. check the file whether it is in the addFiles and head Commit
     *      - if it exists, delete staging, renew staging
     *      - else throw error
     *  2. check the file whether stays in the CWD
     *      - if exists, delete the file
     * */
    public void removeFiles(String fileName) {
        // check the add files
        boolean addFilesExists = false;
        if (addFiles.containsKey(fileName)) {
            addFiles.remove(fileName);
            addFilesExists = true;
        }

        // check the head commit
        boolean headCommitFilesExists = false;
        Commit headCommit = getHeadCommit();
        Map<String, String> headCommitFiles = headCommit.getBlobs();
        if (headCommitFiles.containsKey(fileName)) {
            rmFiles.add(fileName);
            headCommitFilesExists = true;
        }

        // throw error
        if (!addFilesExists && !headCommitFilesExists) {
            throw error("No reason to remove the file.");
        }

        writeObject(STAGING, this);
        if (headCommitFilesExists) {
            // check the CWD
            File file = join(CWD, fileName);
            if (file.exists()) {
                file.delete();
            }
        }

    }


    /* Assisted Function */
    /** judge the staging whether it is empty */
    public boolean isEmpty() {
        if (addFiles.isEmpty() && rmFiles.isEmpty()) {
            return true;
        }
        return false;
    }

    public Map<String, String> viewAddFiles() {
        return addFiles;
    }

    public Set<String> viewRmFiles() {
        return rmFiles;
    }
}
