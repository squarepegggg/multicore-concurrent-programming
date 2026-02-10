import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class PIncrementD {
    static class ReentrantLockAtomicCounter {
        private AtomicInteger count;
        private AtomicBoolean locked;  // Could be used to track lock state (not for actual locking)
        private ReentrantLock lock;
        
        public ReentrantLockAtomicCounter(int initial) {
            this.count = new AtomicInteger(initial);
            this.locked = new AtomicBoolean(false);
            this.lock = new ReentrantLock();
        }
        
        // ReentrantLock provides the mutual exclusion
        // AtomicInteger/AtomicBoolean just store the values safely
        public void increment() {
            lock.lock();
            try {
                // Optional: track that we're in critical section
                locked.set(true);
                
                // Read current value
                int currentValue = count.get();
                
                // Increment
                int newValue = currentValue + 1;
                
                // Write back
                count.set(newValue);
                
                // Optional: track that we're leaving critical section
                locked.set(false);
            } finally {
                lock.unlock();
            }
        }
        
        public int get() {
            lock.lock();
            try {
                return count.get();
            } finally {
                lock.unlock();
            }
        }
    }
    
    public static int parallelIncrementReentrantLockAtomic(int c, int numThreads) {
        ReentrantLockAtomicCounter counter = new ReentrantLockAtomicCounter(c);
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
