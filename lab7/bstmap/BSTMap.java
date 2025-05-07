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

        int numOfChild(){
            int child = 2;
            if (left == null) {
                child--;
            }
            if (right == null) {
                child--;
            }
            return child;
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
        if (root == null) {
            root = new BSTNode(key, val);
            size += 1;
        }
        insert(root, key, val);
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

    /**
     * find parent node of node passed in
     * @param node node you want to findParent
     * @param key  key of the node
     * @return parent node of @param node
     */
    private BSTNode findParent(BSTNode node, K key) {
        if (node == null) {
            return null;
        } else if (node.left != null && node.left.key.compareTo(key) == 0) {
            return node;
        } else if (node.right != null && node.right.key.compareTo(key) == 0) {
            return node;
        } else if (node.key.compareTo(key) < 0) {
            node = findParent(node.right, key);
        } else if (node.key.compareTo(key) > 0) {
            node = findParent(node.left, key);
        }
        return node;
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
    public V remove(K key, V value) {
        BSTNode parent = findParent(root, key);
        if (parent == null && key.compareTo(root.key) != 0) {
            return null;
        }

        BSTNode node = key.compareTo(root.key) == 0 ? root : find(parent, key);
        V val = node.val;
        int child = node.numOfChild();
        BSTNode successor = successor(node);

        /*
         * if the childNum == 2: change origin key:val -> successor.key/val.
           then delete the successor
         * else childNum == 1/0: change the node to be successor
           (as the form of parent.left/right)
         * if root & child != 2: change root to be successor
        */
        if (child == 2) {
            K skey = successor.key;
            V sval = successor.val;
            remove(skey, sval);
            node.key = skey;
            node.val = sval;
        } else if (node == root) {
           root = successor;
        } else if (node == parent.left) {
            parent.left = successor;
        } else if (node == parent.right) {
            parent.right = successor;
        }
        return val;
    }

    /**
     * find a successor for the node pass in.
     * depends on how many child it has.
     * @param node the node you want to delete
     * @return the successor when you delete the node
     */
    private BSTNode successor(BSTNode node) {
        int n = node.numOfChild();
        if (n == 0) {
            return null;
        } else if (n == 1) {
            return node.left == null ? node.right : node.left;
        }
        else { //n == 2
            return node.left.largest();
        }
    }

    public V beautifulRemove(K key, V val) {
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
