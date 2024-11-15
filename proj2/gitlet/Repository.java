package gitlet;
import java.io.*;
import java.util.*;
import static gitlet.Utils.*;
/** Represents a gitlet repository.
 *  does at a high level.
 *  @author karup
 */public class Repository {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File staged = join(GITLET_DIR, "stage");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branch");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "object");
    public static final File BOLBS_DIR = join(OBJECTS_DIR, "bolb");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commit");
    public static final File master = join(BRANCHES_DIR,"master");
    public static final File head = join(GITLET_DIR, "head");
    public static final File newBranch = join(GITLET_DIR, "newBranch");
    /**commit*/
    private File headToFile() {
        String headFilename = readContentsAsString(head);
        return join(BRANCHES_DIR, headFilename);
    }
    /***/
    private  void setPersistence() {
        if (!GITLET_DIR.mkdir() && !GITLET_DIR.exists()) {
            throw new RuntimeException("Failed to create directory: GITLET_DIR");
        }
        if (!BRANCHES_DIR.mkdir() && !BRANCHES_DIR.exists()) {
            throw new RuntimeException("Failed to create directory: BRANCHES_DIR");
        }
        if (!OBJECTS_DIR.mkdir() && !OBJECTS_DIR.exists()) {
            throw new RuntimeException("Failed to create directory: OBJECTS_DIR");
        }
        if (!BOLBS_DIR.mkdir() && !BOLBS_DIR.exists()) {
            throw new RuntimeException("Failed to create directory: BOLBS_DIR");
        }
        if (!COMMITS_DIR.mkdir() && !COMMITS_DIR.exists()) {
            throw new RuntimeException("Failed to create directory: COMMITS_DIR");
        }

        try {
            if (!master.createNewFile() && !master.exists()) {
                throw new RuntimeException("Failed to create file: master");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: master", e);
        }
        try {
            if (!newBranch.createNewFile() && !newBranch.exists()) {
                throw new RuntimeException("Failed to create file: newBranch");
            }
            simpleSet newBranchSet = new simpleSet();
            writeObject(newBranch, newBranchSet);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: newBranch", e);
        }
        try {
            if (!head.createNewFile() && !head.exists()) {
                throw new RuntimeException("Failed to create file: master");
            }
            writeContents(head, master.getName());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: head", e);
        }

        try {
            Stage a = new Stage();
            if (!staged.createNewFile() && !staged.exists()) {
                throw new RuntimeException("Failed to create file: staged");
            }
            writeObject(staged, a);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: staged", e);
        }
    }

    public void init() throws IOException {
        if (!GITLET_DIR.exists()) {
            setPersistence();
            Stage curStaged = readObject(staged, Stage.class);
            Commit initial_commit = new Commit("initial commit", null, curStaged);
            /* hand the commit BolS*/
            try {
                write_commit(initial_commit);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            /* set head and master*/
            writeObject(headToFile(),initial_commit);
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }
    public void commit(String message) throws IOException {
        /*read the current commit and stage*/
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit headCommit = readObject(headToFile(), Commit.class);
        Stage curStaged = readObject(staged, Stage.class);
        Commit curCommit = new Commit(message, headCommit, curStaged);
        /*hand cur commit*/
        try {
            write_commit(curCommit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*change the head point*/
        writeObject(headToFile(),curCommit);
    }
    public void log() {
        Commit commit = readObject(headToFile(), Commit.class);
        while (commit != null) {
            print_Commit(commit);
            commit = findCommit(commit.getParent());
        }
    }
    public void global_log() {
        List<String> allCommits = plainFilenamesIn(COMMITS_DIR);
        if (allCommits == null) {
            return;
        }
        for (String i :allCommits) {
            print_Commit(findCommit(i));
        }
    }

    public void find(String message) {
        List <String> commitSIDlist = plainFilenamesIn(COMMITS_DIR);
        boolean hasTheCommit = false;
        assert commitSIDlist != null;
        for (String SID : commitSIDlist) {
            Commit commit = findCommit(SID);
            if (commit.getMessage().equals(message)) {
                System.out.println(SID);
                hasTheCommit = true;
            }
        }
        if (!hasTheCommit) {
            System.out.println("Found no commit with that message.");
        }
    }
    public void add(String fileName) throws IOException {
        //是否存在这个文件
        File toStagedCWDfile = join(CWD, fileName);
        if (!toStagedCWDfile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String bolbsID = fileSh1ID(toStagedCWDfile);
        Commit curCommit = readObject(headToFile(), Commit.class);
        Stage curStaged = readObject(staged, Stage.class);
        if (curStaged.containKeyInRemoval(fileName)) {
            curStaged.getRemoval().remove(fileName);
            writeObject(staged, curStaged);
            return;
        }
        if (bolbsID.equals(curCommit.getCommitted().get(fileName))) {
            curStaged.rmFileInAddition(fileName);
        } else {
            File theBolbs = join(BOLBS_DIR, bolbsID);
            if (!theBolbs.exists()) {
                if (!theBolbs.createNewFile() && !theBolbs.exists()) {
                    throw new RuntimeException("Failed to create file: theBolbs");
                }
                copyFile(toStagedCWDfile, theBolbs);
            }
            curStaged.stagedForAddition(fileName);
        }
        writeObject(staged, curStaged);
    }

    public void status() {
        /*print the branches*/
        print_Branch();
        print_stage();
        print_ModificationsNoCommit(readObject(staged, Stage.class));
        printUntrackedFiles(readObject(staged, Stage.class));
    }
    private void print_Branch() {
        System.out.println("=== Branches ===");
        for (String branchName : Objects.requireNonNull(plainFilenamesIn(BRANCHES_DIR))) {
            if (branchName.equals(headToFile().getName())) {
                System.out.println('*' + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println(' ');
    }
    private void print_stage() {
        Stage stage = readObject(staged, Stage.class);
        System.out.println("=== Staged Files ===");
        stage.printAddition();
        System.out.println("\n=== Removed Files ===");
        stage.printRemoval();
        System.out.println(" ");
    }
    private void print_ModificationsNoCommit(Stage stage) {
        System.out.println("=== Modifications Not Staged For Commit ===");
        //tracked but unstaged for addition
        List<String> cwdWorkingDir = plainFilenamesIn(CWD);
        if (cwdWorkingDir == null) {
            return;
        }
        Commit headCommit = readObject(headToFile(), Commit.class);
        for (String FileName : cwdWorkingDir) {

            File file = join(CWD, FileName);
            if (headCommit.hasFile(FileName)) {
                if (!fileSh1ID(file).equals(headCommit.getFileUID(FileName))) {
                    System.out.println(FileName);
                }
            } else if (stage.containKeyInAddition(FileName)) {
                if (!fileSh1ID(file).equals(stage.getStaged().get(FileName))) {
                    System.out.println(FileName);
                }
            }
        }
        Set<String> stageFile = stage.getStaged().keySet();
        for (String fileName : stageFile) {
            File file = join(CWD, fileName);
            if (!file.exists()) {
                System.out.println(fileName);
            }
        }
        Set<String> commitFile = headCommit.getCommitted().keySet();
        for (String fileName : commitFile) {
            File file = join(CWD, fileName);
            if (!stageFile.contains(fileName) && !file.exists() && !stage.getRemoval().contains(fileName)) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }
    private void printUntrackedFiles(Stage stage) {
        System.out.println("=== Untracked Files ===");
        List<String> cwdWorkingDir = plainFilenamesIn(CWD);
        if (cwdWorkingDir == null) {
            return;
        }
        Commit headCommit = readObject(headToFile(), Commit.class);
        Set<String> commitFile = headCommit.getCommitted().keySet();
        Set<String> stageFile = stage.getStaged().keySet();
        for (String fileName : cwdWorkingDir) {
            if (!commitFile.contains(fileName) && !stageFile.contains(fileName)) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }
    public void rm(String fileName) {
        /* check if the file been staged or be tracked*/
        Stage curStaged = readObject(staged, Stage.class);
        Commit headCommit = readObject(headToFile(), Commit.class);
        if (!curStaged.containKeyInAddition(fileName) && !headCommit.getCommitted().containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        } else if (headCommit.getCommitted().containsKey(fileName)) {
            File rmFile = join(CWD, fileName);
            if (!rmFile.delete() && rmFile.exists()) {
                throw new RuntimeException("Failed to delete: fileName");
            }
            curStaged.stagedForRemoval(fileName);
        }
        curStaged.rmFileInAddition(fileName);
        writeObject(staged, curStaged);
    }
    private static class simpleSet implements Serializable {
        private final TreeSet<String> set = new TreeSet<>();
        public boolean containBranchName(String branchName) {
            return set.contains(branchName);
        }
        public void addBranchName(String branchName) {
            set.add(branchName);
        }
        public void rmBranchName(String branchName) {
            set.remove(branchName);
        }
    }
    /**create a new branch
     * @param branchName the name of new branch */
    public void branch(String branchName) {
        if(plainFilenamesIn(GITLET_DIR) != null) {
            for (String i : Objects.requireNonNull(plainFilenamesIn(GITLET_DIR))) {
                if (i.equals(branchName)) {
                    System.out.println("A branch with that name already exists.");
                    System.exit(0);
                }
            }
            /*track the commit as master */
            File branch = join(BRANCHES_DIR, branchName);
            try {
                if (!branch.createNewFile() && !branch.exists()) {
                    throw new RuntimeException("Failed to create file: newBranch");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            writeObject(branch, readObject(master, Commit.class));
            simpleSet a = readObject(newBranch, simpleSet.class);
            a.addBranchName(branchName);
            writeObject(newBranch, a);
        }
    }
    public void checkoutHead(String fileName, String commitSId) {
        Commit commit = (commitSId.equals("head"))? readObject(headToFile(), Commit.class):findCommit(commitSId);
        if (commit != null) {
            if (commit.hasFile(fileName)) {
                String headSid = commit.getTrackedFileSId(fileName);
                /*得到当前指定文件*/
                File headTrackedBolb = join(BOLBS_DIR, headSid);
                File workingFile = join(CWD, fileName);
                if (!workingFile.exists()) {
                    try {
                        if (!workingFile.createNewFile() && !workingFile.exists()) {
                            throw new RuntimeException("Fail to create file ");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                copyFile(headTrackedBolb, workingFile);
            } else {
                System.out.println("File does not exist in that commit.");
            }
        } else {
            System.out.println("No commit with that id exists.");
        }
    }
    public void checkoutBranch(String branchName) throws IOException {
        String curBranch = readContentsAsString(head);
        if (branchName.equals(curBranch)) {
            System.out.println("No need to checkout the current branch.");
        } else if (Objects.requireNonNull(plainFilenamesIn(BRANCHES_DIR)).contains(branchName)) {
            File branch = join(BRANCHES_DIR, branchName);
            Commit branchCommit = readObject(branch, Commit.class);
            Commit headCommit = readObject(headToFile(), Commit.class);
            simpleSet isBranchNew = readObject(newBranch, simpleSet.class);
            if (isBranchNew.containBranchName(branchName)) {
                for (String fName : Objects.requireNonNull(plainFilenamesIn(CWD))) {
                    File file = join(CWD, fName);
                    file.delete();
                }
                isBranchNew.rmBranchName(branchName);
                writeObject(newBranch, isBranchNew);
                return;
            }
            removeFile(headCommit, branchCommit, branchName);
            for (String name: branchCommit.getCommitted().keySet()) {
                checkoutHead(name, commitSh1ID(branchCommit));
            }
            Stage stage = readObject(staged, Stage.class);
            stage.clear_staged();
            stage.clear_removal();
            writeObject(staged, stage);
            writeContents(head, branchName);

        } else {
            System.out.println("No such branch exists");
        }
    }
    private void removeFile(Commit headCommit, Commit branchCommit, String branchName) {
        for (String fName : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            File file = join(CWD, fName);
            if (file.exists()) {
                Stage stage = readObject(staged, Stage.class);
                if (!headCommit.getCommitted().containsKey(fName) && !stage.containKeyInAddition(fName) && branchCommit.getCommitted().containsKey(fName)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                } else if (headCommit.getCommitted().containsKey(fName) && !branchCommit.getCommitted().containsKey(fName)) {
                    if (!file.delete() && file.exists()) {
                        throw new RuntimeException("Fail to delete file");
                    }
                }
            }
        }

    }
    public void reset(String commitSID) throws IOException {
        Commit resetCommit = findCommit(commitSID);
        Commit headCommit = readObject(headToFile(), Commit.class);
        if (resetCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            removeFile(headCommit, resetCommit, "z45sd4ad");
            for (String name: resetCommit.getCommitted().keySet()) {
                checkoutHead(name, commitSh1ID(resetCommit));
            }
            Stage stage = readObject(staged, Stage.class);
            stage.clear_staged();
            stage.clear_removal();
            writeObject(headToFile(),resetCommit);
            writeObject(staged, stage);
        }
    }
    public void rm_branch(String branchName) {
        List<String> branchNameList = plainFilenamesIn(BRANCHES_DIR);
        assert branchNameList != null;
        if (!branchNameList.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else if (branchNameList.equals(headToFile())) {
            System.out.println("Cannot remove the current branch.");
        } else {
            File rmBranchName = join(BRANCHES_DIR, branchName);
            if (!rmBranchName.delete() && rmBranchName.exists()) {
                throw new RuntimeException("Fail to delete file");
            }
        }
    }
    public void merge(String branchName) throws IOException {
        mergeEXPHandle(branchName);
        /*找出分支提交和当前提交*/
        File branchHeadFile = join(BRANCHES_DIR, branchName);
        Commit branchHeadCommit = readObject(branchHeadFile, Commit.class);
        Commit curCommit = readObject(headToFile(), Commit.class);
        /*找出分割点*/
        Commit splitCommit = splitCommit(branchName);
        if (splitCommit.equals(branchHeadCommit)) {
            System.out.println("Given branch is an ancestor of the current branch.");
        } else if (splitCommit.equals(curCommit)) {
            System.out.println("Current branch fast-forwarded.");
        } else {
            /*分割点有的文件*/
            for (String FileName : splitCommit.getCommitted().keySet()) {
                String commitUID = splitCommit.getTrackedFileSId(FileName);
                /*检测当前提交状态*/
                /*没有追踪这个文件*/
                if (!curCommit.getCommitted().containsKey(FileName)) {
                    /*分割点有，当前头没有追踪这个文件，分支头文件改了，merge*/
                    if (!branchHeadCommit.getTrackedFileSId(FileName).equals(commitUID)) {
                        /*merge*/
                        mergeFile(FileName, branchHeadCommit.getTrackedFileSId(FileName));
                        File file = new File(FileName);
                        if (file.exists()) {
                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.There is an untracked file in the way; delete it, or add and commit it first.");
                            System.exit(0);
                        }
                    }
                } else if (curCommit.getCommitted().get(FileName).equals(commitUID)) {
                    /*分割点有，当前头提交未更改*/
                    if (!branchHeadCommit.getCommitted().containsKey(FileName)) {
                        /*分割点有，当前头提交未更改,分支删除了————删除当前目录的该文件，并且rm 该文件*/
                        rm(FileName);
                    } else if (!branchHeadCommit.getCommitted().get(FileName).equals(commitUID)) {
                        /*分割点有，当前头提交未更改,分支更改了————checkout该文件，并暂存*/
                        checkoutHead(FileName, commitSh1ID(branchHeadCommit));
                        add(FileName);
                    }
                } else {
                    /*分割点有，当前头提交更改,分支未更改————保持原样*/
                    /*分割点有，当前头提交更改,分支更改或者删除————merge*/
                    if (!branchHeadCommit.getCommitted().containsKey(FileName)) {
                        /*merge*/
                        mergeFile(FileName, branchHeadCommit.getTrackedFileSId(FileName));
                    } else if (!branchHeadCommit.getCommitted().get(FileName).equals(commitUID)) {
                        /*merge*/
                        mergeFile(FileName, branchHeadCommit.getTrackedFileSId(FileName));
                    }
                    add(FileName);
                }
                /*只有当前分支追踪的，保持原样*/
                /*只在给定分支的,check并且检出，类似与check_merge*/
                for (String onlyBranch : findOnlyTrackedByBranch(branchName).keySet()) {
                    File newFile = join(CWD, onlyBranch);
                    if (curCommit.getCommitted().containsKey(onlyBranch)) {
                        mergeFile(onlyBranch,  branchHeadCommit.getCommitted().get(onlyBranch));
                    } else if (newFile.exists()) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.There is an untracked file in the way; delete it, or add and commit it first.");
                        return;
                    } else {
                        /*当前没有这个文件，给定提交有，检出并缓存*/
                        if(!newFile.createNewFile() && !newFile.exists()) {
                            throw new RuntimeException("Fail to create file newFile");
                        }
                        File bolbs = join(BOLBS_DIR, branchHeadCommit.getTrackedFileSId(onlyBranch));
                        copyFile(bolbs, newFile);
                    }
                    add(onlyBranch);
                }
            }
        }
    }
    /** 把两个文件融合**/
    private void mergeFile(String FileName, String branchFileUID) {
        File branchBolbs = join(BOLBS_DIR, branchFileUID);
        StringBuilder fileContent = new StringBuilder();
        Scanner scanner = null;
        try {
            scanner = new Scanner(branchBolbs);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        File file = join(CWD, FileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    System.err.println("Failed to create directory: " + file.getPath());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        while (scanner.hasNextLine()) {
            fileContent.append(scanner.nextLine()).append("\n");
        }
        StringBuilder headContent = new StringBuilder();
        Commit curCommit = readObject(headToFile(), Commit.class);
        File curCommitBolbs = join(BOLBS_DIR, curCommit.getCommitted().get(FileName));
        Scanner headScanner = null;
        try {
            headScanner = new Scanner(curCommitBolbs);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (headScanner.hasNextLine()) {
            headContent.append(headScanner.nextLine()).append("\n");
        }
        try (FileWriter writer = new FileWriter(FileName)) {
            writer.write("<<<<<<< HEAD");
            writer.write("\n");
            writer.write(headContent.toString());// 将整个文件内容写入新文件
            writer.write("======");
            writer.write("\n");
            writer.write(fileContent.toString());
            writer.write(">>>>>>>");
        } catch (IOException e) {
            throw new RuntimeException("Fail to crete writer"); // 异常处理
        }
    }


    /**得到只有给定分支追踪的文件**/
    private HashMap<String, String> findOnlyTrackedByBranch(String branchName) {
        Commit splitCommit = null;
        splitCommit = splitCommit(branchName);
        File branchHeadFile = join(BRANCHES_DIR, branchName);
        HashMap<String, String> kanye = readObject(branchHeadFile, Commit.class).getCommitted();
        for (String FileName : splitCommit.getCommitted().keySet()) {
            kanye.remove(FileName);
        }
        return kanye;
    }
    /**通过分支头和当前节点提交找到第一个祖先节点*/
    private Commit splitCommit(String branchName) {
        File branchHeadFile = join(BRANCHES_DIR, branchName);
        Commit branchHeadCommit = readObject(branchHeadFile, Commit.class);
        Commit curCommit = readObject(headToFile(), Commit.class);
        /*建立一个集合来记录，第一个祖先*/
        HashSet<String> commitsInDegree = new HashSet<>();
        try {
            commitsInDegree.add(commitSh1ID(branchHeadCommit));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            commitsInDegree.add(commitSh1ID(curCommit));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*开始头提交的遍历，从它的父母开始，如果碰见标记了的一定是分支节点，这是因为我们首次遍历集合里除了当前头只有分支了*/
        String pointID = curCommit.getParent();
        while (pointID != null && !commitsInDegree.contains(pointID)) {
            commitsInDegree.add(pointID);
            /*指针改为父母节点ID*/
            pointID = readObject(join(COMMITS_DIR, pointID), Commit.class).getParent();
        }
        /*如果不是走到原始提交了了才退出那就是因为访问到了给出分支*/
        if (pointID != null ) {
            return branchHeadCommit;
        } else {
            pointID = branchHeadCommit.getParent();
            while (!commitsInDegree.contains(pointID)) {
                commitsInDegree.add(pointID);
                /*指针改为父母节点ID*/
                pointID = readObject(join(COMMITS_DIR, pointID), Commit.class).getParent();
            }
            return readObject(join(COMMITS_DIR, pointID), Commit.class);
        }
        /*开始回溯*/
    }
    private void mergeEXPHandle(String branchName) {
        if (!readObject(staged, Stage.class).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
            System.exit(0);
        }
        List<String> branchNameList = plainFilenamesIn(BRANCHES_DIR);
        assert branchNameList != null;
        if (!branchNameList.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else if (branchName.equals(readContentsAsString(headToFile()))) {
            System.out.println("Cannot merge a branch with itself.");
        }
    }

    /**把提交序列化，提交到提交树
     * @param cur 要提交的commit*/
    private void write_commit(Commit cur) throws IOException {
        String commitID = commitSh1ID(cur);
        File commitSnap = join(COMMITS_DIR, commitID);
        writeObject(commitSnap, cur);
    }
    /** Return the SID with file*/
    static String fileSh1ID(File file) {
        byte[] toStaged_byte = readContents(file);
        return sha1((Object) toStaged_byte);
    }
    /**Return the SID with the commit */
    static String commitSh1ID(Commit commit) throws IOException {
        File commitFile = join(GITLET_DIR, "commit");
        if (!commitFile.createNewFile() && !commitFile.exists()) {
            throw new RuntimeException("Fail to create commitFile");
        }
        writeObject(commitFile, commit);
        String commitID = fileSh1ID(commitFile);
        if (!commitFile.delete() && commitFile.exists()) {
            throw new RuntimeException("Fail to delete");
        }
        return commitID;
    }
    /**return the commit with the SID*/
    private Commit findCommit(String SID) {
        if (SID == null) {
            return null;
        }
        File relatedCommit = join(COMMITS_DIR, SID);
        if (relatedCommit.exists()) {
            return readObject(relatedCommit, Commit.class);
        } else {
            return null;
        }
    }
    static void print_Commit(Commit commit) {
        System.out.println("===");
        try {
            System.out.println("commit " + commitSh1ID(commit));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        commit.print_time();
        System.out.println(commit.getMessage());
        System.out.print("\n");
    }

    static void copyFile(File sou, File des) {
        try (FileInputStream fis = new FileInputStream(sou);
             FileOutputStream fos = new FileOutputStream(des)) {
            byte[] buffer = new byte[2000];
            int length;
            // 从源文件中读取数据
            while ((length = fis.read(buffer)) > 0) {
                // 写入到目标文件中
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fail to copy File");
        }
    }
}