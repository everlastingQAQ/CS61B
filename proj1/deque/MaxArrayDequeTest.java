package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {

    public static class IntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer x1, Integer x2) {
            return Integer.compare(x1, x2);
        }
    }

    static final int MN_VALUE = -1000000;
    static final int MX_VALUE = 1000001;
    static final int COUNT = 200;
    static final int N = 5000;

    @Test
    public void randomTest() {
        Comparator<Integer> c = new IntegerComparator();

        for (int ii = 0; ii < COUNT; ii++) {

            int x = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int y = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int mn = Math.min(x, y);
            int mx = Math.max(x, y);
            int currentMax = 0;
            MaxArrayDeque<Integer> tested = new MaxArrayDeque<>(c);

            for (int i = 0; i < N; i++) {
                int num = StdRandom.uniform(mn, mx);
                if (i == 0) {
                    currentMax = num;
                }
                currentMax = Math.max(currentMax, num);
                tested.addLast(num);
                assertEquals((int) tested.max(), currentMax);
            }
        }

    }

}
