public class AndersonIncrement implements Runnable {
    AndersonLock mutex;
    SharedCounter sharedCounter;
    int numIncrements;

    public AndersonIncrement(AndersonLock lock, SharedCounter sharedCounter, int numIncrements) {
        this.mutex = lock;
        this.sharedCounter = sharedCounter;
        this.numIncrements = numIncrements;
    }

    @Override
    public void run() {
        for (int i = 0; i < numIncrements; i++) {
            mutex.lock();
            sharedCounter.value++;
            mutex.unlock();
        }
    }
}
