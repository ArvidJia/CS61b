public class CircleList {
    private class intNode{
        intNode prev;
        int item;
        intNode next;

        intNode(intNode prev, int item,intNode next){
            this.prev = prev;
            this.item = item;
            this.next = next;
            if( this.next != null ) this.next.prev = this;
            if( this.prev != null ) this.prev.next = this;
        }
    }

    intNode sentinal =  new intNode(null,0,null); ;

    CircleList(int item){
        sentinal.next = sentinal;
        sentinal.prev = sentinal;
        this.addFirst(item);
    }
    CircleList(){
        sentinal.next = sentinal;
        sentinal.prev = sentinal;
    }

    public void addFirst(int item){
        sentinal.next = new intNode(sentinal, item, sentinal.next);
    }

    public void addLast(int item){
        sentinal.prev = new intNode(sentinal.prev, item, sentinal);
    }

    private void show(intNode node){
       if(node == sentinal){
           return;
       } else{
           System.out.print(node.item + " ");
           show(node.next);
       }
    }

    public void display(){
        show(sentinal.next);
    }

    public static void main(String[] args) {
        CircleList list = new CircleList(1);
        list.addFirst(0);
        list.addLast(2);

        list.display();


    }
}
