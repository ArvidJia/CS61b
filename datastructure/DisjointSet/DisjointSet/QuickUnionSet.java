package DisjointSet;


public class QuickUnionSet implements DisjointSet {
    int[] parent;

    public QuickUnionSet(int size) {
        parent = new int[size];

        for (int i = 0; i < size; i++) {
            parent[i] = -1;
        }
    }

    private int findRoot(int x) {
        //base case
        int father = parent[x];
        if (parent[father] < 0) {
            return father;
        }
        //recursive case
        return parent[father];
    }

    
    private void pathCompressionHelper(int[] fathers, int root){
        //TO DO;
        for (int i = 0; i < fathers.length; i++) {
            fathers[i] = root;
        }
    }



    @Override
    public void union(int a, int b) {
        if (parent[findRoot(a)] < parent[findRoot(b)]) {
            //a is bigger;
            //connect b -> a;
            parent[findRoot(b)] = findRoot(b);
        } else {
            //connect a -> b;
            parent[findRoot(a)] = findRoot(a);
        }
    }

    @Override
    public boolean isConnected(int a, int b) {
        return findRoot(a) == findRoot(b);
    }



}
