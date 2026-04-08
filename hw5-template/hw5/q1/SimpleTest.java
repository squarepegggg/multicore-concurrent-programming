import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleTest {

    final int[][] ADJ_MATRIX1 = {
      // 0,  1,  2,  3,  4,  5,  6,  7,  8 (vertex labels)
        {0,  0,  0,  0,  0,  0,  0,  1,  0}, // 0
        {0,  0,  7,  9,  0,  0,  14, 0,  0}, // 1
        {0,  7,  0,  10, 15, 0,  0,  0,  0}, // 2
        {0,  9,  10, 0,  11, 0,  2,  0,  0}, // 3
        {0,  0,  15, 11, 0,  6,  0,  0,  0}, // 4
        {0,  0,  0,  0,  6,  0,  9,  0,  0}, // 5
        {0,  14, 0,  2,  0,  9,  0,  0,  0}, // 6
        {1,  0,  0,  0,  0,  0,  0,  0,  7}, // 7
        {0,  0,  0,  0,  0,  0,  0,  7,  0}  // 8
    };
    final int[][] ADJ_MATRIX2 = {
      // 0, 1, 2, 3 (vertex labels)
        {0, -5, 2, 3}, // 0
        {0, 0, 4, 0}, // 1
        {0, 0, 0, 1}, // 2
        {0, 0, 0, 0}
    };
    final int[][] ADJ_MATRIX3 = {
      // 0, 1, 2, 3, 4, 5, 6, 7 (vertex labels)
        {0, 0, 1, 0, 0, 1, 0, 0}, // 0
        {1, 0, 0, 0, 1, 0, 0, 0}, // 1
        {0, 0, 0, 0, 0, 1, 0, 0}, // 2
        {0, 0, 0, 0, 1, 0, 0, 1}, // 3
        {0, 1, 0, 1, 0, 0, 0, 0}, // 4
        {1, 0, 1, 0, 0, 0, 0, 0}, // 5
        {0, 0, 0, 0, 0, 0, 0, 1}, // 6
        {0, 0, 0, 1, 0, 0, 1, 0}  // 7
    };

    final int[] COMPONENTS = {8, 6, 6, 6, 6, 6, 6, 8, 8};
    final int[] SOURCE1_SPATH_COSTS = {Integer.MAX_VALUE, 0, 7, 9, 20, 20, 11, Integer.MAX_VALUE, Integer.MAX_VALUE};
    final int[] JOHNSON_PRICES = {0, 5, 1, 0};

    final int[] COMPONENTS2 = {5, 7, 5, 7, 7, 5, 7, 7};
    
    @Test
    public void testConnectedComponents() {
        ConnectedComponents cc = new ConnectedComponents(ADJ_MATRIX1);
        cc.solve();
        int[] components = cc.getSolution();
        assertArrayEquals(components, COMPONENTS);
    }

    @Test
    public void testConnectedComponents2() {
        ConnectedComponents cc = new ConnectedComponents(ADJ_MATRIX3);
        cc.solve();
        int[] components = cc.getSolution();
        assertArrayEquals(components, COMPONENTS2);
    }

    @Test
    public void testBellmanFord() {
        int source = 1;
        BellmanFord bf = new BellmanFord(ADJ_MATRIX1, source);
        bf.solve();
        int[] costs = bf.getSolution();
        assertArrayEquals(costs, SOURCE1_SPATH_COSTS);
    }
    
    @Test
    public void testJohnsons() {
        Johnsons johnsons = new Johnsons(ADJ_MATRIX2);
        johnsons.solve();
        int[] prices = johnsons.getSolution();
        assertArrayEquals(JOHNSON_PRICES, prices);
    }

    @Test
    public void testConnectedComponents_SingleVertex() {
        int[][] single = {{0}};
        ConnectedComponents cc = new ConnectedComponents(single);
        cc.solve();
        assertArrayEquals(new int[]{0}, cc.getSolution());
    }

    @Test
    public void testConnectedComponents_AllDisconnected() {
        // 4 vertices, no edges — each is its own component, leader = itself
        int[][] disconnected = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        ConnectedComponents cc = new ConnectedComponents(disconnected);
        cc.solve();
        assertArrayEquals(new int[]{0, 1, 2, 3}, cc.getSolution());
    }

    @Test
    public void testConnectedComponents_AllConnected() {
        // All 4 vertices in one component — largest label is 3
        int[][] allConnected = {
                {0, 1, 0, 0},
                {1, 0, 1, 0},
                {0, 1, 0, 1},
                {0, 0, 1, 0}
        };
        ConnectedComponents cc = new ConnectedComponents(allConnected);
        cc.solve();
        assertArrayEquals(new int[]{3, 3, 3, 3}, cc.getSolution());
    }

    @Test
    public void testConnectedComponents_TwoComponents() {
        // Component 1: {0,1,2}, Component 2: {3,4}
        int[][] twoComp = {
                {0, 1, 1, 0, 0},
                {1, 0, 1, 0, 0},
                {1, 1, 0, 0, 0},
                {0, 0, 0, 0, 1},
                {0, 0, 0, 1, 0}
        };
        ConnectedComponents cc = new ConnectedComponents(twoComp);
        cc.solve();
        assertArrayEquals(new int[]{2, 2, 2, 4, 4}, cc.getSolution());
    }

// ---- BellmanFord ----

    @Test
    public void testBellmanFord_SourceIsDestination() {
        // Distance from source to itself should be 0
        int source = 1;
        BellmanFord bf = new BellmanFord(ADJ_MATRIX1, source);
        bf.solve();
        assertEquals(0, bf.getSolution()[source]);
    }

    @Test
    public void testBellmanFord_UnreachableVertices() {
        // Vertices 7 and 8 are unreachable from vertex 1 in ADJ_MATRIX1
        int source = 1;
        BellmanFord bf = new BellmanFord(ADJ_MATRIX1, source);
        bf.solve();
        int[] costs = bf.getSolution();
        assertEquals(Integer.MAX_VALUE, costs[7]);
        assertEquals(Integer.MAX_VALUE, costs[8]);
    }

    @Test
    public void testBellmanFord_DifferentSource() {
        // Source = 0, only reachable vertex is 7 (weight 1) and 8 via 7 (weight 8)
        int source = 0;
        BellmanFord bf = new BellmanFord(ADJ_MATRIX1, source);
        bf.solve();
        int[] costs = bf.getSolution();
        assertEquals(0,              costs[0]);
        assertEquals(Integer.MAX_VALUE, costs[1]);
        assertEquals(Integer.MAX_VALUE, costs[2]);
        assertEquals(Integer.MAX_VALUE, costs[3]);
        assertEquals(Integer.MAX_VALUE, costs[4]);
        assertEquals(Integer.MAX_VALUE, costs[5]);
        assertEquals(Integer.MAX_VALUE, costs[6]);
        assertEquals(1,              costs[7]);
        assertEquals(8,              costs[8]);
    }

    @Test
    public void testBellmanFord_SingleVertex() {
        int[][] single = {{0}};
        BellmanFord bf = new BellmanFord(single, 0);
        bf.solve();
        assertArrayEquals(new int[]{0}, bf.getSolution());
    }

    @Test
    public void testBellmanFord_WithNegativeWeights() {
        int source = 0;
        BellmanFord bf = new BellmanFord(ADJ_MATRIX2, source);
        bf.solve();
        assertArrayEquals(new int[]{0,-5, -1, 0}, bf.getSolution());
    }

// ---- Johnson's ----

    @Test
    public void testJohnsons_AllPositiveWeights() {
        // No negative edges — price vector should stay all zeros
        int[][] allPos = {
                {0, 3, 0},
                {0, 0, 2},
                {0, 0, 0}
        };
        Johnsons j = new Johnsons(allPos);
        j.solve();
        assertArrayEquals(new int[]{0, 0, 0}, j.getSolution());
    }

    @Test
    public void testJohnsons_SingleNegativeEdge() {
        // 0->1 weight -3: needs p[1] >= p[0] - (-3) = p[0] + 3
        // p[0]=0, so p[1] must be >= 3
        int[][] mat = {
                {0, -3, 0},
                {0,  0, 0},
                {0,  0, 0}
        };
        Johnsons j = new Johnsons(mat);
        j.solve();
        int[] prices = j.getSolution();
        // Verify reduced costs are all >= 0
        // w'[0][1] = -3 + p[1] - p[0] >= 0  →  p[1] - p[0] >= 3
        assertTrue(prices[1] - prices[0] >= 3);
    }

    @Test
    public void testJohnsons_ReducedCostsNonNegative() {
        // After solving, verify ALL edges have non-negative reduced cost
        // w'[i][j] = w[i][j] + p[j] - p[i] >= 0
        Johnsons j = new Johnsons(ADJ_MATRIX2);
        j.solve();
        int[] p = j.getSolution();
        for (int i = 0; i < ADJ_MATRIX2.length; i++) {
            for (int k = 0; k < ADJ_MATRIX2[i].length; k++) {
                if (ADJ_MATRIX2[i][k] != 0) {
                    int reducedCost = ADJ_MATRIX2[i][k] + p[k] - p[i];
                    assertTrue(
                            "Reduced cost negative on edge " + i + "->" + k,
                            reducedCost >= 0
                    );
                }
            }
        }
    }

    @Test
    public void testJohnsons_SingleVertex() {
        int[][] single = {{0}};
        Johnsons j = new Johnsons(single);
        j.solve();
        assertArrayEquals(new int[]{0}, j.getSolution());
    }
}
