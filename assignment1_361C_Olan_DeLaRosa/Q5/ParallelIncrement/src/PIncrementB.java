import java.util.concurrent.atomic.AtomicInteger;

public class PIncrementB {
    public static int parallelIncrement(int c, int numThreads) {
        Thread[] threads = new Thread[numThreads];
        int numIncrements = 1_200_000 / numThreads;
        AtomicInteger counter = new AtomicInteger(c);

        for (int i = 0; i < numThreads; i++) {
            compareSetIncrement compareSetThread = new compareSetIncrement(counter, numIncrements);
            threads[i] = new Thread(compareSetThread);
            threads[i].start();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return counter.get();
    }
}
