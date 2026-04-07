import java.util.ArrayList;

public class Johnsons extends LLP {
    private int[][] adjMatrix;
    private ArrayList<ArrayList<Integer>> predecessors;
    // You may be given inputs with negative weights, but no negative cost cycles
    // The graph may be directed, the weight of the edge from i to j is adjMatrix[i][j]
    public Johnsons(int[][] adjMatrix) {
        super();
        GlobalState = new int[adjMatrix.length];
        predecessors = new ArrayList<>();
        numThreads = adjMatrix.length;
        this.adjMatrix = adjMatrix;
        for (int i = 0; i < adjMatrix.length; i++) {
            predecessors.add(new ArrayList<>());
            GlobalState[i] = 0;
        }
        for (int i = 0; i < GlobalState.length; i++) {
            for (int j = 0; j < adjMatrix[i].length; j++) {
                if (adjMatrix[i][j] != 0) {
                    predecessors.get(j).add(i);
                }
            }
        }
    }

    @Override
    public boolean forbidden(int j) {
        int max = Integer.MIN_VALUE;
        for (int predecessor : predecessors.get(j)) {
            if (GlobalState[predecessor] - adjMatrix[predecessor][j] > max) {
                max = GlobalState[predecessor] - adjMatrix[predecessor][j];
            }
        }
        if (GlobalState[j] < max) {
            return true;
        }
        return false;
    }

    @Override
    public void advance(int j) {
        int max = Integer.MIN_VALUE;
        for (int predecessor : predecessors.get(j)) {
            if (GlobalState[predecessor] - adjMatrix[predecessor][j] > max) {
                max = GlobalState[predecessor] - adjMatrix[predecessor][j];
            }
        }
        if (GlobalState[j] < max) {
            GlobalState[j] = max;
        }
    }

    // This method will be called after solve()
    public int[] getSolution() {
        // Return the minimum price vector from Johnson's algorithm
        return GlobalState;
    }
}