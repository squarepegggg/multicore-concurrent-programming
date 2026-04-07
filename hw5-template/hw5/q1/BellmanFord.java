import java.util.ArrayList;

public class BellmanFord extends LLP {
    private int[][] adjMatrix;
    private ArrayList<ArrayList<Integer>> predecessors;
    // You may be given inputs with negative weights, but no negative cost cycles
    // The graph may be directed, the weight of the edge from i to j is adjMatrix[i][j]
    public BellmanFord(int[][] adjMatrix, int source) {
        super();
        GlobalState = new int[adjMatrix.length];
        predecessors = new ArrayList<>();
        numThreads = adjMatrix.length;
        this.adjMatrix = adjMatrix;
        for (int i = 0; i < adjMatrix.length; i++) {
            predecessors.add(new ArrayList<>());
            if (i == source) {
                GlobalState[i] = 0;
            } else {
                GlobalState[i] = Integer.MAX_VALUE;
            }
        }
        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix[i].length; j++) {
                if (adjMatrix[i][j] != 0) {
                    predecessors.get(j).add(i);
                }
            }
        }
    }

    @Override
    public boolean forbidden(int j) {
        for (int predecessor : predecessors.get(j)) {
            boolean neighborMax = GlobalState[predecessor] == Integer.MAX_VALUE;
            if (!neighborMax && (GlobalState[j] > GlobalState[predecessor] + adjMatrix[j][predecessor])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void advance(int j) {
        while (forbidden(j)) {
            for (int predecessor : predecessors.get(j)) {
                int val =  GlobalState[predecessor] + adjMatrix[j][predecessor];
                boolean notMax = !(GlobalState[predecessor] == Integer.MAX_VALUE);
                boolean forbidden = GlobalState[j] > GlobalState[predecessor] + adjMatrix[j][predecessor];
                if (notMax && forbidden) {
                    GlobalState[j] = val;
                }
            }
        }
    }

    // This method will be called after solve()
    public int[] getSolution() {
        // Return the vector of shortest path costs from source to each vertex
        // If a vertex is not connected to the source then its cost is Integer.MAX_VALUE
        return GlobalState;
    }
}
