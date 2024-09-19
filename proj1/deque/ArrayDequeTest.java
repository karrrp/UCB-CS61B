package deque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;
public class ArrayDequeTest {

    @Test
    public void edgg_add() {
        ArrayDeque<Integer> Ad1 = new ArrayDeque<>();
        Ad1.addFirst(5);
        Ad1.addLast(6);
        Ad1.addFirst(4);
        Ad1.addFirst(3);
        Ad1.addFirst(2);
        Ad1.addFirst(1);
        Ad1.addLast(7);
        Ad1.addLast(8);
        Ad1.addLast(9);
        int actual = Ad1.removeFirst();
        assertEquals(1,actual);
        Ad1.printDeque();
    }
    @Test
    public void checkThebound() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(65);
        ad1.addFirst(73);
        ad1.addLast(17);
        ad1.removeFirst();
    }
    @Test
    public void random_test() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                ad1.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = ad1.size();
                System.out.println("size: " + size);
            } else if (operationNumber == 2) {
                int randVal = StdRandom.uniform(0, 100);
                ad1.addFirst(randVal);
                System.out.println("addFirst(" + randVal + ")");
            }
            else if (operationNumber == 3) {
                if (!ad1.isEmpty()) {
                    int remove1 = ad1.removeFirst();
                    System.out.println("remove First " + remove1);
                }
            }
        }
    }
}
