package gitlet;
import javax.management.loading.ClassLoaderRepository;
import java.io.*;
import java.util.*;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  设立持久性，建立.git文件夹以及blobs和stage文件夹。
 *  将不同命令的逻辑推给不同的类，
 *  does at a high level.
 *  @author karup
 */
public class Repository {
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet's directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File staged = join(GITLET_DIR, "stage");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branch");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "object");
    public static final File BOLBS_DIR = join(OBJECTS_DIR, "bolb");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commit");
    public static final File master = join(BRANCHES_DIR,"master");
    public static final File head = join(GITLET_DIR, "head");
    /**返回头指针文件。保存着序列化后的commit*/
    private File headToFile() {
        String headFilename = readContentsAsString(head);
        return join(BRANCHES_DIR, headFilename);
    }
    /**设立持久性*/
    private  void setPersistence() {
        GITLET_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        BOLBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        try {
            master.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            head.createNewFile();
            writeContents(head,master.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Stage a = new Stage();
            staged.createNewFile();
            writeObject(staged, a);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
/** 建立git库 */
    public void init() throws IOException {
        /*防止建立相同git文件*/
        if (!GITLET_DIR.exists()) {
            setPersistence();
            /*提交初始commit*/
            Stage curStaged = readObject(staged, Stage.class);
            Commit initial_commit = new Commit("initial commit", null, curStaged);
            /* hand the commit bolbs*/
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
/**克隆当前的commit
 * 根据暂存区更新commit跟踪的文件
 * @param message 预备commit的message
 * 更新头指针*/
    public void commit(String message) throws IOException {
        /*read the current commit and stage*/
        Commit headCommit = readObject(headToFile(),Commit.class);
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
/**打印出历史提交信息*/
    public void log() {
        Commit commit = readObject(headToFile(), Commit.class);
        while (commit != null) {
            print_Commit(commit);
            commit = findCommit(commit.getParent());
        }
    }
/**不按照顺序打印所有提交信息*/
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
        /*读出包含所有commitSID的列表*/
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
    /** 判断head 指针指向的commited所指向的bolbs和这个fileName的bolbs是否相同
     * 先she1此file文件，随后读出commit，读出filename对应的hashcode
     * */
    public void add(String fileName) throws IOException {
        File toStagedCWDfile = join(CWD, fileName);
        String bolbsID = fileSh1ID(toStagedCWDfile);
        Commit curCommit = readObject(headToFile(), Commit.class);
        Stage curStaged = readObject(staged, Stage.class);
        /*检查add的文件是不是和当前commit追踪的文件相同
        * 如果有，不add
        * 并且检查缓存区是否有这个文件，有则删除*/
        if (bolbsID.equals(curCommit.committed.get(fileName))) {
            curStaged.staged.remove(fileName);
        } else {
            /*是否bolbs已经有了这个文件版本，没有就创建一个*/
            File theBolbs = join(BOLBS_DIR, bolbsID);
            if (!theBolbs.exists()) {
                theBolbs.createNewFile();
                /*在bolbs树里储存这个文件版本*/
                copyFile(toStagedCWDfile, theBolbs);
            }
            /*把这个文件存到暂存区*/
            curStaged.add(fileName);
        }
        writeObject(staged, curStaged);
    }

    public void status() {
        /*print the branches*/
        print_Branch();
        print_stage();
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
        if (!stage.staged.isEmpty()) {
            System.out.println("=== Staged Files ===");
            for(String fileName : stage.staged.keySet()) {
                System.out.println(fileName);
            }
            System.out.println(" ");
        }
        System.out.println("=== Removal Files ===");
        if (!stage.removal.isEmpty()) {
            for(String fileName : stage.removal) {
                System.out.println(fileName);
            }
            System.out.println(" ");
        }
    }
    /**如果当前commit跟踪了这个文件，那么删除当前目录的这个文件。并把这个文件缓存到removal区，下一次commit不在跟踪这个文件
     * 如果这个文件名在缓存区也要去去掉*/
    public void rm(String fileName) {
        /* check if the file been staged or be tracked*/
        Stage curStaged = readObject(staged, Stage.class);
        Commit headCommit = readObject(headToFile(), Commit.class);
        if (!curStaged.staged.containsKey(fileName) && !headCommit.committed.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        } else if (headCommit.committed.containsKey(fileName)) {
            File rmFile = join(CWD, fileName);
            rmFile.delete();
            curStaged.removal.add(fileName);
        }
        curStaged.rm(fileName);
    }
    /**create a new branch
     * @param branchName the name of new branch */
    public void branch(String branchName) {
        if(plainFilenamesIn(GITLET_DIR) != null) {
            for (String i : plainFilenamesIn(GITLET_DIR)) {
                if (i.equals(branchName)) {
                    return;
                }
            }
            /*track the commit as master */
            File newBranch = join(BRANCHES_DIR, branchName);
            try {
                newBranch.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeObject(newBranch, readObject(master, Commit.class));
            writeContents(head, branchName);
        }
    }
    /**
     * @param fileName 要回溯的文件名.
     * @param commitSId 要回溯的提交
     * 给出文件名和指定的提交，用指定提交的文件
     * 覆盖已存在的文件版本（如果存在）*/
    public void checkoutHead(String fileName, String commitSId) throws IOException {
        Commit commit = (commitSId.equals("head"))? readObject(headToFile(), Commit.class):findCommit(commitSId);
        if (commit != null) {
            if (commit.hasFile(fileName)) {
                String headSid = commit.getTrackedFileSId(fileName);
                /*得到当前指定文件*/
                File headTrackedBolb = join(BOLBS_DIR, headSid);
                File workingFile = join(CWD, fileName);
                if (!workingFile.exists()) {
                    try {
                        workingFile.createNewFile();
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
    /**把当前目录的一切都换成分支头提交跟踪的文件。
     * 对于当前目录的文件，两者都有则覆盖，分支没有当前有就删除，分支有当前没有就添加。
     * 如果一个文件还没有被当前提交跟踪，而分支头提交跟踪了，那么checkout失败。提醒你先删除或者暂存提交。
     * @param branchName 要切换的分支名*/
    public void checkoutBranch(String branchName) throws IOException {
        /*检查branchName是不是当前分支*/
        if (branchName.equals(readContentsAsString(headToFile()))) {
            System.out.println("No need to checkout the current branch.");
        } else if (Objects.requireNonNull(plainFilenamesIn(BRANCHES_DIR)).contains(branchName)) {
            File branch = join(BRANCHES_DIR, branchName);
            Commit branchCommit = readObject(branch, Commit.class);
            Commit headCommit = readObject(headToFile(), Commit.class);
            /* 遍历并删除所有该删的文件，当前分支中跟踪但不存在于签出分支中的任何文件都将被删除*/
            removeFile(headCommit, branchCommit);
            /*重写阶段*/
            for (String name: branchCommit.committed.keySet()) {
                checkoutHead(name, commitSh1ID(branchCommit));
            }
            /*清理缓存区*/
            Stage stage = readObject(staged, Stage.class);
            stage.clear_staged();
            stage.clear_removal();
            /*更改当前指针*/
            writeContents(head, branchName);

        } else {
            System.out.println("No such branch exists");
        }
    }
    /**
     *遍历并删除所有该删的文件，当前分支中跟踪但不存在于签出分支中的任何文件都将被删除*/
    private void removeFile(Commit headCommit, Commit branchCommit) {
        for (String fName : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            File file = join(CWD, fName);
            if (file.exists()) {
                /*当前没有但是在工作文件会被重写*/
                if (!headCommit.committed.containsKey(file.getName()) && branchCommit.committed.containsKey(file.getName())) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(1);
                } else if (headCommit.committed.containsKey(file.getName()) && !branchCommit.committed.containsKey(file.getName())) {
                    file.delete();
                }
            }
        }
    }
    public void reset(String commitSID) throws IOException {
        Commit resetCommit = findCommit(commitSID);
        Commit headCommit = readObject(headToFile(), Commit.class);
        if (resetCommit == null) {
            System.out.println("No commit with that id exists.");
        } else {
            removeFile(headCommit, resetCommit);
            for (String name: resetCommit.committed.keySet()) {
                checkoutHead(name, commitSh1ID(resetCommit));
            }
            /*清理缓存区*/
            Stage stage = readObject(staged, Stage.class);
            stage.clear_staged();
            stage.clear_removal();
            writeObject(headToFile(),resetCommit);
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
            rmBranchName.delete();
        }
    }
    /**r合并分支，更改当前目录，创建一个新的commit*/
    public void merge(String branchName) throws IOException {
        /*如果有暂存区为提交，分支不存在，分支名字是当前头则失败*/
        mergeEXPHandle(branchName);
        /*找出分支提交和当前提交*/
        File branchHeadFile = join(BRANCHES_DIR, branchName);
        Commit branchHeadCommit = readObject(branchHeadFile, Commit.class);
        Commit curCommit = readObject(headToFile(), Commit.class);
        /*找出分割点*/
        Commit splitCommit = splitCommit(branchName);
        if (splitCommit.equals(branchHeadCommit)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(1);
        } else if (splitCommit.equals(curCommit)) {
            System.out.println("Current branch fast-forwarded.");
            System.exit(1);
        } else {
            /*分割点有的文件*/
            for (String FileName : splitCommit.committed.keySet()) {
                String commitUID = splitCommit.getTrackedFileSId(FileName);
                /*检测当前提交状态*/
                /*没有追踪这个文件*/
                if (!curCommit.committed.containsKey(FileName)) {
                    /*分割点有，当前头没有追踪这个文件，分支头文件改了，merge*/
                    if (!branchHeadCommit.getTrackedFileSId(FileName).equals(commitUID)) {
                        /*merge*/
                        mergeFile(FileName, branchHeadCommit.getTrackedFileSId(FileName));
                        File file = new File(FileName);
                        if (file.exists()) {
                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.There is an untracked file in the way; delete it, or add and commit it first.");
                            System.exit(1);
                        }
                    }
                } else if (curCommit.committed.get(FileName).equals(commitUID)) {
                    /*分割点有，当前头提交未更改*/
                    if (!branchHeadCommit.committed.containsKey(FileName)) {
                        /*分割点有，当前头提交未更改,分支删除了————删除当前目录的该文件，并且rm 该文件*/
                        rm(FileName);
                    } else if (!branchHeadCommit.committed.get(FileName).equals(commitUID)) {
                        /*分割点有，当前头提交未更改,分支更改了————checkout该文件，并暂存*/
                        checkoutHead(FileName, commitSh1ID(branchHeadCommit));
                        add(FileName);
                    }
                } else {
                    /*分割点有，当前头提交更改,分支未更改————保持原样*/
                    /*分割点有，当前头提交更改,分支更改或者删除————merge*/
                    if (!branchHeadCommit.committed.containsKey(FileName)) {
                        /*merge*/
                        mergeFile(FileName, branchHeadCommit.getTrackedFileSId(FileName));
                    } else if (!branchHeadCommit.committed.get(FileName).equals(commitUID)) {
                        /*merge*/
                        mergeFile(FileName, branchHeadCommit.getTrackedFileSId(FileName));
                    }
                    add(FileName);
                }
                /*只有当前分支追踪的，保持原样*/
                /*只在给定分支的,check并且检出，类似与check_merge*/
                for (String onlyBranch : findOnlyTrackedByBranch(branchName).keySet()) {
                    File newFile = join(CWD, onlyBranch);
                    if (curCommit.committed.containsKey(onlyBranch)) {
                        mergeFile(onlyBranch,  branchHeadCommit.committed.get(onlyBranch));
                    } else if (newFile.exists()) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(1);
                    } else {
                        /*当前没有这个文件，给定提交有，检出并缓存*/
                        newFile.createNewFile();
                        File bolbs = join(BOLBS_DIR, branchHeadCommit.getTrackedFileSId(onlyBranch));
                        copyFile(bolbs, newFile);
                    }
                    add(onlyBranch);
                }
            }
        }
    }
    /** 把两个文件融合**/
    private void mergeFile(String FileName, String branchFileUID) throws IOException {
        File branchBolbs = join(BOLBS_DIR, branchFileUID);
        StringBuilder fileContent = new StringBuilder();
        Scanner scanner = new Scanner(branchBolbs);
        File file = join(CWD, FileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        while (scanner.hasNextLine()) {
            fileContent.append(scanner.nextLine()).append("\n");
        }
        StringBuilder headContent = new StringBuilder();
        Commit curCommit = readObject(headToFile(), Commit.class);
        File curCommitBolbs = join(BOLBS_DIR, curCommit.committed.get(FileName));
        Scanner headScanner = new Scanner(curCommitBolbs);
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
            e.printStackTrace();  // 异常处理
        }
    }


    /**得到只有给定分支追踪的文件**/
    private HashMap<String, String> findOnlyTrackedByBranch(String branchName) throws IOException {
        Commit splitCommit = splitCommit(branchName);
        File branchHeadFile = join(BRANCHES_DIR, branchName);
        HashMap<String, String> kanye = readObject(branchHeadFile, Commit.class).committed;
        for (String FileName : splitCommit.committed.keySet()) {
            kanye.remove(FileName);
        }
        return kanye;
    }
/**通过分支头和当前节点提交找到第一个祖先节点*/
    private Commit splitCommit(String branchName) throws IOException {
        File branchHeadFile = join(BRANCHES_DIR, branchName);
        Commit branchHeadCommit = readObject(branchHeadFile, Commit.class);
        Commit curCommit = readObject(headToFile(), Commit.class);
        /*建立一个集合来记录，第一个祖先*/
        HashSet<String> commitsInDegree = new HashSet<>();
        commitsInDegree.add(commitSh1ID(branchHeadCommit));
        commitsInDegree.add(commitSh1ID(curCommit));
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
            System.exit(1);
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
        commitFile.createNewFile();
        writeObject(commitFile, commit);
        String commitID = fileSh1ID(commitFile);
        commitFile.delete();
        return commitID;
    }
    /**return the commit with the SID*/
    private Commit findCommit(String SID) {
        if (SID == null) {
            return null;
        }
        File relatedCommit = join(COMMITS_DIR, SID);
        if (relatedCommit.exists()) {
            return readObject(relatedCommit,Commit.class);
        } else {
            System.out.println("Found no commit with that message.");
            System.exit(1);
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
            e.printStackTrace();
        }
    }
}
