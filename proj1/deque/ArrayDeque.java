package deque;

/*
    0 0 0 0 0 0 0 0 first = 0, last = 0 -> addFirst 1
    0 0 0 0 0 0 0 1 first = 0, last = 0 -> addFirst 2
    2 0 0 0 0 0 0 1 first = 7, last = 0 -> addLast 3
    2 3 0 0 0 0 0 1 first = 7, last = 1 -> removeFirst
    2 3 0 0 0 0 0 0 first = 0, last = 1 -> removeLast
    2 0 0 0 0 0 0 0
* */

import java.util.Iterator;
import java.util.Objects;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int aSize;
    private int first;
    private int last;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        aSize = 8;
        first = 0;
        last = 0;
    }

    private void resize(int newSize) {
        T[] newItems = (T[]) new Object[newSize];
        int newIndex = 0;

        int sizee = this.size;

        for (int index = first; sizee != 0; index = (index + 1) % aSize) {
            newItems[newIndex] = items[index];
            newIndex += 1;
            sizee -= 1;
            if (sizee == 0) {
                break;
            }
        }

        items = newItems;
        first = 0;
        last = this.size - 1;
        aSize = newSize;

    }

    @Override
    public void addFirst(T item) {
        if (size == aSize) {
            resize(aSize * 2);
        }

        if (size == 0) {
            first = 0;
            last = 0;
        } else {
            first = (first - 1 + aSize) % aSize;
        }

        items[first] = item;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == aSize) {
            resize(aSize * 2);
        }

        if (size == 0) {
            first = 0;
            last = 0;
        } else {
            last = (last + 1) % aSize;
        }

        items[last] = item;
        size += 1;
    }

    @Override
    public void printDeque() {
        int sizee = size;
        for (int index = first; sizee != 0; index = (index + 1) % aSize) {
            System.out.print(items[index] + " ");
            sizee -= 1;
            if (sizee == 0) {
                break;
            }
        }
        System.out.println();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T x = items[first];
        items[first] = null;
        size -= 1;

        if (size == 0) {
            first = 0;
            last = 0;
        } else {
            first = (first + 1) % aSize;
        }

        if (aSize > 8 && 2 * size < aSize) {
            resize(aSize / 2);
        }

        return x;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T x = items[last];
        items[last] = null;
        size -= 1;

        if (size == 0) {
            first = 0;
            last = 0;
        } else {
            last = (last - 1 + aSize) % aSize;
        }

        if (aSize > 8 && 2 * size < aSize) {
            resize(aSize / 2);
        }

        return x;
    }

    private boolean validIndex(int index) {
        if (size - 1 < index || index < 0) {
            return false;
        }
        return true;
    }

    @Override
    public T get(int index) {
        if (!validIndex(index)) {
            return null;
        }
        int trueIndex = (first + index + aSize) % aSize;
        return items[trueIndex];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;
        private int sz;

        private ArrayDequeIterator() {
            pos = first;
            sz = 0;
        }

        @Override
        public boolean hasNext() {
            return sz < size;
        }

        @Override
        public T next() {
            T returnItem = items[pos];
            pos = (pos + 1) % aSize;
            sz += 1;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }

        Deque<T> O = (Deque<T>) o;
        if (O.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < size(); i++) {
            if (!Objects.equals(O.get(i), this.get(i))) {
                return false;
            }
        }
        return true;
    }
}
