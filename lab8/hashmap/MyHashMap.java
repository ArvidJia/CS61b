package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Arvid Jia
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

        @Override
        public boolean equals(Object o) {
            return o.hashCode() == key.hashCode();
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    int N = 0; // # items
    int M = 16; // # buckets
    double loadFactor = 0.75;
    double minFactor = 0.25;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(M);
    }

    public MyHashMap(int initialSize) {
        M = initialSize;
        buckets = createTable(M);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        M = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(M);
    }


    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        Node node = new Node(key, value);
        return node;
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
        Collection<Node> bucket = new ArrayList<Node>();
        return bucket;
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
        Collection<Node>[] table = (Collection<Node> []) new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        N = 0;
        M = 16;
        buckets = createTable(M);
    }

    @Override
    public void put(K key, V value) {
        if ( N >= loadFactor * M ) {
            buckets = resize(2 * M);
        }

        if (containsKey(key)) {
            changeKey(key, value);
        } else {
            Node node = createNode(key, value);
            buckets[bucketIndex(key)].add(node);
            N++;
        }
    }

    private int bucketIndex(K key) {
        int hash = key.hashCode();
        int mod = hash % M;
        return (mod < 0 ? (M + mod) : mod);
    }

    private Collection<Node>[] resize(int newSize) {
         Collection<Node> [] newTable = createTable(newSize);
         for (int i = 0; i < M; i++) {
             for (Node item : buckets[i] ) {
                 newTable[bucketIndex(item.key)].add(item);
             }
         }
         M = newSize;
         return newTable;
    }

    private V changeKey(K key, V value) {
        for (Node item : buckets[bucketIndex(key)]) {
            if (item.key.equals(key)) {
                V oldValue = item.value;
                item.value = value;
                return oldValue;
            }
        }
        return null;
    }

    @Override
    public V get(K key) {
        for (Node item : buckets[bucketIndex(key)]) {
            if (item.key.equals(key)) {
                return item.value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key) {
        if (M > 16 && N <= minFactor * M) {
            buckets = resize(M /2 );
        }

        Node wrapper = new Node(key, null);
        if (removeHelper(wrapper)) {
            N--;
        }
        return wrapper.value;
    }

    @Override
    public V remove(K key, V value) {
        if (M > 16 && N <= minFactor * M) {
            buckets = resize(M /2 );
        }

        Node wrapper = new Node(key, value);
        if (removeHelper(wrapper)) {
            N--;
        }
        return wrapper.value;
    }

    private boolean removeHelper(Node wrapper) {
        V val = get(wrapper.key);
        if (val == null) {
            return false;
        }

        int index = bucketIndex(wrapper.key);
        if (wrapper.value == null || wrapper.value == val) {
            wrapper.value = val;
            return buckets[index].remove(wrapper);
        }
        wrapper.value = null;
        return false;
    }


    @Override
    public int size() {
        return N;
    }

    @Override
    public boolean containsKey(K key) {
        for (Node item : buckets[bucketIndex(key)]) {
            if (item.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<K>();
        for (int i = 0; i < M; i++) {
            for (Node item : buckets[i]) {
                keySet.add(item.key);
            }
        }
        return keySet;
    }

    @Override
    public Iterator<K> iterator() {
        return new HashIterator();
    }

    private class HashIterator implements Iterator<K> {
        int index = 0;
        int mIndex = 0;
        Iterator<Node> iter;
        public HashIterator() {
            iter = buckets[mIndex].iterator();
        }

        public boolean hasNext() {
            return index < N;
        }

        public K next() {
            if (hasNext()) {
                index++;
                if (iter.hasNext()) {
                    return iter.next().key;
                } else {
                    mIndex++;
                    iter = buckets[mIndex].iterator();
                    return iter.next().key;
                }
            }
            return null;
        }
    }
}
