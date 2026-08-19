package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V>
        implements Map61B<K, V> {

    @Override
    public Iterator<K> iterator() {
        return null;
    }

    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;
        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private BSTNode root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    private BSTNode findNode(K key) {
        BSTNode cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) {
                cur = cur.left;
            } else if (cmp > 0) {
                cur = cur.right;
            } else {
                return cur;
            }
        }
        return null;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    public boolean containsKey(K key) {
        if (findNode(key) != null) {
            return true;
        }
        return false;
    }

    public V get(K key) {
        BSTNode node = findNode(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    public int size() {
        return size;
    }

    public void put(K key, V value) {
        if (root == null) {
            root = new BSTNode(key, value);
            size++;
            return;
        }

        BSTNode cur = root;
        while (true) {
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) {
                if (cur.left == null) {
                    cur.left = new BSTNode(key, value);
                    size++;
                    return;
                }
                cur = cur.left;
            } else if (cmp > 0) {
                if (cur.right == null) {
                    cur.right = new BSTNode(key, value);
                    size++;
                    return;
                }
                cur = cur.right;
            } else {
                cur.value = value;
                return;
            }
        }
    }

    @Override
    public Set<K> keySet() {
        return Set.of();
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }

}
