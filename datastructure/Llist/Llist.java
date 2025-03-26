public class Llist {
    public int first;
    public Llist rest;

    public Llist(int first, Llist rest) {
        this.first = first;
        this.rest = rest;
    }

    /*Initialize Llist by recursion */
    public Llist(int[] list) {
        if (list.length == 1) {
            this.first = list[0];
            this.rest = null;
            return;
        }else{
            this.first = list[0];
            int[] restList = new int[list.length - 1];
            System.arraycopy(list, 1, restList, 0, restList.length);
            this.rest = new Llist(restList);
        }
    }

    /*
        public Llist(int[] list) {
            this = new Llist(list[0], null);
            for (int i = 1; i < list.length; i++) {
                this = new Llist(list[i], this);
            }
        }

     */
    public int size(){
        if(this.rest == null){
            return 1;
        } else{
            return 1+this.rest.size();
        }
    }

    public int get(int index){
        if(index == 0) return this.first;
        else{
            return this.rest.get(index-1);
        }
    }



    public static void main(String[] args) {
        Llist L = new Llist(5, null);

        L = new Llist(10, L);
        L = new Llist(20, L);

        int[] array = new int[3];
        array[0] = 1;
        array[1] = 2;
        array[2] = 3;
        Llist b = new Llist(array);
        int sizeB = b.size();
        int elem2 = b.get(2);
        System.out.println(sizeB);
        System.out.println(elem2);
    }



}