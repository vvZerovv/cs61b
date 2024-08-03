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
    public void test() {
        AListNoResizing<Integer> a=new AListNoResizing<Integer>();
        BuggyAList<Integer> b=new BuggyAList<Integer>();
        a.addLast(4);
        a.addLast(4);
        a.addLast(5);
        a.addLast(5);
        a.addLast(6);
        b.addLast(4);
        b.addLast(4);
        b.addLast(5);
        b.addLast(5);
        b.addLast(6);
        assertEquals(b.size(),a.size());
        assertEquals(a.removeLast(),b.removeLast());
        assertEquals(a.removeLast(),b.removeLast());
        assertEquals(a.removeLast(),b.removeLast());
        assertEquals(a.removeLast(),b.removeLast());
        assertEquals(a.removeLast(),b.removeLast());
    }
}
