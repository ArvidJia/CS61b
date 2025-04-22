package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {
    @Test
    public void testMax() {
        Comparator<Integer> comp = Integer::compare;
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<Integer>(comp);

        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        int max = deque.max();
        assertEquals(3, max);
    }

    @Test
    public void testMaxComparator() {
        Comparator<Integer> comp = (a,b) -> -Integer.compare(a,b);
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<Integer>(comp);

        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        int max = deque.max();
        assertEquals(1, max);
    }
}
