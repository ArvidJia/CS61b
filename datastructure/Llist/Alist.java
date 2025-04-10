public class Alist<T> implements List61B<T> {
    private T[] items;
    private int size = 0;
    private int capacity= 100;

    public Alist() {
        //creat a empty array
        items = (T[]) new Object[capacity];
    }

    @Override
    public void addLast(T item) {
        if (size == capacity) {
            this.resize(capacity*2);
        }
        items[size] = item;
        size += 1;
    }

    @Override
    public T getLast(){
        return items[size-1];
    }

    @Override
    public int size() {
        return this.size;
    }

    public T removeLast() {
        if ( size < capacity/4 && size > 4) {
           this.resize(capacity/4);
        }

        if(size > 0){
            T last = items[size-1];
            size--;
            return last;
        }else{
            System.out.println("Error! List is empty!");
            return null;
        }
    }

    private void resize(int capacity){
        T[] newItems = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newItems[i] = items[i];
        }
        items = newItems;
    }
}
