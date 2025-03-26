public class SLList {

    private class intNode{
        int item;
        intNode next;

        intNode(int i, intNode node){
            item = i;
            next = node;
        }

        int size(){
            if( next == null ){
                return 1;
            }
            return 1+next.size();
        }

        void addLast(int i){
            if(next == null){
                next = new intNode(i,null);
                return;
            }
            next.addLast(i);
        }

        int get(int i){
            if(i > size()){
                System.out.println("Error");
                return -1;
            }else if(i == 0){
                return item;
            }else{
                return next.get(i-1);
            }
        }


    }


    private intNode first;
    private int size;

    public SLList(int x){
        first = new intNode(x, null);
    }


    public void addFirst(int x){
        first = new intNode(x, first);
    }

    public void addLast(int x){
        first.addLast(x);
    }

   public int size(){
        return first.size();
   }

   public int get(int i){
        return first.get(i);
   }

   void printNode(intNode node){
        if(node.next == null ){
            System.out.println(node.item);
        }else{
            System.out.println(node.item + " ");
            printNode( node.next );
        }
    }

   public void print(){
        printNode( first );
   }


   public static void main(String[] args){
        SLList list = new SLList(1);
        list.addLast(2);
        list.addFirst(0);

        list.print();



   }


}

