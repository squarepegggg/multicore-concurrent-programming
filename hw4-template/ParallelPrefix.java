public class ParallelPrefix extends LLP {
    int[] arr;
    int[] sumArr;
    int n;
    // A is an input array that we want to compute the prefix scan for
    // S is the pre-computed summation tree (reduction), computed using LLP-Reduce
    public ParallelPrefix(int[] A, int[] S) {
        super();

        int size = 1;
        while (size < A.length) size *= 2;
        n = size;

        arr = new int[n + 1];
        sumArr = new int[n];
        System.arraycopy(A, 0, arr, 1, A.length);
        System.arraycopy(S, 0, sumArr, 0, S.length);

        numThreads = 2 * n;
        GlobalSpace = new int[2 * n];
        for (int i = 0; i < GlobalSpace.length; i++) {
            GlobalSpace[i] = Integer.MIN_VALUE;
        }
    }

    @Override
    public boolean forbidden(int j) {
        if (j == 1) {
            return !(GlobalSpace[j] >= 0);
        }
        else if (j % 2 == 0) {
            if (GlobalSpace[j/2] == Integer.MIN_VALUE) return false;
            return !(GlobalSpace[j] >= GlobalSpace[j / 2]);
        }
        else if (j < n) {
            if (GlobalSpace[j/2] == Integer.MIN_VALUE) return false;
            return !(GlobalSpace[j] >= sumArr[j - 2] + GlobalSpace[j/2]);
        }
        else if (j >= n) {
            if (GlobalSpace[j/2] == Integer.MIN_VALUE) return false;
            return !(GlobalSpace[j] >= arr[j-n] + GlobalSpace[j/2]);
        }
        return false;
    }

    @Override
    public void advance(int j) {
        if (j == 1) {
            GlobalSpace[j] = 0;
        }
        else if (j % 2 == 0) {
            GlobalSpace[j] = GlobalSpace[j/2];
        }
        else if (j < n) {
            GlobalSpace[j] = sumArr[j - 2] + GlobalSpace[j/2];
        }
        else if (j > n) {
            GlobalSpace[j] = arr[j-n] + GlobalSpace[j/2];
        }
    }

    // This method will be called after solve()
    public int[] getSolution() {
        // Return only the prefix scan part of the state vector
        // i.e. return the last n elements
        int[] solution = new int[n];
        for (int i = 0; i < n; i++) {
            solution[i] = GlobalSpace[n + i];
        }
        return solution;
    }
}
