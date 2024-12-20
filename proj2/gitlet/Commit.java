package gitlet;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static gitlet.Utils.writeObject;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author chen
 */
public class Commit implements Serializable {
    private String parent;
    private String second_parent;
    private Date date;
    private String message;
    /** The file this commit pointing. */
    private HashMap<String, String> committed;
    /** Dangling Javadoc commen.
     * clone the head commit
     * change the file tracking by stage
     * return the commit object*/
    public Commit(String message, Commit head, Stage staged) throws IOException {
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if (head == null) {
            this.message = "initial commit";
            date = new Date(0);
            this.parent = null;
            this.committed = new HashMap<>();
        } else if (staged.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else {
            parent = Repository.commitSh1ID(head);
            date = new Date();
            this.message = message;
            this.committed = head.committed;
            for (String key : staged.getStaged().keySet()) {
                committed.put(key, staged.getStaged().get(key));
            }
            staged.clear_staged();
            for (String key : staged.getRemoval()) {
                committed.remove(key);
            }
            staged.clear_removal();
            writeObject(Repository.staged, staged);
        }
    }

    public void setSecond_parent(String second_parent) {
        this.second_parent = second_parent;
    }

    public HashMap <String, String> getCommitted() {
        return committed;
    }
    public boolean hasFile(String fileName) {
        return committed.containsKey(fileName);
    }
    public String getFileUID(String fileName) {
        assert hasFile(fileName);
        return committed.get(fileName);
    }
    public String getTrackedFileSId(String fileName) {
        return committed.get(fileName);
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
}