public class ListPractice {
    public static void main(String[] args) {
        Alist<Integer> alist = new Alist<>();
        alist.addLast(2);
        alist.addLast(3);

        alist.print();

        List61B<String> slist = new SLList<String>("Dear");
        slist.print();

        System.out.println("The size of the alist and slist is: " + alist.size()
            + " and " + slist.size());


    }






}
