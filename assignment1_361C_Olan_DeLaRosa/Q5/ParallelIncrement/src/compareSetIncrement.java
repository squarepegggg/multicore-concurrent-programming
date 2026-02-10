import java.util.concurrent.atomic.AtomicInteger;

public class compareSetIncrement implements Runnable {
    AtomicInteger c;
    int numIncrements;

    public compareSetIncrement(AtomicInteger c, int numIncrements) {
        this.c = c;
        this.numIncrements = numIncrements;
    }

    @Override
    public void run() {
        for (int i = 0; i < numIncrements; i++) {
            boolean incremented = false;
            while (!incremented) {
                int expected = c.get();
                incremented = c.compareAndSet(expected, expected + 1);
            }
        }
    }
}
