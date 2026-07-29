package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    static final int MN_VALUE = -1000000;
    static final int MX_VALUE = 1000001;
    static final int COUNT = 200;
    static final int N = 5000;
    static final int ZERO = 0;
    static final int TEN = 10;

    public void check(LinkedList<?> expected, LinkedListDeque<?> tested) {
        assertEquals(expected.size(), tested.size());
        assertEquals(expected.isEmpty(), tested.isEmpty());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), tested.get(i));
        }
    }

    @Test
    public void randomTest() {
        LinkedList<Integer> expected = new LinkedList<>();
        LinkedListDeque<Integer> tested = new LinkedListDeque<>();

        for (int ii = 0; ii < COUNT; ii++) {

            int x = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int y = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int mn = Math.min(x, y);
            int mx = Math.max(x, y);

            for (int i = 0; i < N; i++) {
                int uniform = StdRandom.uniform(ZERO, TEN);
                if (uniform == 0) { //Test addFirst
                    int num = StdRandom.uniform(mn, mx);
                    expected.addFirst(num);
                    tested.addFirst(num);
                    check(expected, tested);
                } else if (uniform == 1) { //Test addLast
                    int num = StdRandom.uniform(mn, mx);
                    expected.addLast(num);
                    tested.addLast(num);
                    check(expected, tested);
                } else if (uniform == 2) { //Test isEmpty
                    assertEquals(expected.isEmpty(), tested.isEmpty());
                } else if (uniform == 3) { //Test size
                    assertEquals(expected.size(), tested.size());
                } else if (uniform == 4) { //Test printDeque
                    check(expected, tested);
                } else if (uniform == 5) { //Test removeFirst
                    if (expected.size() == 0) {
                        continue;
                    }
                    assertEquals(expected.removeFirst(), tested.removeFirst());
                    check(expected, tested);
                } else if (uniform == 6) {
                    if (expected.size() == 0) {
                        continue;
                    }
                    assertEquals(expected.removeLast(), tested.removeLast());
                    check(expected, tested);
                } else if (uniform == 7) {
                    if (expected.size() == 0) {
                        continue;
                    }
                    int index = StdRandom.uniform(0, expected.size());
                    assertEquals(expected.get(index), tested.get(index));
                    check(expected, tested);
                } else if (uniform == 8) {
                    Iterator<Integer> iter = expected.iterator();
                    for (Integer item : tested) {
                        if (!item.equals(iter.next())) {
                            throw new IllegalArgumentException("Failed In Iterator");
                        }
                    }
                    check(expected, tested);
                } else if (uniform == 9) {
                    LinkedListDeque<Integer> expectedDeque = new LinkedListDeque<>();
                    for (Integer item : expected) {
                        expectedDeque.addLast(item);
                    }
                    assertTrue(tested.equals(expectedDeque));
                    check(expected, tested);
                }
            }
        }

    }
}
