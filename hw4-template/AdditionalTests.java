import org.junit.Test;
import static org.junit.Assert.*;

public class AdditionalTests {

    // =========================================================
    // StableMarriage Tests
    // =========================================================

    /**
     * Verifies the matching is a valid bijection: every man is matched to
     * exactly one woman and vice-versa.
     */
    private void assertValidMatching(int[] matching) {
        boolean[] seen = new boolean[matching.length];
        for (int w : matching) {
            assertFalse("Duplicate woman in matching", seen[w]);
            seen[w] = true;
        }
    }

    /**
     * Verifies that no (man, woman) pair would both prefer each other over
     * their current partners — the definition of stability.
     */
    private void assertStable(int[][] mprefs, int[][] wprefs, int[] matching) {
        int n = matching.length;

        // Build inverse: wPartner[w] = the man matched to w
        int[] wPartner = new int[n];
        for (int m = 0; m < n; m++) wPartner[matching[m]] = m;

        // Build rank lookups so preference checks are O(1)
        int[][] mrank = new int[n][n]; // mrank[m][w] = position of w in m's list
        int[][] wrank = new int[n][n]; // wrank[w][m] = position of m in w's list
        for (int m = 0; m < n; m++)
            for (int pos = 0; pos < n; pos++)
                mrank[m][mprefs[m][pos]] = pos;
        for (int w = 0; w < n; w++)
            for (int pos = 0; pos < n; pos++)
                wrank[w][wprefs[w][pos]] = pos;

        for (int m = 0; m < n; m++) {
            int mw = matching[m];
            for (int w = 0; w < n; w++) {
                if (w == mw) continue;
                // Would m prefer w over his current partner?
                if (mrank[m][w] < mrank[m][mw]) {
                    // Would w prefer m over her current partner?
                    int wm = wPartner[w];
                    assertFalse(
                            "Unstable pair: man " + m + " and woman " + w,
                            wrank[w][m] < wrank[w][wm]
                    );
                }
            }
        }
    }

    // --- n=1 trivial case ---
    @Test
    public void testStableMarriage_singlePair() {
        int[][] mprefs = {{ 0 }};
        int[][] wprefs = {{ 0 }};
        int[] expected = { 0 };

        StableMarriage sm = new StableMarriage(mprefs, wprefs);
        sm.solve();
        assertArrayEquals(expected, sm.getSolution());
    }

    // --- n=2 both men prefer woman 0 ---
    @Test
    public void testStableMarriage_twoMen_contested() {
        // Man 0 and man 1 both prefer woman 0 first.
        // Woman 0 prefers man 0; woman 1 prefers man 1.
        // Man-optimal GS result: m0->w0, m1->w1
        int[][] mprefs = {{ 0, 1 }, { 0, 1 }};
        int[][] wprefs = {{ 0, 1 }, { 1, 0 }};
        int[] expected = { 0, 1 };

        StableMarriage sm = new StableMarriage(mprefs, wprefs);
        sm.solve();
        int[] matching = sm.getSolution();
        assertArrayEquals(expected, matching);
        assertValidMatching(matching);
        assertStable(mprefs, wprefs, matching);
    }

    // --- n=2 crossed preferences, unique stable matching ---
    @Test
    public void testStableMarriage_twoMen_crossed() {
        int[][] mprefs = {{ 0, 1 }, { 1, 0 }};
        int[][] wprefs = {{ 0, 1 }, { 1, 0 }};
        int[] expected = { 0, 1 };

        StableMarriage sm = new StableMarriage(mprefs, wprefs);
        sm.solve();
        int[] matching = sm.getSolution();
        assertArrayEquals(expected, matching);
        assertStable(mprefs, wprefs, matching);
    }

    // --- n=4 hand-crafted case ---
    @Test
    public void testStableMarriage_four() {
        int[][] mprefs = {
                { 0, 1, 2, 3 },
                { 1, 0, 3, 2 },
                { 2, 3, 0, 1 },
                { 3, 2, 1, 0 }
        };
        int[][] wprefs = {
                { 0, 1, 2, 3 },
                { 1, 0, 3, 2 },
                { 2, 3, 0, 1 },
                { 3, 2, 1, 0 }
        };
        // Each man and his matching woman mutually top-rank each other
        int[] expected = { 0, 1, 2, 3 };

        StableMarriage sm = new StableMarriage(mprefs, wprefs);
        sm.solve();
        int[] matching = sm.getSolution();
        assertArrayEquals(expected, matching);
        assertValidMatching(matching);
        assertStable(mprefs, wprefs, matching);
    }

    // --- Random small instances: just verify stability and bijectivity ---
    @Test
    public void testStableMarriage_randomSmall() {
        for (int trial = 0; trial < 20; trial++) {
            int n = 5;
            RandomInstances.StableMarriageStruct s = RandomInstances.randomStableMarriage(n);
            StableMarriage sm = new StableMarriage(s.mprefs, s.wprefs);
            sm.solve();
            int[] matching = sm.getSolution();
            assertValidMatching(matching);
            assertStable(s.mprefs, s.wprefs, matching);
        }
    }

    // --- Random large instance ---
    @Test
    public void testStableMarriage_randomLarge() {
        int n = 100;
        RandomInstances.StableMarriageStruct s = RandomInstances.randomStableMarriage(n);
        StableMarriage sm = new StableMarriage(s.mprefs, s.wprefs);
        sm.solve();
        int[] matching = sm.getSolution();
        assertValidMatching(matching);
        assertStable(s.mprefs, s.wprefs, matching);
    }

    // =========================================================
    // ParallelReduce Tests
    // =========================================================

    /** Computes the expected reduce tree by hand for verification. */
    private int[] expectedReduce(int[] A) {
        int n = A.length;
        int sz = RandomInstances.ceilPow2(n);
        // Internal nodes only: the reduce tree has sz-1 internal nodes
        // stored in BFS order (root at index 0).
        int[] tree = new int[sz - 1];
        // Leaf layer values (padded with 0)
        int[] leaves = new int[sz];
        for (int i = 0; i < n; i++) leaves[i] = A[i];

        // Fill bottom-up
        int width = sz / 2;
        int offset = sz / 2 - 1; // index of first node at this level
        for (int i = 0; i < width; i++) {
            tree[offset + i] = leaves[2 * i] + leaves[2 * i + 1];
        }
        width /= 2;
        offset = offset / 2 - (offset == 0 ? 0 : 0);
        // Recompute offset properly layer by layer
        int[] layerStart = new int[32];
        int levels = Integer.numberOfTrailingZeros(sz); // log2(sz)
        layerStart[levels - 1] = sz / 2 - 1;
        for (int lev = levels - 2; lev >= 0; lev--) {
            layerStart[lev] = (layerStart[lev + 1] - 1) / 2;
        }
        // Re-fill using layerStart
        for (int lev = levels - 1; lev >= 1; lev--) {
            int start = layerStart[lev];
            int count = 1 << lev;
            for (int i = 0; i < count; i++) {
                int parent = layerStart[lev - 1] + i / 2;
                // already filled leaves; combine children
            }
        }
        // Simpler recomputation: just sum children already in tree or leaves
        for (int i = sz / 2 - 2; i >= 0; i--) {
            tree[i] = tree[2 * i + 1] + tree[2 * i + 2];
        }
        return tree;
    }

    // --- Power-of-2 sized array ---
    @Test
    public void testParallelReduce_pow2() {
        int[] A = { 3, 1, 4, 1, 5, 9, 2, 6 };
        // Tree (BFS): root=31, then 9,22, then 4,5,14,8
        int[] expected = { 31, 9, 22, 4, 5, 14, 8 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        assertArrayEquals(expected, pr.getSolution());
    }

    // --- n=1 edge case ---
    @Test
    public void testParallelReduce_singleElement() {
        int[] A = { 42 };
        // Only one leaf, no internal nodes needed; but tree root = 42
        // (depends on implementation — adjust if your tree stores root differently)
        int[] expected = { 42 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] result = pr.getSolution();
        // The total sum must be 42
        assertEquals(42, result[0]);
    }

    // --- n=2 ---
    @Test
    public void testParallelReduce_twoElements() {
        int[] A = { 5, 7 };
        int[] expected = { 12 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        assertArrayEquals(expected, pr.getSolution());
    }

    // --- Non-power-of-2 size (n=5) ---
    @Test
    public void testParallelReduce_nonPow2() {
        int[] A = { 1, 2, 3, 4, 5 };
        // padded to 8: [1,2,3,4,5,0,0,0]
        // leaves pairs: (1+2)=3, (3+4)=7, (5+0)=5, (0+0)=0
        // next: (3+7)=10, (5+0)=5
        // root: 15
        // BFS order: 15, 10, 5, 3, 7, 5, 0
        int[] expected = { 15, 10, 5, 3, 7, 5, 0 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        assertArrayEquals(expected, pr.getSolution());
    }

    // --- All zeros ---
    @Test
    public void testParallelReduce_allZeros() {
        int[] A = { 0, 0, 0, 0 };
        int[] expected = { 0, 0, 0 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        assertArrayEquals(expected, pr.getSolution());
    }

    // --- Negative values ---
    @Test
    public void testParallelReduce_negatives() {
        int[] A = { -1, -2, -3, -4 };
        // pairs: (-3), (-7); root: -10
        int[] expected = { -10, -3, -7 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        assertArrayEquals(expected, pr.getSolution());
    }

    // --- Random: root of reduce tree must equal brute-force sum ---
    @Test
    public void testParallelReduce_randomRootEqualsSum() {
        for (int trial = 0; trial < 30; trial++) {
            int n = 1 + (int)(Math.random() * 64);
            int[] A = RandomInstances.randomIntArray(n);

            int bruteSum = 0;
            for (int v : A) bruteSum += v;

            ParallelReduce pr = new ParallelReduce(A);
            pr.solve();
            int[] tree = pr.getSolution();

            assertEquals("Root should equal brute-force sum (trial " + trial + ")", bruteSum, tree[0]);
        }
    }

    // =========================================================
    // ParallelPrefix Tests
    // =========================================================

    // --- n=1: prefix of single element is 0 (exclusive) ---
    @Test
    public void testParallelPrefix_singleElement() {
        int[] A = { 99 };
        int[] reduceExpected = { 99 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] S = pr.getSolution();
        assertArrayEquals(reduceExpected, S);

        int[] expected = { 0 };
        ParallelPrefix pp = new ParallelPrefix(A, S);
        pp.solve();
        assertArrayEquals(expected, pp.getSolution());
    }

    // --- n=2 ---
    @Test
    public void testParallelPrefix_twoElements() {
        int[] A = { 3, 5 };
        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] S = pr.getSolution();

        int[] expected = { 0, 3 };
        ParallelPrefix pp = new ParallelPrefix(A, S);
        pp.solve();
        assertArrayEquals(expected, pp.getSolution());
    }

    // --- Power-of-2, all ones ---
    @Test
    public void testParallelPrefix_allOnes() {
        int n = 8;
        int[] A = new int[n];
        java.util.Arrays.fill(A, 1);

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] S = pr.getSolution();

        // exclusive prefix sums: 0,1,2,3,4,5,6,7
        int[] expected = new int[n];
        for (int i = 0; i < n; i++) expected[i] = i;

        ParallelPrefix pp = new ParallelPrefix(A, S);
        pp.solve();
        assertArrayEquals(expected, pp.getSolution());
    }

    // --- Non-power-of-2 (n=5) ---
    @Test
    public void testParallelPrefix_nonPow2() {
        int[] A = { 2, 4, 6, 8, 10 };
        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] S = pr.getSolution();

        int[] expected = { 0, 2, 6, 12, 20 };
        ParallelPrefix pp = new ParallelPrefix(A, S);
        pp.solve();
        assertArrayEquals(expected, pp.getSolution());
    }

    // --- Negative values ---
    @Test
    public void testParallelPrefix_negatives() {
        int[] A = { -1, -2, -3, -4 };
        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] S = pr.getSolution();

        int[] expected = { 0, -1, -3, -6 };
        ParallelPrefix pp = new ParallelPrefix(A, S);
        pp.solve();
        assertArrayEquals(expected, pp.getSolution());
    }

    // --- Mixed positive/negative ---
    @Test
    public void testParallelPrefix_mixed() {
        int[] A = { 5, -3, 2, -1, 4 };
        ParallelReduce pr = new ParallelReduce(A);
        int[] expectedReduce = {7,3,4,2,1,4,0};
        pr.solve();
        int[] S = pr.getSolution();
        assertArrayEquals(expectedReduce, S);

        int[] expected = { 0, 5, 2, 4, 3 };
        ParallelPrefix pp = new ParallelPrefix(A, S);
        pp.solve();
        int[] result =  pp.getSolution();
        assertArrayEquals(expected, result);
    }

    // --- Random: verify exclusive prefix property against brute force ---
    @Test
    public void testParallelPrefix_randomCorrectness() {
        for (int trial = 0; trial < 30; trial++) {
            int n = 2 + (int)(Math.random() * 63); // 2..64
            int[] A = RandomInstances.randomIntArray(n);

            // Brute-force exclusive prefix
            int[] brutePrefix = new int[n];
            brutePrefix[0] = 0;
            for (int i = 1; i < n; i++) brutePrefix[i] = brutePrefix[i - 1] + A[i - 1];

            ParallelReduce pr = new ParallelReduce(A);
            pr.solve();
            int[] S = pr.getSolution();

            ParallelPrefix pp = new ParallelPrefix(A, S);
            pp.solve();
            int[] result = pp.getSolution();

            assertArrayEquals("Mismatch on trial " + trial, brutePrefix, result);
        }
    }

    // --- Prefix of a power-of-2 array matches brute force ---
    @Test
    public void testParallelPrefix_pow2_bruteForce() {
        int[] A = { 10, 20, 30, 40, 50, 60, 70, 80 };
        int[] brutePrefix = { 0, 10, 30, 60, 100, 150, 210, 280 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] S = pr.getSolution();

        ParallelPrefix pp = new ParallelPrefix(A, S);
        pp.solve();
        assertArrayEquals(brutePrefix, pp.getSolution());
    }
}