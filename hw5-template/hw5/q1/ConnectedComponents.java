public class ConnectedComponents extends LLP {
    private int[][] adjMatrix;
    public ConnectedComponents(int[][] adjMatrix) {

        super();
        GlobalState = new int[adjMatrix.length];
        for (int i = 0; i < GlobalState.length; i++) {
            GlobalState[i] = i;
        }
        this.adjMatrix = adjMatrix;
        numThreads = adjMatrix.length;
}

    @Override
    public boolean forbidden(int j) {
        if (GlobalState[j] < GlobalState[GlobalState[j]]) {
            return true;
        }
        else {
            for (int i = 0; i < adjMatrix.length; i++) {
                if (adjMatrix[j][i] > 0 && GlobalState[i] > GlobalState[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void advance(int j) {
        if (GlobalState[j] < GlobalState[GlobalState[j]]) {
            GlobalState[j] = GlobalState[GlobalState[j]];
        }
        else {
            for (int i = 0; i < adjMatrix.length; i++) {
                if (adjMatrix[j][i] > 0 && GlobalState[i] > GlobalState[j]) {
                    GlobalState[j] = GlobalState[i];
                }
            }
        }
    }

    // This method will be called after solve()
    public int[] getSolution() {
        // Return the vector where the i^th entry is the index j where
        // j is the largest vertex label contained in the component containing 
        // vertex i
        return GlobalState;
    }
}
