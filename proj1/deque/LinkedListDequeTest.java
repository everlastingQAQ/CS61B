package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    int MN_VALUE = -1000000;
    int MX_VALUE = 1000001;

    public void check (LinkedList <?> expected, LinkedListDeque <?> tested) {
        assertEquals(expected.size(), tested.size());
        assertEquals(expected.isEmpty(), tested.isEmpty());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), tested.get(i));
        }
    }

    @Test
    public void randomTest () {
        LinkedList <Integer> expected = new LinkedList<>();
        LinkedListDeque <Integer> tested = new LinkedListDeque<>();

        int count = 50;
        for (int ii = 0; ii  < count; ii++) {

            int N = 5000;
            int x = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int y = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int mn = Math.min(x, y);
            int mx = Math.max(x, y);

            for (int i = 0; i < N; i++) {
                int Case = StdRandom.uniform(0, 8);
                if (Case == 0) {//Test addFirst
                    int num = StdRandom.uniform(mn, mx);
                    expected.addFirst(num);
                    tested.addFirst(num);
                    check(expected, tested);
                }else if (Case == 1) {//Test addLast
                    int num = StdRandom.uniform(mn, mx);
                    expected.addLast(num);
                    tested.addLast(num);
                    check(expected, tested);
                }else if (Case == 2) {//Test isEmpty
                    assertEquals(expected.isEmpty(), tested.isEmpty());
                }else if (Case == 3) {//Test size
                    assertEquals(expected.size(), tested.size());
                }else if (Case == 4) {//Test printDeque
                    check(expected, tested);
                }else if (Case == 5) {//Test removeFirst
                    if (expected.size() == 0) continue;
                    assertEquals(expected.removeFirst(), tested.removeFirst());
                    check(expected, tested);
                }else if (Case == 6) {
                    if (expected.size() == 0) continue;
                    assertEquals(expected.removeLast(), tested.removeLast());
                    check(expected, tested);
                }else if (Case == 7) {
                    if (expected.size() == 0) continue;
                    int index = StdRandom.uniform(0, expected.size());
                    assertEquals(expected.get(index), tested.get(index));
                    check(expected, tested);
                }
            }
        }

    }


//    @Test
//    /** Adds a few things to the list, checking isEmpty() and size() are correct,
//     * finally printing the results.
//     *
//     * && is the "and" operation. */
//    public void addIsEmptySizeTest() {
//
//        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();
//
//		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
//		lld1.addFirst("front");
//
//		// The && operator is the same as "and" in Python.
//		// It's a binary operator that returns true if both arguments true, and false otherwise.
//        assertEquals(1, lld1.size());
//        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());
//
//		lld1.addLast("middle");
//		assertEquals(2, lld1.size());
//
//		lld1.addLast("back");
//		assertEquals(3, lld1.size());
//
//		System.out.println("Printing out deque: ");
//		lld1.printDeque();
//    }
//
//    @Test
//    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
//    public void addRemoveTest() {
//
//        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
//		// should be empty
//		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());
//
//		lld1.addFirst(10);
//		// should not be empty
//		assertFalse("lld1 should contain 1 item", lld1.isEmpty());
//
//		lld1.removeFirst();
//		// should be empty
//		assertTrue("lld1 should be empty after removal", lld1.isEmpty());
//    }
//
//    @Test
//    /* Tests removing from an empty deque */
//    public void removeEmptyTest() {
//
//        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
//        lld1.addFirst(3);
//
//        lld1.removeLast();
//        lld1.removeFirst();
//        lld1.removeLast();
//        lld1.removeFirst();
//
//        int size = lld1.size();
//        String errorMsg = "  Bad size returned when removing from empty deque.\n";
//        errorMsg += "  student size() returned " + size + "\n";
//        errorMsg += "  actual size() returned 0\n";
//
//        assertEquals(errorMsg, 0, size);
//    }
//
//    @Test
//    /* Check if you can create LinkedListDeques with different parameterized types*/
//    public void multipleParamTest() {
//
//        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
//        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
//        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();
//
//        lld1.addFirst("string");
//        lld2.addFirst(3.14159);
//        lld3.addFirst(true);
//
//        String s = lld1.removeFirst();
//        double d = lld2.removeFirst();
//        boolean b = lld3.removeFirst();
//    }
//
//    @Test
//    /* check if null is return when removing from an empty LinkedListDeque. */
//    public void emptyNullReturnTest() {
//
//        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
//
//        boolean passed1 = false;
//        boolean passed2 = false;
//        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
//        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());
//    }
//
//    @Test
//    /* Add large number of elements to deque; check if order is correct. */
//    public void bigLLDequeTest() {
//
//        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
//        for (int i = 0; i < 1000000; i++) {
//            lld1.addLast(i);
//        }
//
//        for (double i = 0; i < 500000; i++) {
//            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
//        }
//
//        for (double i = 999999; i > 500000; i--) {
//            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
//        }
//
//    }
}
