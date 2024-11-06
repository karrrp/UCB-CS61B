package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    @Override
    public void clear() {

    }

    @Override
    public boolean containsKey(K key) {
        int keyIndex = hash(key);
        for (Node node : buckets[keyIndex]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
      int keyIndex = hash(key);
      for (Node node : buckets[keyIndex]) {
          if (node.key.equals(key)) {
              return node.value;
          }
      }
      return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        size++;
        /*resize*/
        if (size * 1.0 / hashmapLength > loadFactor) {
            hashmapLength *= 2;
            Collection<Node>[] temp = createTable(hashmapLength);
            for (int i = 0; i < hashmapLength / 2; i++) {
                for (Node node : buckets[i]) {
                    int nodeIndex = hash(node.key);
                    temp[nodeIndex].add(node);
                }
            }
            buckets = temp;
        }
        if (containsKey(key)) {
            return;
        }
        Node newNode = createNode(key, value);
        int keyIndex = hash(key);
        buckets[keyIndex].add(newNode);
    }

    private int hash(K key) {
        return Math.floorMod(key.hashCode(), hashmapLength);
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> keySet = new HashSet<>();
        for (int i = 0; i < hashmapLength / 2; i++) {
            for (Node node : buckets[i]) {
                keySet.add(node.key);
            }
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        throw new RuntimeException();
    }

    @Override
    public V remove(K key, V value) {
        throw new RuntimeException();
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }
    private class MyHashMapIterator implements Iterator<K> {
        int cup;
        int bucketIndex = 0;
        Iterator<Node> bucketIterator;

        public MyHashMapIterator() {
            cup = 0;
            bucketIndex = 0;
            bucketIterator = buckets[bucketIndex].iterator();
        }

        @Override
        public boolean hasNext() {
            return cup < size;
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new IndexOutOfBoundsException();
            }
            if (!bucketIterator.hasNext()) {
                do {
                    bucketIndex++;
                } while (buckets[bucketIndex].isEmpty());
                bucketIterator = buckets[bucketIndex].iterator();
            }
            cup++;
            return bucketIterator.next().key;
        }
    }

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
        public Collection<Node>[] buckets;
        // You should probably define some more!
        private int size;
        private double loadFactor = .75;
        private int hashmapLength = 16;

        /**
         * Constructors
         */
        public MyHashMap() {
            size = 0;
            buckets = createTable(hashmapLength);
        }

        public MyHashMap(int initialSize) {
            size = 0;
            hashmapLength = initialSize;
            buckets = createTable(initialSize);
        }

        /**
         * MyHashMap constructor that creates a backing array of initialSize.
         * The load factor (# items / # buckets) should always be <= loadFactor
         *
         * @param initialSize initial size of backing array
         * @param maxLoad     maximum load factor
         */
        public MyHashMap(int initialSize, double maxLoad) {
            size = 0;
            buckets = createTable(initialSize);
            loadFactor = maxLoad;
        }

        /**
         * Returns a new node to be placed in a hash table bucket
         */
        private Node createNode(K key, V value) {
            return new Node(key, value);
        }

        /**
         * Returns a data structure to be a hash table bucket
         * <p>
         * The only requirements of a hash table bucket are that we can:
         * 1. Insert items (`add` method)
         * 2. Remove items (`remove` method)
         * 3. Iterate through items (`iterator` method)
         * <p>
         * Each of these methods is supported by java.util.Collection,
         * Most data structures in Java inherit from Collection, so we
         * can use almost any data structure as our buckets.
         * <p>
         * Override this method to use different data structures as
         * the underlying bucket type
         * <p>
         * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
         * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
         */
        protected Collection<Node> createBucket() {
            return new LinkedList<>();
        }

        /**
         * Returns a table to back our hash table. As per the comment
         * above, this table can be an array of Collection objects
         * <p>
         * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
         * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
         *
         * @param tableSize the size of the table to create
         */
        private Collection<Node>[] createTable(int tableSize) {
            Collection[] newTable = new Collection[tableSize];
            for (int i = 0; i < tableSize; i++) {
                newTable[i] = this.createBucket();
            }
            return newTable;
        }
        // TODO: Implement the methods of the Map61B Interface below
        // Your code won't compile until you do so!
    }
