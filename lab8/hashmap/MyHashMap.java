package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Everlasting
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private final double maxLoad;
    private int size;

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.maxLoad = maxLoad;
        size = 0;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    private Node getNode(K key) {
        int index = getIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    private int getIndex(K key) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }

    private void resize() {
        if ((double) size / buckets.length <= maxLoad) {
            return;
        }

        int newSize = buckets.length * 2;
        while ((double) size / newSize > maxLoad) {
            newSize *= 2;
        }

        Collection<Node>[] oldBuckets = buckets;
        buckets = createTable(newSize);
        for (Collection<Node> bucket : oldBuckets) {
            for (Node node : bucket) {
                int index = getIndex(node.key);
                buckets[index].add(node);
            }
        }
    }

    @Override
    public void clear() {
        buckets = createTable(buckets.length);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        Node node = getNode(key);
        if (node == null) {
            return false;
        }
        return true;
    }

    @Override
    public V get(K key) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        Node node = getNode(key);
        if (node != null) {
            node.value = value;
            return;
        }
        int index = getIndex(key);
        buckets[index].add(createNode(key, value));
        size++;
        resize();
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                set.add(node.key);
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        int index = getIndex(key);
        buckets[index].remove(node);
        size--;
        return node.value;
    }

    @Override
    public V remove(K key, V value) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        if (!node.value.equals(value)) {
            return null;
        }
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

}
