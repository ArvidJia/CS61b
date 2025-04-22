package deque;

public interface Deque<T> {
    public int size();
    public default boolean isEmpty(){
        return size() == 0;
    };
    public void addFirst(T item);
    public void addLast(T item);
    public T get(int index);
    public T removeFirst();
    public T removeLast();
    public void printDeque();


}
