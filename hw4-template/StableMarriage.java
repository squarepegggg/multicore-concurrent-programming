public class StableMarriage extends LLP {
    int[][] malePrefs;
    int[][] womanRank; // womanRank[k][i] is how woman k ranks man i 0 being highest
    int n;
    // mprefs[i][k] is man i's kth choice
    // wprefs[i][k] is woman i's kth choice
    public StableMarriage(int[][] mprefs, int[][] wprefs) {
        super();
        malePrefs = mprefs;
        n = mprefs.length;
        numThreads = n;

        womanRank = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                womanRank[i][wprefs[i][j]] = j;
            }
        }

        GlobalSpace = new int[n];
        for (int i = 0; i < n; i++) {
            GlobalSpace[i] = 0;
        }
    }

    @Override
    public boolean forbidden(int j) {
        // Get woman, w, currently being proposed to by man j
        int w = malePrefs[j][GlobalSpace[j]];

        /*
        Does there exist some man, i, such that:
        woman w, is ranked as high or higher by man i than man j
        and woman w, prefers  man i to man j
         */
        for (int man = 0; man < n; man++) {
            if (man == j) continue;

            int currentProposal = malePrefs[man][GlobalSpace[man]];
            if ((currentProposal == w) && (womanRank[w][man] < womanRank[w][j])) {
                return true;
            }
        }
        // Alpha
        return false;
    }

    @Override
    public void advance(int j) {
        GlobalSpace[j]++;
        if (GlobalSpace[j] > n) {
            unsatisfiable.set(true);
        }
        System.out.println("j: " + j + " G[j]: " + GlobalSpace[j]);
    }

    // This method will be called after solve()
    public int[] getSolution() {
        int[] solution = new int[n];
        for (int j = 0; j < n; j++) {
            solution[j] = malePrefs[j][GlobalSpace[j]];
        }
        return solution;
    }
}
