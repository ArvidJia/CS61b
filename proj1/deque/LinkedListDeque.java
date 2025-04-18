package deque;

public class LinkedListDeque<T> {
    private class Node<T>{
        Node<T> prev;
        T item;
        Node<T> next;

        public Node(Node<T> _prev,T _item, Node<T> _next) {
            this.prev = _prev;
            this.item = _item;
            this.next = _next;
            if(this.next != null){
                this.next.prev = this;
            }
            if(this.prev != null){
                this.prev.next = this;
            }
        }
    }

    Node<T> frontSentinel = new Node<T>(null, null, null);
    Node<T> backSentinel = new Node<T>(frontSentinel, null, frontSentinel);
    int size = 0;

    public LinkedListDeque() {
        frontSentinel.next = backSentinel;
        frontSentinel.prev = backSentinel;
    };

    public LinkedListDeque(T _item) {
        this.addFirst(_item);
        size = 1;
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public void addFirst(T _item) {
        frontSentinel.next = new Node<T>(frontSentinel, _item, frontSentinel.next);
        ++size;
    }

    public void addLast(T _item) {
        backSentinel.prev = new Node<T>(backSentinel.prev, _item, backSentinel);
        size = 1;
    }

    public T get(int i){
        if(i < size/2 && i >= 0){
            Node<T> temp = frontSentinel.next;
            while(i > 0){
                temp = temp.next;
            }
            return temp.item;
        }else{
            Node<T> temp = backSentinel.prev;
            i = size - i;
            while(i > 0){
                temp = temp.prev;
            }
            return temp.item;
        }
    }


    private T getRrcursivelyHelper(Node<T> node,int index){
        if(index > 0){
             getRrcursivelyHelper(node.next, index-1);
        }
        return node.item;
    }

    public void getRecursively(int i){
        if(i >= size || i < 0){
            System.out.println("Out of bounds");
        }
        getRrcursivelyHelper(frontSentinel.next, i);
    }



    public T removeFirst() {
        if(size != 0){
            T first = get(0);
            frontSentinel.next = frontSentinel.next.next;
            return first;
        }
        System.out.println("Empty Deque");
        return null;
    }

    public T removeLast() {
        if(size != 0){
            T last = get(size);
            backSentinel.prev = frontSentinel.prev.prev;
            return last;
        }
        System.out.println("Empty Deque");
        return null;
    }

    public void printDeque(){
        System.out.print("{ ");
        Node<T> temp = frontSentinel.next;
        while(temp.item != null){
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
        System.out.print("}");
    }

    public static void main(String[] args) {
        LinkedListDeque<Integer> deque = new LinkedListDeque();
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addLast(3);
        deque.printDeque();

    }
}



