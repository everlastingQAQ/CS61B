package deque;

/*
    0 0 0 0 0 0 0 0 first = 0, last = 0 -> addFirst 1
    0 0 0 0 0 0 0 1 first = 0, last = 0 -> addFirst 2
    2 0 0 0 0 0 0 1 first = 7, last = 0 -> addLast 3
    2 3 0 0 0 0 0 1 first = 7, last = 1 -> removeFirst
    2 3 0 0 0 0 0 0 first = 0, last = 1 -> removeLast
    2 0 0 0 0 0 0 0
* */

public class ArrayDeque <T> {
    private T[] items;
    private int size;
    public int Asize;
    private int first;
    private int last;

    public ArrayDeque () {
        items = (T[]) new Object[8];
        size = 0;
        Asize = 8;
        first = 8;
        last = -1;
    }

    public void resize (int size) {
        T[] newItems = (T[]) new Object[size];
        int newIndex = 0;

        int sizee = this.size;

        for (int index = first; ; index = (index + 1) % Asize) {
            newItems[newIndex] = items[index];
            newIndex += 1;
            sizee -= 1;
            if (sizee == 0) break;
        }

        items = newItems;
        first = 0;
        last = this.size - 1;
        Asize = size;

    }

    public void addFirst (T item) {
        if (size == Asize) {
            resize(size * 2);
        }
        first = (first - 1 + Asize) % Asize;
        items[first] = item;
        size += 1;
    }

    public void addLast (T item) {
        if (size == Asize) {
            resize(size * 2);
        }
        last = last + 1;
        items[last] = item;
        size += 1;
    }

    public void printDeque () {
        int sizee = size;
        for (int index = first; ; index = (index + 1) % Asize) {
            System.out.print(items[index] + " ");
            sizee -= 1;
            if (sizee == 0) break;
        }
        System.out.println();
    }

    public boolean isEmpty () {
        return size == 0;
    }

    public int size () {
        return size;
    }

    public T removeFirst () {
        if (size == 0) return null;
        if ((size - 1) * 2 < Asize) {
            resize(Asize / 2);
        }
        T x = items[first];

        items[first] = null;
        first = (first + 1) % Asize;
        size = size - 1;
        return x;
    }

    public T removeLast () {
        if (size == 0) return null;
        T x = items[last];

        items[last] = null;
        last = (last - 1) % Asize;
        size = size - 1;

        return x;
    }

    public T get (int index) {
        int trueIndex = (first + index + Asize) % Asize;
        return items[trueIndex];
    }
}
