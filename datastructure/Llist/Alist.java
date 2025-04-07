import org.junit.jupiter.api.test;
public class Alist {
    int capacity = 100;
    int[] arr = new int[capacity];
    int size = 0;

    Alist(int x){
        addLast(x);
    }

    public int getSize(){
        return size;
    }

    public int getLast(){
        return arr[size-1];
    }

    private void resize(int new_capacity){
        int[] new_arr = new int[new_capacity];
        arr = new_arr;
    }

    public void addLast(int x){
        if(size == capacity){
            resize(capacity*2);
            capacity *= 2;
        }
        arr[size] = x;
        size++;
    }

    private void removeLast(){
        if( size>0 ){
            size--;
        } else{
            System.out.println("Error");
        }

        if(size < capacity/2){
            resize(capacity/2);
            capacity /= 2;
        }
    }

    @test
    public void testAlsit() {
        Alist a = new Alist(0);
        for(int i=1; i < 200; i++){
            a.addLast(i*i);
        }

        System.out.println(a.getSize());
        System.out.println(a.getLast());
        System.out.println(a.capacity);

        for(int i=0; i < 101; i++){
            a.removeLast();
        }
        System.out.println(a.getSize());
        System.out.println(a.getLast());
        System.out.println(a.capacity);
    }

}
