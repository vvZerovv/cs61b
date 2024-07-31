package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SuqarePrimesTest2 {
    @Test
    public void testSuqarePrimes() {
        IntList lst=IntList.of(70,3,5,7,11,13,14);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("70 -> 9 -> 25 -> 49 -> 121 -> 169 -> 14", lst.toString());
        assertTrue(changed);
    }

}
