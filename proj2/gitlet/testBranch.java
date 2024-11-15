package gitlet;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.join;

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
        gitlet.add("g.txt");
        gitlet.commit("first commit");
        gitlet.branch("other");
        gitlet.checkoutBranch("master");
        gitlet.checkoutBranch("other");
    }
}
