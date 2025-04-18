package deque;


public class ArrayDeque<T> {

    private int capacity = 8;
    private T[] items;

    private int nextfront = capacity-1;
    private int nextback = 0;
    private int size = 0;

    public ArrayDeque() {
        this.items = (T[]) new Object[capacity];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void addLast(T item) {
        if (size == capacity) {
            this.resize(capacity*2);
        }
        items[nextback] = item;
        nextback++;
        size++;
    }

    public void addFirst(T item) {
        if(size == capacity) {
            this.resize(capacity*2);
        }
        items[nextfront] = item;
        nextfront--;
        size++;
    }

    public T removeFirst() {
        if(size < capacity/2){
            resize(capacity/2);
        }
        T first = this.get(0);
        nextfront = nextfront+1;
        size--;
        return first;
    }

    public T removeLast() {
        if(size < capacity/2){
            resize(capacity/2);
        }
        T last = this.get(size-1);
        nextback = nextback-1;
        size--;
        return last;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }else{
            int realIndex = index + nextfront + 1;
            return realIndex >= capacity ? items[realIndex - capacity] : items[realIndex];
        }
    }


    public void printDeque(){
        System.out.print("{ ");
        for(int i = 0; i < size; i++){
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
        nextfront = capacity-1;
        nextback = size;
        items = newItems;
    }

    public static void main(String[] args) {
        ArrayDeque<Integer> arr = new ArrayDeque<Integer>();

        for(int i = 0; i < 100; i++){
            arr.addLast(i);
            if(arr.size() % 10 == 0){
                arr.printDeque();
            }
        }
        for(int i = 0; i < 100; i++){
            arr.removeFirst();
            if(arr.size() % 10 == 0){
                arr.printDeque();
            }
        }


    }
}
