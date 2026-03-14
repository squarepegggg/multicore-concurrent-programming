import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SimpleTest {

    @Test
    public void testJobScheduling() {
        int[] time = { 2, 3, 4, 1, 5 };
        int[][] prerequisites = {
                {},         // Job 0 has no prerequisites
                { 0 },      // Job 1 depends on Job 0
                { 0, 1 },   // Job 2 depends on Job 0 and Job 1
                { 2 },      // Job 3 depends on Job 2
                { 3 }       // Job 4 depends on Job 3
        };
        int[] expected = { 2, 5, 9, 10, 15 };

        JobScheduling js = new JobScheduling(time, prerequisites);
        js.solve();
        int[] completion_times = js.getSolution();
        assertArrayEquals(expected, completion_times);
    }

    @Test
    public void testStableMarriage() {
        int[][] mprefs = {
                { 1, 2, 0 },
                { 0, 1, 2 },
                { 0, 1, 2 }
        };
        int[][] wprefs = {
                { 1, 0, 2 },
                { 1, 2, 0 },
                { 0, 1, 2 }
        };
        int[] expected = { 2, 0, 1 };

        StableMarriage sm = new StableMarriage(mprefs, wprefs);
        sm.solve();
        int[] matching = sm.getSolution();
        for (int i = 0; i < matching.length; i++) {
            System.out.print(matching[i] + " ");
        }
        assertArrayEquals(expected, matching);
    }

    @Test
    public void testParallelReduce() {
        int[] A = { 1, 2, 3, 4 };
        int[] expected = { 10, 3, 7 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] reduce = pr.getSolution();
        assertArrayEquals(expected, reduce);
    }
    @Test public void testParallelReduce2() {
        int[] A = { 415, -373, 174, 373, 67, 500, 304, -124, 159, -301, -360, 216, -121, -512, -160, -465 };
        int[] expected = { -208, 1336, -1544, 589, 747, -286, -1258, 42, 547, 567, 180, -142, -144, -633, -625 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] reduce = pr.getSolution();
        assertArrayEquals(expected, reduce);
    }

    @Test public void testParallelReduce3() {
        int[] A = { -409, -374, -426, 367, 317, -68, 231, -438, -121, 113, 452, -373, -4, -354, -411, -177, -194, 110, 158, -379, -394, 284, 200, -121, -335, -354, 391, 249, -211, 436, 373, 456, -84, 174, 22, 288, -423, 171, 138, 356, -264, 313, 99, 226, 461, -8, -98 };
        int expected = 0;
        for (int i = 0;i < A.length;i++) {
            expected += A[i];
        }


        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] reduce = pr.getSolution();
        //System.out.print(Arrays.toString(reduce));
        assertEquals(expected, reduce[0]);
    }

    @Test public void testParallelReduce4() {
        int[] A = {100};
        int[] expected = {100};

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] reduce = pr.getSolution();
        assertArrayEquals(expected, reduce);
    }

    @Test public void testParallelPrefix2() {
        int[] A = { 100 };
        int[] expectedPR = {100};
        int[] expectedPP = {0};

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] S = pr.getSolution();
        assertArrayEquals(expectedPR, S);

        ParallelPrefix pp = new ParallelPrefix(A, S);
        pp.solve();
        int[] prefix = pp.getSolution();
        assertArrayEquals(expectedPP, prefix);
    }

    @Test
    public void testParallelPrefix() {
        int[] A = { 1, 2, 3, 4, 5, 6, 7, 8 };
        int[] expectedPR = {36, 10, 26, 3, 7, 11, 15};
        int[] expected = { 0, 1, 3, 6, 10, 15, 21, 28 };

        ParallelReduce pr = new ParallelReduce(A);
        pr.solve();
        int[] S = pr.getSolution();
        assertArrayEquals(expectedPR, S);

        ParallelPrefix pp = new ParallelPrefix(A, S);
        pp.solve();
        int[] prefix = pp.getSolution();
        assertArrayEquals(expected, prefix);
    }

    @Test
    public void testListRanking() {
        int[] parents = { 2, 4, 8, 8, -1, 2, 1, 6, 4 };
        int[] expected = { 3, 1, 2, 2, 0, 3, 2, 3, 1 };

        ListRank lr = new ListRank(parents);
        lr.solve();
        int[] distances = lr.getSolution();
        assertArrayEquals(expected, distances);
    }
}
