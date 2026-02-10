import java.util.concurrent.atomic.AtomicInteger;


public class PIncrementC {
    static class SynchronizedAtomicCounter {
        private AtomicInteger count;

        public SynchronizedAtomicCounter(int initialValue) {
            this.count = new AtomicInteger(initialValue);
        }

        public synchronized void increment() {
            int currentValue = count.get();
            int newValue = currentValue + 1;
            count.set(newValue);
        }
        public synchronized int get() {
            return count.get();
        }
    }
    public static int parallelIncrementSynchronizedAtomic(int c, int numThreads) {
        SynchronizedAtomicCounter counter = new SynchronizedAtomicCounter(c);
        Thread[] threads = new Thread[numThreads];
        int numIncrements = 1_200_000 / numThreads;
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int op = 0; op < numIncrements; op++) {
                    counter.increment();
                }
            });
        }
        
        for (Thread t : threads) {
            t.start();
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
