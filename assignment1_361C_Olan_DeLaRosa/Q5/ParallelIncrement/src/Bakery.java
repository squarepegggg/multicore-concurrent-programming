import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Bakery {
    int N;
    AtomicBoolean[] choosing;
    AtomicInteger[] number;

    public Bakery(int numProcesses) {
        N = numProcesses;
        choosing = new AtomicBoolean[N];
        number = new AtomicInteger[N];
        for (int i = 0; i < N; i++) {
            choosing[i] = new AtomicBoolean(false);
            number[i] = new AtomicInteger(0);
        }
    }

    public void requestCS(int i) {
        choosing[i].set(true);
        for (int j = 0; j < N; j++) {
            if (number[j].get() > number[i].get()) {
                number[i].set(number[j].get());
            }
        }
        int iNum = number[i].get() + 1;
        number[i].set(iNum);
        choosing[i].set(false);

        for (int j = 0; j < N; j++) {
            while(choosing[j].get());
            int jNum = number[j].get();
            while ((jNum != 0) && ((jNum < iNum) || ((jNum == iNum) && j < i))) {
                jNum = number[j].get();
            }
        }
    }

    public void releaseCS(int i) {
        number[i].set(0);
    }
}
