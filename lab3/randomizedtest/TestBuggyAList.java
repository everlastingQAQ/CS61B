package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {

    @Test
    public void testThreeAddThreeReomve () {
        AListNoResizing <Integer> expected = new AListNoResizing <>();
        BuggyAList <Integer> testing = new BuggyAList <>();

        for (int i = 4; i <= 6; i++) {
            expected.addLast(i);
            testing.addLast(i);
        }

        for (int i = 6; i >= 4; i--) {
            int expectRemove = expected.removeLast();
            int testRemove = testing.removeLast();
            assertEquals(expectRemove, testRemove);
        }
    }

    @Test
    public void randomizedTest () {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList <Integer> T = new BuggyAList <>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                T.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int sizeT = T.size();
                System.out.println("size: " + size);
                assertEquals(size, sizeT);
            }else {
                if (L.size() <= 0) continue;
                int expectRemove = L.removeLast();
                int testRemove = T.removeLast();
                assertEquals(expectRemove, testRemove);
            }
        }
    }
}
