public class BakeryIncrement implements Runnable {
    Bakery lock;
    SharedCounter c;
    int numIncrements;
    int id;

    public BakeryIncrement(Bakery lock, SharedCounter c, int numIncrements, int id) {
        this.lock = lock;
        this.c = c;
        this.numIncrements = numIncrements;
        this.id = id;
    }

    @Override
    public void run() {
        for (int i = 0; i < numIncrements; i++) {
            lock.requestCS(id);
            c.value++;
            lock.releaseCS(id);
        }
    }
}
