public class deployThreads {
    public static int deploy(int c, int numThreads) {
        Thread[] threads = new Thread[numThreads];
        AndersonLock lock = new AndersonLock(numThreads);
        int numIncrements = 120_000 / numThreads;
        SharedCounter sharedCounter = new SharedCounter(c);

        for (int i = 0; i < numThreads; i++) {
            AndersonIncrement t = new AndersonIncrement(lock, sharedCounter, numIncrements);
            threads[i] = new Thread(t);
            threads[i].start();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        return sharedCounter.value;
    }
}
