public class ParallelReduce extends LLP {
    int[] arr;
    int n;
    // A is an input array that we want to compute the reduction for
    public ParallelReduce(int[] A) {
        super();

        n = RandomInstances.ceilPow2(A.length);

        arr = new int[n + 1];
        System.arraycopy(A, 0, arr, 1, A.length);

        numThreads = n;
        GlobalSpace = new int[n];
        for (int i = 0; i < GlobalSpace.length; i++) {
            GlobalSpace[i] = Integer.MIN_VALUE / 2;
        }
    }

    @Override
    public boolean forbidden(int j) {
        if (1 <= j && j < n/2) {
            if (!(GlobalSpace[j] >= GlobalSpace[2 * j] + GlobalSpace[(2 * j) + 1])) {
                return true;
            }
        }
        else if (n/2 <= j && j < n) {
            if (!(GlobalSpace[j] >= arr[(2 * j) - n + 1] + arr[(2 * j) - n + 2])) {
                return true;
            }
        }
        //System.out.println("J: " + j);
        return false;
    }

    @Override
    public void advance(int j) {
        if (1 <= j && j < n/2) {
            if (!(GlobalSpace[j] >= GlobalSpace[2 * j] + GlobalSpace[(2 * j) + 1])) {
                GlobalSpace[j] = GlobalSpace[2 * j] + GlobalSpace[(2 * j) + 1];
                //System.out.println("J: " + j + " G[j]: " + GlobalSpace[j]);
            }
        }
        else if (n/2 <= j && j < n) {
            if (!(GlobalSpace[j] >= arr[(2 * j) - n + 1] + arr[(2 * j) - n + 2])) {
                GlobalSpace[j] = arr[(2 * j) - n + 1] + arr[(2 * j) - n + 2];
                //System.out.println("J: " + j + " G[j]: " + GlobalSpace[j]);
            }
        }
    }

    // This method will be called after solve()
    public int[] getSolution() {
        // Trim the state vector to only the reduce elements
        // Your result should have n-1 elements
        int[] solution = new int[n-1];
        for (int i = 0; i < n-1; i++) {
            solution[i] = GlobalSpace[i +  1];
        }
        return solution;
    }
}
