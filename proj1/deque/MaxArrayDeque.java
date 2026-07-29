package deque;

import java.util.Comparator;

public class MaxArrayDeque <T> extends ArrayDeque <T> {

    Comparator <T> c;

    public MaxArrayDeque (Comparator <T> c) {
        super();
        this.c = c;
    }

    public T max () {
        return max(c);
    }

    public T max (Comparator <T> c) {
        if (super.isEmpty()) {
            return null;
        }
        T returnItem = super.get(0);
        for (T item : this) {
            if (c.compare(item, returnItem) > 0) {
                returnItem = item;
            }
        }
        return returnItem;
    }
}
