package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private static class BSTNode<K, V> {
        public K nodeKey;
        public V nodeValue;
        public BSTNode<K, V> leftNode;
        public BSTNode<K, V> rightNode;

        public BSTNode() {
            leftNode = null;
            rightNode = null;
        }
        public BSTNode(K key, V value) {
            nodeKey = key;
            nodeValue = value;
            leftNode = null;
            rightNode = null;
        }
    }

    private BSTNode<K, V> sentinel;
    private int size = 0;


    @Override
    public void clear() {
        sentinel = null;
        size = 0;

    }

    @Override
    public boolean containsKey(K key) {
        BSTNode<K, V> pointNode = sentinel;
        while (pointNode != null) {
            int compareNum = pointNode.nodeKey.compareTo(key);
            if (compareNum == 0) {
                return true;
            } else if(compareNum > 0) {
                pointNode = pointNode.leftNode;
            } else {
                pointNode = pointNode.rightNode;
            }
        }
        return false;
    }
    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        };
        return getRecursion(key, sentinel);
    }

    private V getRecursion(K key, BSTNode<K, V> P) {
        if (key.compareTo(P.nodeKey) == 0) {
            return P.nodeValue;
        } else if (key.compareTo(P.nodeKey) > 0) {
            return getRecursion(key, P.rightNode);
        } else {
            return getRecursion(key, P.leftNode);
        }
    }
    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (containsKey(key)) {
            return;
        }
        else {
            size++;
            sentinel = insert(sentinel, key, value);
        }
    }

    private BSTNode<K, V> insert(BSTNode<K, V> T, K key, V value) {
        if (T == null) {
            return new BSTNode<>(key, value);
        } else if(T.nodeKey.compareTo(key) > 0) {
            T.leftNode = insert(T.leftNode, key, value);
        } else {
            T.rightNode = insert(T.rightNode, key, value);
        }
        return T;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
    public void printInOrder() {

    }
    private void printByrecursion(BSTNode<K, V> node) {
        if (node == null) {
            return;
        } else {
            System.out.print("key:%s" + node.nodeKey.toString());
            printByrecursion(node.leftNode);
            printByrecursion(node.rightNode);
        }

    }
}
