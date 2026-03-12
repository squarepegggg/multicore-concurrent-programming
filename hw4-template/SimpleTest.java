import org.junit.Test;
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
