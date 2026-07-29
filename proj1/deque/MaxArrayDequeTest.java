package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MaxArrayDequeTest {

    public static class IntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer x1, Integer x2) {
            return Integer.compare(x1, x2);
        }
    }

    int MN_VALUE = -1000000;
    int MX_VALUE = 1000001;

    @Test
    public void randomTest () {
        Comparator<Integer> c = new IntegerComparator();

        int count = 10;
        for (int ii = 0; ii < count; ii++) {

            int N = 5000;
            int x = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int y = StdRandom.uniform(MN_VALUE, MX_VALUE);
            int mn = Math.min(x, y);
            int mx = Math.max(x, y);
            int currentMax = 0;
            MaxArrayDeque <Integer> tested = new MaxArrayDeque<>(c);

            for (int i = 0; i < N; i++) {
                int num = StdRandom.uniform(mn, mx);
                if (i == 0) {
                    currentMax = num;
                }
                currentMax = Math.max(currentMax, num);
                tested.addLast(num);
                assertEquals((int)tested.max(), currentMax);
            }
        }

    }

}
