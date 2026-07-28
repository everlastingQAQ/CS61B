package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {


    int MN_VALUE = -1000000;
    int MX_VALUE = 1000001;

    public void check (LinkedList <?> expected, ArrayDeque <?> tested) {
        assertEquals(expected.size(), tested.size());
        assertEquals(expected.isEmpty(), tested.isEmpty());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), tested.get(i));
        }
    }

    @Test
    public void randomTest () {
        LinkedList <Integer> expected = new LinkedList<>();
        ArrayDeque <Integer> tested = new ArrayDeque<>();

        int count = 5;
        for (int ii = 0; ii  < count; ii++) {

            int N = 50;
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
//    public void addTest () {
//        ArrayDeque <Integer> tested = new ArrayDeque<>();
//        LinkedList <Integer> expected = new LinkedList<>();
//
//        for (int i = 0; i < 8; i++) {
//            tested.addFirst(i);
//            expected.addFirst(i);
//        }
//
//        tested.addLast(8);
//        tested.addLast(9);
//        tested.addFirst(10);
//        tested.printDeque();
//        for (int i = 0; i < 5; i++) {
//            tested.removeFirst();
//            tested.printDeque();
//            for (int j = 0; j < tested.size(); j++) {
//                System.out.print(tested.get(j) + " ");
//            }
//            System.out.println();
//        }
//    }

}
