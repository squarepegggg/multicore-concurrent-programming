import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monkey {
    AtomicBoolean[] rope = new AtomicBoolean[3];

    ReentrantLock ropeLock = new ReentrantLock();
    Condition ropeEmpty = ropeLock.newCondition();
    Condition spotAvailable = ropeLock.newCondition();

    AtomicInteger count = new AtomicInteger(0);
    AtomicInteger currDirection = new AtomicInteger(-2);
    AtomicBoolean kongOnRope = new AtomicBoolean(false);
    ThreadLocal<Integer> spot = new ThreadLocal<>();

    public Monkey() {
        for (int i = 0; i < 3; i++) {
            rope[i] = new AtomicBoolean(false);
        }
    }
    public void ClimbRope(int direction) throws InterruptedException {
        //if count > 0 then we need to check to see if the current direction is ours or not
        ropeLock.lock();
        System.out.println(Thread.currentThread().getName() + " count=" + count.get() + " dir=" + direction);

        while (true) {
            int currentDirection = currDirection.get();
            if (currentDirection != direction && currentDirection != -2) {
                ropeEmpty.await();
            }
            else if (count.get() == 3) {
                spotAvailable.await();
            }
            else {
                break;
            }
        }

        if (count.get() == 0) {
            currDirection.set(direction);
            if (direction == -1) {
                kongOnRope.set(true);
            }
        }
        for (int i = 0; i < rope.length; i++) {
            if (!rope[i].get()) {
                rope[i].set(true);
                spot.set(i);
                break;
            }
        }
        count.incrementAndGet();

        ropeLock.unlock();
    }

    // After crossing the river, every monkey calls this method which
    // allows other monkeys to climb the rope.
    public void LeaveRope() {
        ropeLock.lock();

        rope[spot.get()].set(false);
        if (count.decrementAndGet() == 0) {
            if (currDirection.get() == -1) {
                kongOnRope.set(false);
            }
            currDirection.set(-2);
            ropeEmpty.signalAll();
        }
        else {
            spotAvailable.signalAll();
        }
        ropeLock.unlock();
    }
    /**
     * Returns the number of monkeys on the rope currently for test purpose.
     *
     * Positive Test Cases:
     * case 1: normal monkey (0 and 1)on the rope, this value should <= 3, >= 0
     * case 2: when Kong is on the rope, this value should be 1
     */
    public int getNumMonkeysOnRope() {
        return count.get();
    }
}