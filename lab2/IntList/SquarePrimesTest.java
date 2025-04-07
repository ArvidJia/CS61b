package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }


    @Test
    public void testSquarePrimesComplex() {
        IntList lst = IntList.of(0, 9, 3, 1, 17);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("0 -> 9 -> 9 -> 1 -> 289", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimesReturn() {
        IntList lst = IntList.of(4, 6);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 6", lst.toString());
        assertFalse(changed);
    }
}
