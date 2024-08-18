package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> right_one =new AListNoResizing<>();
        BuggyAList<Integer>test_one =new BuggyAList<>();
        int[] input = {1,2,3};
        for (int j : input) {
            right_one.addLast(j);
            test_one.addLast(j);
        }
        for (int i = 0; i < 3; i++) {
            right_one.removeLast();
            test_one.removeLast();
            assertEquals(right_one.get(i),test_one.get(i));
        }
    }
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> bugL = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                bugL.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
            } else if (operationNumber ==2 && L.size()>0) {
                int the_last = L.getLast();
                int bug_the_last = bugL.getLast();
                assertEquals(the_last,bug_the_last);
                int remove_last = L.removeLast();
                int bug_remove_last = bugL.removeLast();
                assertEquals(remove_last,bug_remove_last);
            }


        }
        }
    }



