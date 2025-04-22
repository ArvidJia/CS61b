package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import org.junit.Assert.*;


import java.sql.Array;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {
    @Test
    public void testRandomAddRemove() {
        ArrayDeque<Integer> deque = new ArrayDeque<Integer>();
        int num=0;
        for (int i = 0; i < 500; i++) {
            int chose = StdRandom.uniform(0,5);
            if (chose == 0) {
                deque.addLast(i);
                num++;
            }else if (chose == 1) {
                if(num >0) {
                    deque.removeFirst();
                    num--;
                }
            }else if (chose == 2) {
                deque.addFirst(i);
                num++;
            }else if (chose == 3) {
                if(num >0) {
                    deque.removeLast();
                    num--;
                }
            }else if (chose == 4) {
                assertEquals("Should have the same size", deque.size(), num);
            }
        }
    }

    @Test
    public void testAddRemove() {
        ArrayDeque<Integer> deque = new ArrayDeque<Integer>();

        for (int i = 0; i < 10; i++) {
            deque.addLast(i);
        }

        for (int i = 0; i < 10; i++) {
            assertEquals("Should be equal", i, (double) deque.removeFirst(),0.0);
        }

        for (int i = 0; i < 10; i++) {
            deque.addFirst(i);
        }

        for (int i = 0; i < 10; i++) {
            assertEquals("Should be equal", i, (double) deque.removeLast(),0.0);
        }

    }

    @Test
    public void testEmptySize() {
        ArrayDeque<Integer> deque = new ArrayDeque<Integer>();
        assertEquals("Should be empty",deque.isEmpty(), true);

        deque.addFirst(1);
        assertEquals("Should not be empty",deque.isEmpty(), false);
    }


    @Test
    public void testIter() {
        ArrayDeque<Integer> deque = new ArrayDeque<Integer>();
        Iterator<Integer> iter = deque.iterator();

        for (int i = 0; i < 10; i++) {
            deque.addLast(i);
        }

        for (int i = 0; i < 10; i++) {
            assertEquals("should be equaled value", deque.get(i),iter.next());
        }

        assertEquals("should has no next", iter.hasNext(), false);
    }


    @Test
    public void testIsEqual(){
        ArrayDeque<Integer> i1 = new ArrayDeque<Integer>();
        ArrayDeque<Integer> i2 = new ArrayDeque<Integer>();

        ArrayDeque<String> s1 = new ArrayDeque<String>();
        ArrayDeque<String> s2 = new ArrayDeque<String>();

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



}
