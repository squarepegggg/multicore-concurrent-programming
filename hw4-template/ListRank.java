public class ListRank extends LLP {

    // parent[i] tells us the index of the parent of node i
    // the root r has parent[r] = -1
    public ListRank(int[] parent) {
        super();
    }

    @Override
    public boolean forbidden(int j) {
        return false;
    }

    @Override
    public void advance(int j) {

    }

    // This method will be called after solve()
    public int[] getSolution() {
        // Return the distance of every node to the root
        return new int[]{};
    }
}
