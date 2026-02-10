import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class Frequency {
    
    /**
     * Computes the frequency of x in array A using multiple threads
     * @param x The value to search for
     * @param A The array to search in
     * @param numThreads The number of threads to use for parallel processing
     * @return The total frequency of x in array A
     */
    public static int parallelFreq(int x, int[] A, int numThreads) {
        // Handle edge cases
        if (A == null || A.length == 0 || numThreads <= 0) {
            return 0;
        }
        
        numThreads = Math.min(numThreads, A.length);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);        // Create a thread pool
        List<Future<Integer>> futures = new ArrayList<>();        // List to hold Future objects
        int chunkSize = A.length / numThreads;        // Calculate the size of each chunk
        int remainder = A.length % numThreads;
        int startIndex = 0;

        for (int i = 0; i < numThreads; i++) {        // Create and submit tasks for each thread
            int currentChunkSize = chunkSize + (i < remainder ? 1 : 0);            // Calculate end index for this chunk
            int endIndex = startIndex + currentChunkSize;            // Distribute remainder among first few threads
            Callable<Integer> task = new FrequencyTask(x, A, startIndex, endIndex);            // Create a Callable task for this chunk            
            futures.add(executor.submit(task));            // Submit the task and store the Future            
            startIndex = endIndex;            // Update start index for next chunk
        }
        int totalFrequency = 0;        // Collect results from all threads
        try {
            for (Future<Integer> future : futures) {
                totalFrequency += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();            // Shutdown the executor
        }
        return totalFrequency;
    }
    
    /**
     * Callable task that computes frequency of a value in a subarray
     */
    private static class FrequencyTask implements Callable<Integer> {
        private final int target;
        private final int[] array;
        private final int startIndex;
        private final int endIndex;
        
        public FrequencyTask(int target, int[] array, int startIndex, int endIndex) {
            this.target = target;
            this.array = array;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
        
        @Override
        public Integer call() {
            int count = 0;
            for (int i = startIndex; i < endIndex; i++) {
                if (array[i] == target) {
                    count++;
                }
            }
            return count;
        }
    }
    
    // Test method
    public static void main(String[] args) {
        // Test case 1: Simple array
        int[] testArray1 = {1, 2, 3, 4, 5, 1, 2, 1, 3, 1};
        System.out.println("Test 1 - Frequency of 1 in array: " + 
                          parallelFreq(1, testArray1, 3));
        
        // Test case 2: Larger array
        int[] testArray2 = new int[1000];
        for (int i = 0; i < testArray2.length; i++) {
            testArray2[i] = i % 10;
        }
        System.out.println("Test 2 - Frequency of 5 in array of 1000 elements: " + 
                          parallelFreq(5, testArray2, 4));
        
        // Test case 3: Value not in array
        int[] testArray3 = {1, 2, 3, 4, 5};
        System.out.println("Test 3 - Frequency of 10 (not in array): " + 
                          parallelFreq(10, testArray3, 2));
        
        // Test case 4: All same values
        int[] testArray4 = {7, 7, 7, 7, 7, 7, 7, 7};
        System.out.println("Test 4 - Frequency of 7 in array of all 7s: " + 
                          parallelFreq(7, testArray4, 4));
    }
}
