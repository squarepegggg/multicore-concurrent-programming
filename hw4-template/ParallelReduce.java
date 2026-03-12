public class ParallelReduce extends LLP {
    int[] arr;
    int n;
    // A is an input array that we want to compute the reduction for
    public ParallelReduce(int[] A) {
        super();

        // Pad to next power of 2
        int size = 1;
        while (size < A.length) size *= 2;
        n = size;

        arr = new int[n + 1];
        System.arraycopy(A, 0, arr, 1, A.length);

        numThreads = n;
        GlobalSpace = new int[n];
        for (int i = 0; i < GlobalSpace.length; i++) {
            GlobalSpace[i] = Integer.MIN_VALUE;
        }
    }

    @Override
    public boolean forbidden(int j) {
        if (1 <= j && j < n/2) {
            if (!(GlobalSpace[j] >= GlobalSpace[2 * j] + GlobalSpace[2 * j + 1])) {
                return true;
            }
        }
        if (n/2 <= j && j < n) {
            if (!(GlobalSpace[j] >= arr[2 * j - n + 1] + arr[2 * j - n + 2])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void advance(int j) {
        if (1 <= j && j < n/2) {
            GlobalSpace[j] = GlobalSpace[2 * j] + GlobalSpace[2 * j + 1];
        }
        if (n/2 <= j && j < n) {
            GlobalSpace[j] = arr[2 * j - n + 1] + arr[2 * j - n + 2];
        }
    }

    // This method will be called after solve()
    public int[] getSolution() {
        // Trim the state vector to only the reduce elements
        // Your result should have n-1 elements
        int[] solution = new int[n - 1];
        for (int i = 1; i < n; i++) {
            solution[i - 1] = GlobalSpace[i];
        }
        return solution;
    }
}
