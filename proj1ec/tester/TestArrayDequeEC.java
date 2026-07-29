package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    static final int MN_VALUE = -1000000;
    static final int MX_VALUE = 1000001;
    static final int COUNT = 20;
    static final int N = 5000;
    static final int ZERO = 0;
    static final int FOUR = 4;

    @Test
    public void randomTest() {
        for (int ii = 0; ii  < COUNT; ii++) {

            ArrayDequeSolution<Integer> expected = new ArrayDequeSolution<>();
            StudentArrayDeque<Integer> tested = new StudentArrayDeque<>();
            int x = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int y = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int mn = Math.min(x, y);
            int mx = Math.max(x, y);

            StringBuilder message = new StringBuilder();

            for (int i = 0; i < N; i++) {
                int uniform = StdRandom.uniform(ZERO, FOUR);
                if (uniform == 0) { //Test addFirst
                    int num = StdRandom.uniform(mn, mx);
                    expected.addFirst(num);
                    tested.addFirst(num);
                    message.append("addFirst(" + num + ")\n");
                } else if (uniform == 1) { //Test addLast
                    int num = StdRandom.uniform(mn, mx);
                    expected.addLast(num);
                    tested.addLast(num);
                    message.append("addLast(" + num + ")\n");
                } else if (uniform == 2) { //Test removeFirst
                    if (expected.isEmpty()) {
                        continue;
                    }
                    message.append("removeFirst()\n");
                    assertEquals(message.toString(), expected.removeFirst(), tested.removeFirst());
                } else if (uniform == 3) { //Test removeLast
                    if (expected.isEmpty()) {
                        continue;
                    }
                    message.append("removeLast()\n");
                    assertEquals(message.toString(), expected.removeLast(), tested.removeLast());
                }
            }
        }

    }
}
