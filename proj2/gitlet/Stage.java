package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static gitlet.Utils.*;

public class Stage implements Serializable {
    private final TreeMap<String,String> staged = new TreeMap<>();
    private final TreeSet<String> removal = new TreeSet<>();
    private static final File CWD = new File(System.getProperty("user.dir"));

    public TreeMap<String, String> getStaged() {
        return staged;
    }
    public TreeSet<String> getRemoval() {
        return removal;
    }
    /**
     * 添加缓存区
     * @param fileName 添加的文件名
     * */
    public void stagedForAddition(String fileName) {
        File theFile = join(CWD, fileName);
        String shaID = fileSh1ID(theFile);
        this.staged.put(fileName,shaID);
    }
    public void stagedForRemoval(String fileName) {
        this.removal.add(fileName);
    }
    public boolean isEmpty() {
        return staged.isEmpty() && removal.isEmpty();
    }
    public boolean isAdditionEmpty() {
        return staged.isEmpty();
    }
    public boolean isRemovalEmpty() {
        return removal.isEmpty();
    }
    public boolean containKeyInAddition(String fileName) {
        return staged.containsKey(fileName);
    }
    /*清除暂存区的的该文件*/
    public void rmFileInAddition(String fileName) {
        staged.remove(fileName);
    }

    public void clear_staged() {
        staged.clear();
    }
    public void clear_removal() {
        removal.clear();
    }
    public void printAddition() {
        for (String fileName : staged.keySet()) {
            System.out.println(fileName);
        }
    }
    public void printRemoval() {
        for (String fileName : removal) {
            System.out.println(fileName);
        }
    }
    private String fileSh1ID(File file) {
        byte[] toStaged_byte = readContents(file);
        return sha1((Object) toStaged_byte);
    }

}
