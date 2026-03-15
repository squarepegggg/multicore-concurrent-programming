public class JobScheduling extends LLP {
    private int[] time;
    private int[][] prerequisites;

    // time[i] = duration of job i
    // prerequisites[i] = list of jobs that must complete before job i
    public JobScheduling(int[] time, int[][] prerequisites) {
        super();
        this.time = time;
        this.prerequisites = prerequisites;
        this.numThreads = time.length;
        this.GlobalSpace = new int[time.length];

        // Initialize each job's completion time to its own duration (no deps assumed)
        for (int i = 0; i < time.length; i++) {
            GlobalSpace[i] = time[i];
        }
    }

    @Override
    public boolean forbidden(int j) {
        // Job j is forbidden if its current completion time is wrong —
        // i.e., some prerequisite finishes AFTER our current estimate minus own time
        int minStart = 0;
        for (int prereq : prerequisites[j]) {
            minStart = Math.max(minStart, GlobalSpace[prereq]);
        }
        // Correct completion = latest prereq finish + own duration
        return GlobalSpace[j] < minStart + time[j];
    }

    @Override
    public void advance(int j) {
        // Set completion time to: max completion of all prerequisites + own duration
        int minStart = 0;
        for (int prereq : prerequisites[j]) {
            minStart = Math.max(minStart, GlobalSpace[prereq]);
        }
        GlobalSpace[j] = minStart + time[j];
    }

    // Called after solve()
    public int[] getSolution() {
        return GlobalSpace;
    }
}