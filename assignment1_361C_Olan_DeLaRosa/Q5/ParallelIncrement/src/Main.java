public class Main {
    public static void main(String[] args) {
    int[] threadCounts = {1, 2, 4, 8};

    System.out.println("Using Lamport's Bakery Algorithm");
    for (int n : threadCounts) {
        long start = System.nanoTime();
        int result = PIncrement.parallelIncrement(0, n);
        long end = System.nanoTime();

System.out.printf("Threads: %d | Result: %d | Time(ms): %.2f%n", 
                  n, result, (end - start) / 1_000_000.0);    }


    System.out.println("\nUsing CompareAndSet method");
    for (int n : threadCounts) {
        long start = System.nanoTime();
        int result = PIncrementB.parallelIncrement(0, n);
        long end = System.nanoTime();

System.out.printf("Threads: %d | Result: %d | Time(ms): %.2f%n", 
                  n, result, (end - start) / 1_000_000.0);    }


    System.out.println("\nSynchronized with Atomic Variables");
    for (int n : threadCounts) {
        long start = System.nanoTime();
        int result = PIncrementC.parallelIncrementSynchronizedAtomic(0, n);
        long end = System.nanoTime();

System.out.printf("Threads: %d | Result: %d | Time(ms): %.2f%n", 
                  n, result, (end - start) / 1_000_000.0);    }


    System.out.println("\nReentrant lock with Atomic Variables");
    for (int n : threadCounts) {
        long start = System.nanoTime();
        int result = PIncrementD.parallelIncrementReentrantLockAtomic(0, n);
        long end = System.nanoTime();

System.out.printf("Threads: %d | Result: %d | Time(ms): %.2f%n", 
                  n, result, (end - start) / 1_000_000.0);    }



    }
}