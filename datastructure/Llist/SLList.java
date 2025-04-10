public class SLList<T> implements List61B<T> {

    private class Node{
        T item;
        Node next;

        Node(T i, Node node){
            item = i;
            next = node;
        }

        int size(){
            if( next == null ){
                return 1;
            }
            return 1+next.size();
        }

        void addLast(T i){
            if(next == null){
                next = new Node(i,null);
                return;
            }
            next.addLast(i);
        }

        T get(int i){
            if(i > size()){
                System.out.println("Error");
                return null;
            }else if(i == 0){
                return item;
            }else{
                return next.get(i-1);
            }
        }

        void removeLast(){
            if(this.next.next == null) {
                next = null;
            }this.next.removeLast();
        }

    }


    private Node sentinal = new Node(null,null);
    private int size=0;

    public SLList(T x){
        sentinal.next = new Node(x, null);
        size += 1;
    }


    public void addFirst(T x){
        sentinal.next = new Node(x, sentinal.next);
        size += 1;
    }

    @Override
    public void addLast(T x){
        sentinal.next.addLast(x);
        size += 1;
    }

    @Override
    public T getLast(){
        return get(size()-1);
    }
    @Override
    public int size(){
        return size;
    }

    @Override
    public T removeLast(){
        T last = this.getLast();
        sentinal.next.removeLast();
        size -= 1;
        return last;
    }


    public T get(int i){
        return sentinal.next.get(i);
   }

   void printNode(Node node){
        if(node.next == null ){
            System.out.println(node.item);
        }else{
            System.out.println(node.item + " ");
            printNode( node.next );
        }
    }

   public void print(){
        System.out.println("slist own print.");
        printNode(sentinal.next);
   }

}

