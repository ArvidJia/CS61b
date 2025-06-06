package deque;

import org.junit.Test;
import java.util.Iterator;
import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 50; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 99; i > 50; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }
    }

    @Test
    /* Test iterator:.next(). add 100 nums in the Deque.
     * test if the order is correct
     * */
    public void iteratorTest(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        Iterator<Integer> it = lld1.iterator();

        assertFalse(it.hasNext());

        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
        }

        Iterator<Integer> iter = lld1.iterator();
        assertEquals("Should have next", true, iter.hasNext());

        for (double i = 0; i < 100; i++) {
            assertEquals("Should have the same value", i, (double) iter.next(), 0.0);
        }

        assertEquals("Should not have next", false, iter.hasNext());
    }


    @Test
    /* test if two deque is equal*/
    public void isEqualTest(){
        LinkedListDeque<Integer> i1 = new LinkedListDeque<Integer>();
        LinkedListDeque<Integer> i2 = new LinkedListDeque<Integer>();

        LinkedListDeque<String> s1 = new LinkedListDeque<String>();
        LinkedListDeque<String> s2 = new LinkedListDeque<String>();

        assertEquals("Should have the same value", true, i1.equals(i2));
        assertEquals("Should have the same value", true, s1.equals(s2));

        for (int i = 0; i < 2; i++) {
            i1.addLast(i);
            i2.addLast(i);
        }

        for (double i = 0; i < 2; i++) {
            i1.removeFirst();
            i2.removeFirst();
            assertEquals("Should have the same value", true, i1.equals(i2));
        }

        i1.addLast(99);
        assertEquals("Should have the same value", false, i1.equals(i2));

        s1.addLast("string");
        s2.addLast("string");
        assertEquals("Should have the same value", true, s1.equals(s2));

        s1.removeFirst();
        assertEquals("Should have the same value", false, s1.equals(s2));
    }

    @Test
    public void getRecursiveTest(){
        LinkedListDeque<Integer> d1 = new LinkedListDeque<Integer>();

        for (int i = 0; i < 10; i++) {
            d1.addLast(i);
        }

        for (int i = 0; i < 10; i++) {
            assertEquals(i, (int) d1.getRecursive(i));
        }
    }

    @Test
    public void alDequeEqualTest(){
        ArrayDeque<Integer> a1 = new ArrayDeque<Integer>();
        LinkedListDeque<Integer> l1 = new LinkedListDeque<Integer>();
        int[] arr1 = {0,1, 2, 3, 4, 5, 6, 7, 8, 9};

        assertEquals(true, a1.equals(l1));
        assertEquals(true, l1.equals(a1));
        assertEquals(false, a1.equals(arr1));

        for (int i = 0; i < 10; i++) {
            a1.addLast(i);
            l1.addLast(i);
        }

        assertEquals(true, a1.equals(l1));
        assertEquals(true, l1.equals(a1));
        assertEquals(false, a1.equals(arr1));
    }
}
