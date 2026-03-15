public class ListRank extends LLP {
    private int[] pred;
    // rank[i] = accumulated hops from node i to the root
    private int[] rank;
    // jumper[i] = the node we currently leap to in rank[i] steps
    private int[] jumper;
    private int head;

    // pred[i] = predecessor of node i; the root satisfies pred[root] = -1
    public ListRank(int[] pred) {
        super(pred.length);
        this.pred = pred;
        this.rank = new int[n];
        this.jumper = new int[n];

        // locate the root node
        head = -1;
        for (int i = 0; i < n; i++) {
            if (pred[i] == -1) { head = i; break; }
        }

        // base case: root has rank 0 and jumps to itself
        // every other node starts with rank 1, jumping to its predecessor
        for (int i = 0; i < n; i++) {
            if (i == head) {
                rank[i]   = 0;
                jumper[i] = head;
            } else {
                rank[i]   = 1;
                jumper[i] = pred[i];
            }
        }
    }

    // node j still needs work as long as its jumper hasn't landed on the root
    @Override
    public boolean forbidden(int j) {
        return jumper[j] != head;
    }

    // pointer jump: fold in the distance through jumper, then leap jumper forward
    // rank[j]   := rank[j] + rank[jumper[j]]
    // jumper[j] := jumper[jumper[j]]
    @Override
    public void advance(int j) {
        rank[j]   = rank[j] + rank[jumper[j]];
        jumper[j] = jumper[jumper[j]];
    }

    // returns each node's distance to the root after solve() completes
    public int[] getSolution() {
        return rank;
    }
}
