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
    public void testThreeAddThreeRemoves() {
        AListNoResizing<Integer> lnr = new AListNoResizing();
        BuggyAList<Integer> bl = new BuggyAList();
        for (int i = 0; i < 3; i++) {
            lnr.addLast(i);
            bl.addLast(i);

            assertEquals(lnr.size(), bl.size());
            assertEquals(lnr.get(i), bl.get(i));
        }

        for (int i = 0; i < 3; i++) {
            lnr.removeLast();
            bl.removeLast();

            assertEquals(lnr.size(), bl.size());
            if (lnr.size() > 0) {
                assertEquals(lnr.getLast(), bl.getLast());
            }
        }
    }


    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> bl = new BuggyAList();
        int N = 3000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal); bl.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
                assertEquals(L.getLast(), bl.getLast());

            } else if (operationNumber == 1) {
                // size
                int size = L.size(); int blSize = bl.size();
                System.out.println("L size: " + size + " || bl size: " + blSize);
                assertEquals(size, blSize);
            } else if (operationNumber == 2) {
                //removeLast
                if (L.size() > 0) {
                    int a=L.removeLast(); int b = bl.removeLast();
                    System.out.println("L removeLast: " + a + " || bl removeLast: " + b + ")");
                    assertEquals(a, b);
                }
            } else if (operationNumber == 3) {
                //getLast
                if (L.size() > 0) {
                    System.out.println("L getLast: " + L.getLast() + " || bl getLast: " + bl.getLast());
                }
            }
        }
    }



}
