import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LLP {
    // Feel free to add any methods here. Common parameters (e.g. number of processes)
    // can be passed up through a super constructor. Your code will be tested by creating
    // an instance of a sub-class, calling the solve() method below, and then calling the
    // sub-class's getSolution() method. You are free to modify anything else as long as
    // you follow this API (see SimpleTest.java)
    int [] GlobalState;
    int numThreads;
    AtomicBoolean unsatisfiable = new AtomicBoolean(false);
    AtomicBoolean solved = new AtomicBoolean(false);
    AtomicBoolean anyForbidden = new AtomicBoolean(false);
    Thread[] threads;

    // Checks whether process j is forbidden in the state vector G
    public abstract boolean forbidden(int j);

    // Advances on process j
    public abstract void advance(int j);

    public void threadTask(int j, CyclicBarrier barrier) throws BrokenBarrierException, InterruptedException {
        while (!solved.get()) {
            boolean forbidden = false;
            forbidden = forbidden(j);
            //System.out.println("j: " + j + " G[j] " + GlobalSpace[j] + " forbidden: " + forbidden);
            barrier.await();

            if (forbidden) {
                anyForbidden.set(true);
                advance(j);
            }
            barrier.await();

            if (j == 0) {
                // System.out.println(Arrays.toString(GlobalSpace));
                if (!anyForbidden.get() || unsatisfiable.get()) {
                    solved.set(true);
                }
                anyForbidden.set(false);
            }
            barrier.await();
        }
    }

    public void initThreads(CyclicBarrier barrier) {
        threads = new Thread[numThreads + 1];
        for (int t = 0; t < numThreads; t++) {
            final int j = t;
            threads[t] = new Thread(() -> {
                try {
                    threadTask(j, barrier);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void solve() {
        // Implement this method. There are many ways to do this but you
        // should follow the following basic steps:
        // 1. Compute the forbidden states
        // 2. Advance on forbidden states in parallel
        // 3. Repeat 1 and 2 until there are no forbidden states
        CyclicBarrier barrier = new CyclicBarrier(numThreads);
        initThreads(barrier);

        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }

        // Threads execute, when join is done G[j] is populated
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (unsatisfiable.get()) {
            GlobalState = null;
        }
    }
}
