package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable,V> implements Map61B<K,V> {
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


        BSTNode largest() {
            if (right == null) {
                return this;
            } else {
                return right.largest();
            }
        }

        BSTNode smallest() {
            if (left == null) {
                return this;
            } else {
                return left.smallest();
            }
        }


//        BSTNode remove(BSTNode node){
//            int child = node.numOfChild();
//            BSTNode delete = node;
//            if (child == 0) {
//                node = null;
//                return delete;
//            } else if (child == 1) {
//                node = node.left == null ? node.right : node.left;
//                return delete;
//            } else {
//                BSTNode successer = remove(node.largest());
//                node = successer;
//                return delete;
//            }
//        }

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
        if (parent == null) {
            if (root.key.compareTo(key) == 0) {
                BSTNode successor = root.left.largest();
                K skey = successor.key;
                V sval = successor.val;
                remove(skey, sval);
                root.key = skey;
                root.val = sval;
                return sval;
            }
            return null;
        }

        BSTNode node = find(parent, key);
        V val = node.val;
        int child = node.numOfChild();
        if (node == parent.left) {
            BSTNode successor = successor(node);
            if (successor == null) {
                parent.left = null;
            } else if (successor == node.right || successor == node.left) {
                parent.left = successor;
            } else {
                K skey = successor.key;
                V sval = successor.val;
                remove(skey, sval);
                node.key = skey;
                node.val = sval;
            }
        } else {
            BSTNode successor = successor(node);
            if (successor == null) {
                parent.right = null;
            } else if (successor == node.right || successor == node.left) {
                parent.right = successor;
            } else {
                K skey = successor.key;
                V sval = successor.val;
                remove(skey, sval);
                node.key = skey;
                node.val = sval;
            }
        }
        return val;
    }

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





    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
