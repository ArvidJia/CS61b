package deque;

import org.junit.Test;
import org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {
    @Test
    public void testAddFirstLast() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(1);
        deque.addLast(2);
        deque.addLast(3);



    }

    @Test
    public void testEmpty() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        boolean isEmpty = deque.isEmpty();

        assertEquals(isEmpty, true);
    }
}
