package flik;

import org.junit.Test;
import static org.junit.Assert.*;

public class FilkTest {
    @Test
    public void testEqual() {
        int i = 0;
        for (int j = 0; j < 500; j++, i++) {
            assertTrue("Should have be equaled", Flik.isSameNumber(i, j));
        }
    }

    @Test
    public void testNotEqual() {
        int i = 0;
        for (int j = 1; j < 500; j++, i++) {
            assertNotEquals("Should have not been equaled", Flik.isSameNumber(i, j));
        }
    }
}
