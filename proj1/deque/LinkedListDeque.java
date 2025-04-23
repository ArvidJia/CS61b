package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node<T> {
        Node<T> prev;
        T item;
        Node<T> next;

        Node(Node<T> prev, T item, Node<T> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
            this.restructe();
        }

        /**
         * restructe the relationship between nodes
         */
        private void restructe() {
            if (this.next != null) {
                this.next.prev = this;
            }
            if (this.prev != null) {
                this.prev.next = this;
            }
        }
    }

    private Node<T> frontSentinel = new Node<T>(null, null, null);
    private Node<T> backSentinel = new Node<T>(frontSentinel, null, frontSentinel);
    private int size = 0;

    public LinkedListDeque() {
        frontSentinel.next = backSentinel;
        frontSentinel.prev = backSentinel;
    };

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(T item) {
        frontSentinel.next = new Node<T>(frontSentinel, item, frontSentinel.next);
        ++size;
    }

    @Override
    public void addLast(T item) {
        backSentinel.prev = new Node<T>(backSentinel.prev, item, backSentinel);
        size++;
    }

    @Override
    public T get(int i) {
        if (i < size / 2 && i >= 0) {
            Node<T> temp = frontSentinel.next;
            while (i > 0) {
                temp = temp.next;
                i--;
            }
            return temp.item;
        } else {
            Node<T> temp = backSentinel.prev;
            i = size - i - 1;
            while (i > 0) {
                temp = temp.prev;
                i--;
            }
            return temp.item;
        }
    }

    /**
     * @param node the first effective node of Deque
     * @param index index of elem want to get
     * @return the elem at position index
     */
    private T getRrcursiveHelper(Node<T> node, int index) {
        if (index > 0) {
            return getRrcursiveHelper(node.next, index - 1);
        }
        return node.item;
    }

    public T getRecursive(int i) {
        if (i >= size || i < 0) {
            System.out.println("Out of bounds");
        }
        return getRrcursiveHelper(frontSentinel.next, i);
    }

    @Override
    public T removeFirst() {
        if (size != 0) {
            T first = get(0);
            frontSentinel.next = frontSentinel.next.next;
            frontSentinel.restructe();
            size--;
            return first;
        }
        System.out.println("Empty Deque");
        return null;
    }

    @Override
    public T removeLast() {
        if (size != 0) {
            T last = get(size);
            backSentinel.prev = backSentinel.prev.prev;
            backSentinel.restructe();
            size--;
            return last;
        }
        System.out.println("Empty Deque");
        return null;
    }

    @Override
    public void printDeque() {
        System.out.print("{ ");
        Node<T> temp = frontSentinel.next;
        while (temp.item != null) {
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
        System.out.print("}");
    }

    public boolean equals(Object obj) {
        return this.isEqual(obj);
    }

    private boolean isEqual(Object o) {
        if (o instanceof Deque && ((Deque<?>) o).size() == this.size()) {
            Deque<?> oDeque = (Deque<?>) o;
            for (int i = 0; i < this.size(); i++) {
                if (!oDeque.get(i).equals(this.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * create an iterator from beginning of the Deque.
     * @return iterator pointing the first elem of Deque
     */
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements java.util.Iterator<T> {
        LinkedListDeque<T>.Node<T> current;

       DequeIterator() {
           current = frontSentinel.next;
        }

        @Override
        public boolean hasNext() {
            return current != backSentinel;
        }

        @Override
        public T next() {
            if (hasNext()) {
                T item = current.item;
                current = current.next;
                return item;
            }
            return null;
        }
    }
}
