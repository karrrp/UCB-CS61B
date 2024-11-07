package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    public HashMap<String,String> staged = new HashMap<>();
    public HashSet<String> removal = new HashSet<>();
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * 添加缓存区
     * @param fileName 添加的文件名
     * */
    public void add(String fileName) {
        File theFile = join(CWD, fileName);
        String shaID = fileSh1ID(theFile);
        this.staged.put(fileName,shaID);
    }
    public boolean isEmpty() {
        return staged.isEmpty() && removal.isEmpty();
    }
    /*清除暂存区的的该文件*/
    public void rm(String fileName) {
        staged.remove(fileName);
    }

    public void clear_staged() {
        staged.clear();
    }
    public void clear_removal() {
        removal.clear();
    }

    private String fileSh1ID(File file) {
        byte[] toStaged_byte = readContents(file);
        return sha1((Object) toStaged_byte);
    }

}
