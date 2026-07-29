package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T> {

    private class Node {

        private T item;
        private Node prev;
        private Node next;

        private Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node firstOld = sentinel.next;
        Node firstNew = new Node(item, sentinel, firstOld);
        firstOld.prev = firstNew;
        sentinel.next = firstNew;
        size = size + 1;
    }

    @Override
    public void addLast(T item) {
        Node lastOld = sentinel.prev;
        Node lastNew = new Node(item, lastOld, sentinel);
        lastOld.next = lastNew;
        sentinel.prev = lastNew;
        size = size + 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int newSize = this.size;
        Node x = sentinel.next;
        while (newSize != 0) {
            System.out.print(x.item);
            System.out.print(" ");
            x = x.next;
            newSize = newSize - 1;
        }
        System.out.print('\n');
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size = size - 1;
        Node first = sentinel.next;
        sentinel.next = first.next;
        first.next.prev = sentinel;
        return first.item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size = size - 1;
        Node last = sentinel.prev;
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        return last.item;
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
        Node node = sentinel.next;
        while (index != 0) {
            node = node.next;
            index = index - 1;
        }
        return node.item;
    }

    private T gettingRecursive(Node node, int index) {
        if (index == 0) {
            return node.item;
        }
        return gettingRecursive(node.next, index - 1);
    }

    public T getRecursive(int index) {
        if (!validIndex(index)) {
            return null;
        }
        return gettingRecursive(sentinel.next, index);
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node node;

        private LinkedListDequeIterator() {
            node = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return node != sentinel;
        }

        @Override
        public T next() {
            T returnItem = node.item;
            node = node.next;
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
