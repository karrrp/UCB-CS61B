package gitlet;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.join;
import static gitlet.Utils.writeContents;

public class testBranch {
    @Test
    public void testBadcheckout() throws IOException {
        Repository gitlet = new Repository();
        gitlet.init();
        File ffile = join(Repository.CWD, "f.txt");
        File gfile = join(Repository.CWD, "g.txt");
        ffile.createNewFile();
        gfile.createNewFile();
        gitlet.add("f.txt");
        writeContents(ffile,"wug");
        gitlet.add("g.txt");
        writeContents(gfile, "not wug");
        gitlet.commit("first commit", null);
        gitlet.branch("other");
        File hfile = join(Repository.CWD, "h.txt");
        hfile.createNewFile();
        writeContents(hfile,"2 wug");
        gitlet.add("h.txt");
        gitlet.rm("g.txt");
        gitlet.commit("Add h.txt and remove g.txt", null);
        gitlet.checkoutBranch("other");
        File kfile = join(Repository.CWD, "k.txt");
        kfile.createNewFile();
        writeContents(kfile,"3 wug");
        gitlet.add("k.txt");
        gitlet.rm("f.txt");
        gitlet.commit("Add k.txt and remove f.txt", null);
        gitlet.checkoutBranch("master");
        gitlet.merge("other");
    }
    @Test
    public void test_rm_branch() throws IOException {
        Repository gitlet = new Repository();
        gitlet.init();
        File ffile = join(Repository.CWD, "f.txt");
        File gfile = join(Repository.CWD, "g.txt");
        ffile.createNewFile();
        gfile.createNewFile();
        gitlet.add("f.txt");
        writeContents(ffile,"This is a wug.");
        gitlet.add("g.txt");
        writeContents(gfile, "This is not a wug.");
        gitlet.commit("first commit", null);
        gitlet.branch("other");
        File hfile = join(Repository.CWD, "h.txt");
        hfile.createNewFile();
        writeContents(hfile, "Another wug.");
        gitlet.add("h.txt");
        gitlet.rm("g.txt");
        writeContents(ffile, "Another wug.");
        gitlet.add("f.txt");
        gitlet.commit("Add h.txt, remove g.txt", null);
        gitlet.checkoutBranch("other");
        writeContents(ffile,"This is not a wug.");
        gitlet.add("f.txt");
        File kfile = join(Repository.CWD, "k.txt");
        writeContents(kfile, "And yet another wug.");
        gitlet.add("k.txt");
        gitlet.commit("Add k.txt and modify f.txt", null);
        gitlet.checkoutBranch("master");
        gitlet.merge("other");
        gitlet.status();

    }
}
