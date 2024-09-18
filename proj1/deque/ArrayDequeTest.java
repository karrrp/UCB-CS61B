package deque;
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
}
