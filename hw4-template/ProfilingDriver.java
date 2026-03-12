import java.util.*;

public class ProfilingDriver {

    private static final int TRIALS = 5;
    private static final int[] NS = {4, 8, 16, 32, 64, 128, 256};

    private static final int TASKS_PER_N = 4;
    private static final int TRIALS_PER_N = TASKS_PER_N * TRIALS;

    private static final int BAR_WIDTH = 30;

    // ANSI controls
    private static final String ANSI_CLEAR_LINE = "\u001B[2K"; // erase entire line
    private static final boolean ANSI_OK = isAnsiLikelySupported();

    public static void main(String[] args) {

        System.out.println("problem,n,median_ms");
        System.out.println();

        for (int n : NS) {
            Map<String, Result> results = new LinkedHashMap<>();
            int completedTrialsForN = 0;

            results.put("ParallelReduce+ParallelPrefix",
                    runTrialsForN(n, "R+P", completedTrialsForN, () -> {
                        int[] A = RandomInstances.randomIntArray(n);
                        ParallelReduce pr = new ParallelReduce(A);
                        pr.solve();
                        int[] S = pr.getSolution();
                        ParallelPrefix pp = new ParallelPrefix(A, S);
                        pp.solve();
                    }));
            completedTrialsForN += TRIALS;

            results.put("StableMarriage",
                    runTrialsForN(n, "SM", completedTrialsForN, () -> {
                        RandomInstances.StableMarriageStruct inst =
                                RandomInstances.randomStableMarriage(n);
                        StableMarriage sm = new StableMarriage(inst.mprefs, inst.wprefs);
                        sm.solve();
                    }));
            completedTrialsForN += TRIALS;

            results.put("ListRank",
                    runTrialsForN(n, "LR", completedTrialsForN, () -> {
                        int[] parent = RandomInstances.randomListRank(n);
                        ListRank lr = new ListRank(parent);
                        lr.solve();
                    }));
            completedTrialsForN += TRIALS;

            results.put("JobScheduling",
                    runTrialsForN(n, "JS", completedTrialsForN, () -> {
                        RandomInstances.JobSchedulingStruct inst =
                                RandomInstances.randomJobScheduling(n);
                        JobScheduling js = new JobScheduling(inst.time, inst.prerequisites);
                        js.solve();
                    }));
            completedTrialsForN += TRIALS;

            finishBarLine();

            printSummaryBlock(n, results);

            for (Map.Entry<String, Result> e : results.entrySet()) {
                System.out.println(e.getKey() + "," + n + "," + e.getValue().medianMs);
            }
            System.out.println();
        }

        System.out.println("DONE");
    }

    /* -------------------- Trials (per n) -------------------- */

    private static Result runTrialsForN(int n, String shortLabel, int baseCompletedTrialsForN, Runnable r) {
        long[] times = new long[TRIALS];

        for (int i = 0; i < TRIALS; i++) {
            times[i] = timeMillis(r);

            int done = baseCompletedTrialsForN + (i + 1);
            printBarForN(n, shortLabel, i + 1, done);
        }

        Arrays.sort(times);
        long median = times[times.length / 2];
        return new Result(median, times);
    }

    private static long timeMillis(Runnable r) {
        long t0 = System.nanoTime();
        r.run();
        long t1 = System.nanoTime();
        return (t1 - t0) / 1_000_000;
    }

    /* -------------------- Progress Bar -------------------- */

    private static void printBarForN(int n, String shortLabel, int trialIdx, int completedTrialsForN) {
        double progress = (double) completedTrialsForN / TRIALS_PER_N;
        int filled = (int) Math.round(BAR_WIDTH * progress);
        int pct = (int) Math.floor(progress * 100.0);

        StringBuilder bar = new StringBuilder();
        bar.append("n=").append(n)
           .append(" ").append(shortLabel)
           .append(" ").append(trialIdx).append("/").append(TRIALS)
           .append(" [");
        for (int i = 0; i < BAR_WIDTH; i++) bar.append(i < filled ? "=" : " ");
        bar.append("] ")
           .append(String.format("%3d%% (%2d/%2d)", pct, completedTrialsForN, TRIALS_PER_N));

        if (ANSI_OK) {
            // Clear line, return to column 0, write bar
            System.out.print("\r" + ANSI_CLEAR_LINE + "\r" + bar);
        } else {
            // Fallback: just print as separate lines (never mangles output)
            System.out.println("PROGRESS " + bar);
            return;
        }

        System.out.flush();
    }

    private static void finishBarLine() {
        if (ANSI_OK) {
            System.out.print("\r" + ANSI_CLEAR_LINE + "\r");
            System.out.flush();
        }
        System.out.println();
    }

    private static boolean isAnsiLikelySupported() {
        // Very conservative: VS Code terminal typically supports ANSI.
        // If the user pipes output to a file, ANSI makes things ugly, so disable in that case.
        if (System.console() == null) return false; // often null when redirected
        String term = System.getenv("TERM");
        if (term == null) return false;
        if ("dumb".equalsIgnoreCase(term)) return false;
        return true;
    }

    /* -------------------- Summary Printing -------------------- */

    private static void printSummaryBlock(int n, Map<String, Result> results) {
        System.out.println("============================================================");
        System.out.println("SUMMARY FOR n = " + n + "  (times in ms; median of " + TRIALS + ")");
        System.out.println("------------------------------------------------------------");

        for (Map.Entry<String, Result> e : results.entrySet()) {
            String problem = e.getKey();
            Result r = e.getValue();
            System.out.printf("  %-28s  median=%6d ms   trials=%s%n",
                    problem, r.medianMs, Arrays.toString(r.trialTimesSorted));
        }

        System.out.println("============================================================");
    }

    /* -------------------- Helper Class -------------------- */

    private static class Result {
        final long medianMs;
        final long[] trialTimesSorted;

        Result(long medianMs, long[] trialTimesSorted) {
            this.medianMs = medianMs;
            this.trialTimesSorted = trialTimesSorted;
        }
    }
}