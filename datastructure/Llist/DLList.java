public class DLList {
    private class intNode{
        intNode prev;
        int item;
        intNode next;

        intNode(intNode p, int i, intNode n){
            this.prev = p;
            this.item = i;
            this.next = n;
            if(p!=null) this.prev.next = this;
            if(n!=null) this.next.prev = this;
        }

    }

    private intNode frontSentinal = new intNode(null, 999, null);
    private intNode backSentinal = new intNode(null, -999, frontSentinal);
    private int size;

    public DLList(int i){
        //sentinal.next.item = i;
        //sentinal.next.prev = sentinal;
        //sentinal.next.next = sentinal;
        size=1;
        frontSentinal.prev = backSentinal;
        frontSentinal.next = new intNode(frontSentinal, i, backSentinal);
        backSentinal.prev = frontSentinal.next;
    }
   public void addFirst(int i){
        size++;
        frontSentinal.next = new intNode(frontSentinal, i,frontSentinal.next);
   }
   public void addLast(int i){
        size++;
        backSentinal.prev = new intNode(backSentinal.prev,i,backSentinal);

   }

   public int size(){
       return size;
   }


   public void print(intNode p){
        if(p==backSentinal){
           System.out.println(p.item);
           return;
        }else{
            System.out.println(p.item);
            print(p.next);
        }

   }


    public static void main (String[] args){
        DLList dl = new DLList(1);
        dl.addLast(2);
        dl.addLast(0);
        int s = dl.size;
        System.out.println("Size is " +s);
    }




}
