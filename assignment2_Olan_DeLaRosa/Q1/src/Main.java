public class Main {
    public static void main(String[] args) {
        int[] threadCounts = {1, 2, 4, 8};
        System.out.println("Anderson Lock");

        for (int n : threadCounts) {
            long start = System.nanoTime();
            int result = deployThreads.deploy(0, n);
            long end = System.nanoTime();
            int time = (int) (end - start) / 1_000_000;

            System.out.printf("Threads: %d Result: %d Time(ms): %d%n", n, result, time);
        }

    }
}