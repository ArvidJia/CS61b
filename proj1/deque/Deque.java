package deque;

public interface Deque<T> {
    int size();
    default boolean isEmpty() {
        return size() == 0;
    };
    void addFirst(T item);
    void addLast(T item);
    T get(int index);
    T removeFirst();
    T removeLast();
    void printDeque();
}
