public class PIncrement {
    public static int parallelIncrement(int c, int numThreads) {
        Thread[] threads = new Thread[numThreads];
        Bakery lock = new Bakery(numThreads);
        int numIncrements = 1_200_000 / numThreads;
        SharedCounter sharedCounter = new SharedCounter(c);

        for (int i = 0; i < numThreads; i++) {
            BakeryIncrement bakeryThread = new BakeryIncrement(lock, sharedCounter, numIncrements, i);
            threads[i] = new Thread(bakeryThread);
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
