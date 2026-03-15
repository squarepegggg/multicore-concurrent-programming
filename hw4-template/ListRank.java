public class ListRank extends LLP {
    private int[] parent;

    // parent[i] = index of parent of node i
    // root r has parent[r] = -1
    public ListRank(int[] parent) {
        super();
        this.parent = parent;
        this.numThreads = parent.length;
        this.GlobalSpace = new int[parent.length];
        // Initialize: every non-root node starts with rank 1 (optimistic guess),
        // root starts at 0
        for (int i = 0; i < parent.length; i++) {
            if (parent[i] == -1) {
                GlobalSpace[i] = 0; // root
            } else {
                GlobalSpace[i] = 1; // all others start at 1
            }
        }
    }
    @Override
    public boolean forbidden(int j) {
        // Root is never forbidden — its rank is always 0
        if (parent[j] == -1) return false;
        // Node j is forbidden if its rank doesn't equal parent's rank + 1
        return GlobalSpace[j] != GlobalSpace[parent[j]] + 1;
    }
    @Override
    public void advance(int j) {
        // Pull rank from parent and add 1
        GlobalSpace[j] = GlobalSpace[parent[j]] + 1;
    }
    // Called after solve()
    public int[] getSolution() {
        return GlobalSpace;
    }
}