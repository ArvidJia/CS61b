public interface List61B<T> {
    public void addLast(T item);
    public T removeLast();
    public int size();
    public T getLast();

    public default boolean isEmpty() {
        return size() == 0;
    }

    public default void print() {
        System.out.print("{ ");
        for (int i = 0; i < size(); i++) {
            System.out.print(getLast() + " ");
        }
        System.out.print(" }" + '\n');
    }
}
