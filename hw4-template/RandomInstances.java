import java.util.*;

public final class RandomInstances {

    private static Random rng = new Random();

    public static int ceilPow2(int n) {
        if (n <= 1) return 1;
        int x = 1;
        while (x < n) x <<= 1;
        return x;
    }

    private static int[] randomPermutation(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        return a;
    }

    public static int[] randomIntArray(int n) {
        int[] A = new int[n];
        for (int i = 0; i < n; i++) {
            // mix positives and negatives
            int v = rng.nextInt(2 * 512 + 1) - 512;
            A[i] = v;
        }

        return A;
    }

    public static final class StableMarriageStruct {
        public final int[][] mprefs;
        public final int[][] wprefs;
        public StableMarriageStruct(int[][] mprefs, int[][] wprefs) {
            this.mprefs = mprefs;
            this.wprefs = wprefs;
        }
    }

    public static StableMarriageStruct randomStableMarriage(int n) {
        int[][] mprefs = new int[n][n];
        int[][] wprefs = new int[n][n];

        for (int i = 0; i < n; i++) {
            mprefs[i] = randomPermutation(n);
            wprefs[i] = randomPermutation(n);
        }

        return new StableMarriageStruct(mprefs, wprefs);
    }

    public static int[] randomListRank(int n)
    {
        int[] parent = new int[n];
        parent[0] = -1; // single root

        for (int i = 1; i < n; i++) {
            parent[i] = rng.nextInt(i);  // uniform parent in {0,...,i-1}
        }

        return parent;
    }

    public static final class JobSchedulingStruct {
        public final int[] time;
        public final int[][] prerequisites;
        public JobSchedulingStruct(int[] time, int[][] prerequisites) {
            this.time = time;
            this.prerequisites = prerequisites;
        }
    }

    public static JobSchedulingStruct randomJobScheduling(int n) 
    {
        int minTime= 1;
        int maxTime = 100;
        double edgeProb = .25;

        // durations
        int[] time = new int[n];
        for (int i = 0; i < n; i++) {
            time[i] = minTime + rng.nextInt(maxTime - minTime + 1);
        }

        // random topological order
        int[] order = randomPermutation(n);
        int[] pos = new int[n];
        for (int i = 0; i < n; i++) pos[order[i]] = i;

        // adjacency (prereq edges u->v mean u must finish before v starts)
        boolean[][] adj = new boolean[n][n];
        int edges = 0;
        for (int i = 0; i < n; i++) {
            int u = order[i];
            for (int j = i + 1; j < n; j++) {
                int v = order[j];
                if (rng.nextDouble() < edgeProb) {
                    adj[u][v] = true;
                    edges++;
                }
            }
        }

        // Convert to prerequisites[v] = {u : adj[u][v]}
        int[][] prerequisites = new int[n][];
        for (int v = 0; v < n; v++) {
            int degIn = 0;
            for (int u = 0; u < n; u++) if (adj[u][v]) degIn++;
            int[] pre = new int[degIn];
            int idx = 0;
            for (int u = 0; u < n; u++) if (adj[u][v]) pre[idx++] = u;
            prerequisites[v] = pre;
        }

        return new JobSchedulingStruct(time, prerequisites);
    }
}