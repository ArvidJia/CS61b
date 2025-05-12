package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V> {
    private class BSTNode {
        K key;
        V val;
        BSTNode left;
        BSTNode right;
        BSTNode(K key, V val) {
            this.key = key;
            this.val = val;
            left = right = null;
        }

        /**
         * get the largest node
         * @return largest node of BST
         */
        BSTNode largest() {
            if (right == null) {
                return this;
            } else {
                return right.largest();
            }
        }

        /**
         * get the smallest node
         * @return smallest node of BST
         */
        BSTNode smallest() {
            if (left == null) {
                return this;
            } else {
                return left.smallest();
            }
        }
    }

    private BSTNode root;
    private int size = 0;

    public BSTMap() {
        root = null;
    }

   @Override
    public void put(K key, V val) {
        root = insert(root, key, val);
    }

    private BSTNode insert(BSTNode node, K key,V val) {
        if (node == null) {
            size += 1;
            return new BSTNode(key, val);
        } else if (key.compareTo(node.key) < 0) {
            node.left = insert(node.left, key, val);
        } else if (key.compareTo(node.key) > 0) {
            node.right = insert(node.right, key, val);
        }
        return node;
    }

    @Override
    public boolean containsKey(K key) {
        return find(root, key) != null;
    }

    private BSTNode find(BSTNode node, K key) {
        if (node == null) {
            return null;
        } else if (node.key.compareTo(key) == 0) {
            return node;
        } else if (node.key.compareTo(key) < 0) {
            return find(node.right, key);
        } else if (node.key.compareTo(key) > 0) {
            return find(node.left, key);
        }
        return null;
    }


    @Override
    public void clear() {
        size = 0;
        root = null;
    }


    @Override
    public V get(K key) {
        BSTNode node = find(root, key);
        return node == null ? null : node.val;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        BSTNode node = find(root, key);
        if (node == null) { //No such item
            return null;
        }
        V val = node.val;
        node.val = null;
        return val;
    }

   @Override
    public V remove(K key, V val) {
        root = remove(root, key); // root deleted here;
        return val;
    }

    private BSTNode remove (BSTNode node, K key) {
        // Base case: return successor || return null when not found
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            node.right = remove(node.right, key); // Child 0/1 deleted here
        } else if (cmp < 0) {
            node.left = remove(node.left, key);
        } else {
            // return successor when child = 0/1;
            if (node.left == null) {
                return node.right;
            } else if (node.right == null){
                return node.left;
            } else { // 2 Child: delete node and return deleted one
                BSTNode successor = node.left.largest();
                node.key = successor.key;
                node.val = successor.val;
                remove(successor, node.key);
            }
        }

        // Base case: just return the Tree after deleting
        return node;
    }


    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

}
