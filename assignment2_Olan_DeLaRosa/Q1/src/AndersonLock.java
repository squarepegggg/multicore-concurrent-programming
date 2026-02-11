import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AndersonLock {
    AtomicInteger tailSlot = new AtomicInteger(0);
    AtomicBoolean[] available;
    ThreadLocal<Integer> mySlot;
    int numThreads;

    public AndersonLock(int n) {
        this.numThreads = n;
        mySlot = new ThreadLocal<>();

        available = new AtomicBoolean[n];
        available[0] = new AtomicBoolean(true);
        for (int i = 1; i < n; i++) {
            available[i] = new AtomicBoolean(false);
        }
    }

    public void lock() {
        int slot = tailSlot.getAndIncrement() % numThreads;
        mySlot.set(slot);
        while(!available[slot].get()) {
            Thread.onSpinWait();
        }
    }

    public void unlock() {
        int slot = mySlot.get();
        available[slot].set(false);
        available[(slot + 1) % numThreads].set(true);
    }
}
