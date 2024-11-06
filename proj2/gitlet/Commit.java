package gitlet;
// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author chen
 */
public class Commit implements Serializable {
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet's directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The message of this Commit. */
    private String parent;
    private Date date;
    private String message;
    /** The file this commit pointing. */
    public HashMap<String,String> committed;
    /** Dangling Javadoc commen.
     * clone the head commit
     * change the file tracking by stage
     * return the commit object*/
    public Commit(String message, Commit head, Stage staged) throws IOException {
        if (head == null) {
            this.message = "initial commit";
                // 解析日期字符串
                date = new Date(0);
            this.parent = null;
            this.committed = new HashMap<>();
        } else if (staged.staged == null && staged.removal == null) {
            System.out.println("There nothing to commit");
        } else {
            parent = Repository.commitSh1ID(head);
            date = new Date();
            this.message = message;
            /* 读出并克隆 */
            this.committed = head.committed;
            /* 清除stage的文件，添加到commit里面 */
            for (String key : staged.staged.keySet()) {
                committed.put(key, staged.staged.get(key));
            }
            staged.clear_staged();
            for (String key : staged.removal) {
                committed.remove(key);
            }
            staged.clear_removal();
        }
    }
    public boolean hasFile(String fileName) {
        return committed.containsKey(fileName);
    }
    public String getTrackedFileSId(String fileName) {
        return committed.get(fileName);
    }
    public Date commitDate() {
        return date;
    }
    public String getParent() {
        return parent;
    }
    public String getMessage() {
        return message;
    }
    public void print_time() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-08:00"));
        System.out.println("Date: " + sdf.format(date));
    }
    /* TODO: fill in the rest of this class. */

}
