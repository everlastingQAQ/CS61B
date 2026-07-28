package deque;

public class LinkedListDeque <T> implements Deque <T>{

    public class Node {

        public T item;
        public Node prev;
        public Node next;

        public Node (T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }

        public Node (Node node) {
            this.item = node.item;
            this.prev = node.prev;
            this.next = node.next;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque () {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst (T item) {
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
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque () {
        int size = this.size;
        Node x = sentinel.next;
        while (size != 0) {
            System.out.print(x.item);
            System.out.print(" ");
            x = x.next;
            size = size - 1;
        }
        System.out.print('\n');
    }

    @Override
    public T removeFirst () {
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
    public T removeLast () {
        if (size == 0) {
            return null;
        }
        size = size - 1;
        Node last = sentinel.prev;
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        return last.item;
    }

    public boolean validIndex (int index) {
        if (size - 1 < index || index < 0) return false;
        return true;
    }

    @Override
    public T get (int index) {
        if (!validIndex(index)) return null;
        Node node = new Node(sentinel.next);
        while (index != 0) {
            node = node.next;
            index = index - 1;
        }
        return node.item;
    }

    public T get_Recursive (Node node, int index) {
        if (index == 0) {
            return node.item;
        }
        return get_Recursive(node.next, index - 1);
    }

    public T getRecursive (int index) {
        if (!validIndex(index)) return null;
        return get_Recursive(sentinel.next, index);
    }
}
