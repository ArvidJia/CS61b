package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private int capacity = 8;
    private T[] items;

    private int nextfront = capacity - 1;
    private int nextback = 0;
    private int size = 0;

    public ArrayDeque() {
        this.items = (T[]) new Object[capacity];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addLast(T item) {
        if (size == capacity) {
            this.resize(capacity * 2);
        }
        items[nextback] = item;
        nextback = nextback == capacity - 1 ? 0 : nextback + 1;
        size++;
    }

    @Override
    public void addFirst(T item) {
        if (size == capacity) {
            this.resize(capacity * 2);
        }
        items[nextfront] = item;
        nextfront = nextfront == 0 ? capacity - 1 : nextfront - 1;
        size++;
    }

    @Override
    public T removeFirst() {
        if (size < capacity / 2) {
            resize(capacity / 2);
        }
        if (size > 0) {
            T first = this.get(0);
            nextfront = nextfront == capacity - 1 ? 0 : nextfront + 1;
            size--;
            return first;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (size < capacity / 2) {
            resize(capacity / 2);
        }
        if (size > 0) {
            T last = this.get(size - 1);
            nextback = nextback == 0 ? capacity - 1 : nextback - 1;
            size--;
            return last;
        }
        return null;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        } else {
            int realIndex = index + nextfront + 1;
            return realIndex >= capacity ? items[realIndex - capacity] : items[realIndex];
        }
    }


    public void printDeque() {
        System.out.print("{ ");
        for (int i = 0; i < size; i++) {
            System.out.print(this.get(i) + " ");
        }
        System.out.println(" }");
    }



    private void resize(int newCapacity) {
        T[] newItems = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newItems[i] = this.get(i);
        }
        capacity = newCapacity;
        nextfront = capacity - 1;
        nextback = size;
        items = newItems;
    }



    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private int index;
        DequeIterator() {
            index = 0;
        }
        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            T item = get(index);
            index++;
            return item;
        }
    }


    public boolean equals(Object o) {
        return this.isEqual(o);
    }

    private boolean isEqual(Object o) {
        if (o instanceof Deque && ((Deque<?>) o).size() == this.size) {
            Deque<?> oDeque = (Deque<?>) o;
            for (int i = 0; i < size; i++) {
                if (this.get(i).equals(oDeque.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
