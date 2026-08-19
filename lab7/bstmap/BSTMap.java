package bstmap;

import java.util.HashSet;
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

    public void printInOrder() {
        BSTNode cur = root;
        if (cur != null) {
            dfsPrint(cur);
        }
    }

    private void dfsPrint(BSTNode cur) {
        if (cur.left != null) {
            dfsPrint(cur.left);
        }
        System.out.println(cur.key + " -> " + cur.value);
        if (cur.right != null) {
            dfsPrint(cur.right);
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        getKeys(root, keys);
        return keys;
    }

    private void getKeys(BSTNode cur, Set<K> keys) {
        if (cur == null) {
            return;
        }
        getKeys(cur.left, keys);
        keys.add(cur.key);
        getKeys(cur.right, keys);
    }

    public V remove(K key) {
        BSTNode tar = findNode(key);
        if (tar == null) {
            return null;
        }
        V rmValue = tar.value;
        root = rmNode(root, key);
        size--;
        return rmValue;
    }

    public V remove(K key, V value) {
        BSTNode tar = findNode(key);
        if (tar == null) {
            return null;
        }
        if (value.equals(tar.value)) {
            return null;
        }
        return remove(key);
    }

    private BSTNode rmNode(BSTNode cur, K key) {
        if (cur == null) {
            return null;
        }
        int cmp = key.compareTo(cur.key);

        if (cmp < 0) {
            cur.left = rmNode(cur.left, key);
        } else if (cmp > 0) {
            cur.right = rmNode(cur.right, key);
        } else {
            if (cur.left == null) {
                return cur.right;
            }
            if (cur.right == null) {
                return cur.left;
            }

            BSTNode newCur = findMin(cur.right);
            cur.key = newCur.key;
            cur.value = newCur.value;
            cur.right = rmNode(cur.right, newCur.key);
        }

        return cur;
    }

    private BSTNode findMin(BSTNode cur) {
        while (cur.left != null) {
            cur = cur.left;
        }
        return cur;
    }

}
